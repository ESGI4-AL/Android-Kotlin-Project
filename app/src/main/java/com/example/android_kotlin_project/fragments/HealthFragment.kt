package com.example.android_kotlin_project.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.databinding.BodyCompositionCardBinding
import com.example.android_kotlin_project.databinding.DailyStepsCardBinding
import com.example.android_kotlin_project.databinding.FragmentHealthBinding
import com.example.android_kotlin_project.databinding.HeartRateCardBinding
import com.example.android_kotlin_project.databinding.OxygenCardBinding
import com.example.android_kotlin_project.viewmodels.HealthViewModel
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch

class HealthFragment : Fragment() {
    private lateinit var healthViewModel: HealthViewModel
    private lateinit var dailyStepsTextView: TextView
    private lateinit var heartRateTextView: TextView
    private lateinit var lineChart: LineChart
    private lateinit var oxygenLevelTextView: TextView
    private lateinit var oxygenBulletChart: HorizontalBarChart
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var _fragmentBinding: FragmentHealthBinding? = null
    private val fragmentBinding get() = _fragmentBinding!!

    private var _heartRateBinding: HeartRateCardBinding? = null
    private val heartRateBinding get() = _heartRateBinding!!

    private var _dailyStepsCardBinding: DailyStepsCardBinding? = null
    private val dailyStepsCardBinding get() = _dailyStepsCardBinding!!

    private var _oxygenLevelCardBinding: OxygenCardBinding? = null
    private val oxygenLevelCardBinding get() = _oxygenLevelCardBinding!!

    private var _bodyCompositionCardBinding: BodyCompositionCardBinding? = null
    private val bodyCompositionCardBinding get() = _bodyCompositionCardBinding!!

    /**
     * Create the view
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentBinding = FragmentHealthBinding.inflate(inflater, container, false)
        _heartRateBinding = HeartRateCardBinding.bind(fragmentBinding.root.findViewById(R.id.heart_rate_card))
        _dailyStepsCardBinding = DailyStepsCardBinding.bind(fragmentBinding.root.findViewById(R.id.daily_steps_card))
        _oxygenLevelCardBinding = OxygenCardBinding.bind(fragmentBinding.root.findViewById(R.id.oxygen_card))

        swipeRefreshLayout = fragmentBinding.root.findViewById(R.id.swipeRefreshLayout)

        _bodyCompositionCardBinding = BodyCompositionCardBinding.bind(
            fragmentBinding.root.findViewById(R.id.body_composition_card)
        )

        swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
        return fragmentBinding.root
    }

    /**
     * Set up the view
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        heartRateTextView = heartRateBinding.heartRateData
        lineChart = heartRateBinding.heartRateLineChart
        dailyStepsTextView = dailyStepsCardBinding.dailyStepsData
        oxygenLevelTextView = oxygenLevelCardBinding.oxygenData

        setupChart()

        // Get the view model
        healthViewModel = ViewModelProvider(requireActivity())[HealthViewModel::class.java]

        // Observe last heart rate data
        healthViewModel.lastHeartRate.observe(viewLifecycleOwner) { heartRate ->
            updateLastHeartRate(heartRate)
        }

        // Observe heart rate data
        healthViewModel.heartRateData.observe(viewLifecycleOwner) { heartRateData ->
            updateHeartRateLineChart(heartRateData)
        }

        // Observe daily steps data
        healthViewModel.dailySteps.observe(viewLifecycleOwner) { dailySteps ->
            updateDailySteps(dailySteps)
        }

        oxygenBulletChart = oxygenLevelCardBinding.oxygenBulletChart
        setupOxygenBulletChart()

        // Observe oxygen level data
        healthViewModel.oxygenLevel.observe( viewLifecycleOwner) { oxygenLevel ->
            updateOxygenLevel(oxygenLevel)
            updateOxygenBulletChart(oxygenLevel.replace("%", "").toFloat())
        }

        // Observe body composition data
        healthViewModel.height.observe(viewLifecycleOwner) { height ->
            updateHeight(height)
        }

        healthViewModel.weight.observe(viewLifecycleOwner) { weight ->
            updateWeight(weight)
        }

        // Navigate to body composition fragment
        bodyCompositionCardBinding.bodyCompositionCard.setOnClickListener {
            healthViewModel.resetSaveStatus()
            findNavController().navigate(R.id.bodyCompositionFragment)
        }

        refreshData()

    }

    /**
     * Update the daily steps with new data
     */
    private fun updateDailySteps(dailySteps: String) {
        dailyStepsTextView.text = dailySteps
    }


    /**
     * Update the last heart rate with new data
     */
    private fun updateLastHeartRate(heartRate: String) {
        heartRateTextView.text = heartRate
    }

    /**
     * Set up the chart
     */
    private fun setupChart() {
        lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)

            setPinchZoom(true)
            isDoubleTapToZoomEnabled = true

            legend.isEnabled = false

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(true)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val hours = (value / 3600).toInt()
                        val minutes = ((value % 3600) / 60).toInt()
                        return String.format("%02d:%02d", hours, minutes)
                    }
                }
                textSize = 10f
                labelRotationAngle = -45f
            }

            axisLeft.apply {
                setDrawGridLines(true)
                textSize = 10f
                axisMinimum = 40f
                axisMaximum = 200f
            }

            // Disable right Y-axis
            axisRight.isEnabled = false
        }
    }

    /**
     * Update the line chart with new data
     */
    private fun updateHeartRateLineChart(data: List<Pair<Float, Float>>) {
        if (data.isEmpty()) return

        val entries = data.map { (timeInSeconds, bpm) ->
            Entry(timeInSeconds, bpm)
        }


        val dataSet = LineDataSet(entries, "").apply {
            color = ContextCompat.getColor(requireContext(), R.color.red_heart)
            setCircleColor(color)
            lineWidth = 2f
            circleRadius = 4f
            setDrawCircleHole(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(false)
        }

        lineChart.data = LineData(dataSet)

        // Calculate visible range for last 15 minutes
        val lastTimePoint = entries.maxByOrNull { it.x }?.x ?: 0f
        val fifteenMinutesInSeconds = 10 * 60f

        // Visible range
        lineChart.apply {
            setVisibleXRangeMaximum(fifteenMinutesInSeconds)

            // Zoom on the last 15 minutes of data
            moveViewToX(lastTimePoint - fifteenMinutesInSeconds)

            xAxis.spaceMin = 50f
            xAxis.spaceMax = 50f

            animateX(1000)
            invalidate()
        }
    }

    /**
     * Update the oxygen level with new data
     */
    private fun updateOxygenLevel(oxygenLevel: String) {
        oxygenLevelTextView.text = oxygenLevel
    }

    /**
     * Set up the oxygen bullet chart
     */
    private fun setupOxygenBulletChart() {
        oxygenBulletChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            isDragEnabled = false
            setScaleEnabled(false)

            xAxis.apply {
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
            }

            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 100f
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
            }

            axisRight.isEnabled = false

            setDrawBorders(false)

            // Resets the padding values to 0
            minOffset = 0f
            extraBottomOffset = 0f
            extraTopOffset = 0f
            setViewPortOffsets(0f, 0f, 0f, 0f)
        }
    }


    /**
     * Update the oxygen bullet chart with new data
     */
    private fun updateOxygenBulletChart(oxygenLevel: Float) {
        val entries = listOf(BarEntry(0f, oxygenLevel))

        // Set the color based on the oxygen level
        val dataSet = BarDataSet(entries, "").apply {
            color = when {
                oxygenLevel >= 95f -> ContextCompat.getColor(requireContext(), R.color.blue_oxygen)
                oxygenLevel >= 90f -> ContextCompat.getColor(requireContext(), R.color.yellow_warning)
                else -> ContextCompat.getColor(requireContext(), R.color.red_danger)
            }
            setDrawValues(false)
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.75f
        }

        oxygenBulletChart.apply {
            data = barData
            animateY(500)
            invalidate()
        }
    }

    /**
     * Update height data
     */
    private fun updateHeight(height: String) {
        bodyCompositionCardBinding.heightData.text = height
    }

    /**
     * Update weight data
     */
    private fun updateWeight(weight: String) {
        bodyCompositionCardBinding.weightData.text = weight
    }

    /**
     * Clean up the view
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentBinding = null
        _heartRateBinding = null
        _dailyStepsCardBinding = null
        _oxygenLevelCardBinding = null
        _bodyCompositionCardBinding = null
    }

    /**
     * Refresh the data when swiping down
     */
    private fun refreshData() {
        swipeRefreshLayout.isRefreshing = true

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                healthViewModel.fetchHeartRateData()
                healthViewModel.fetchLastHeartRate()
                healthViewModel.fetchDailySteps()
                healthViewModel.fetchOxygenLevel()
                healthViewModel.loadBodyComposition()
            } catch (e: Exception) {
                Log.e("HealthFragment", "Error refreshing data", e)
            } finally {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}
