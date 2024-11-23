package com.example.android_kotlin_project.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.databinding.DailyStepsCardBinding
import com.example.android_kotlin_project.databinding.FragmentHealthBinding
import com.example.android_kotlin_project.databinding.HeartRateCardBinding
import com.example.android_kotlin_project.viewmodels.HealthViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

class HealthFragment : Fragment() {
    private lateinit var healthViewModel: HealthViewModel
    private lateinit var dailyStepsTextView: TextView
    private lateinit var heartRateTextView: TextView
    private lateinit var lineChart: LineChart
    private var _fragmentBinding: FragmentHealthBinding? = null
    private val fragmentBinding get() = _fragmentBinding!!

    private var _heartRateBinding: HeartRateCardBinding? = null
    private val heartRateBinding get() = _heartRateBinding!!

    private var _dailyStepsCardBinding: DailyStepsCardBinding? = null
    private val dailyStepsCardBinding get() = _dailyStepsCardBinding!!

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
     * Clean up the view
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentBinding = null
        _heartRateBinding = null
        _dailyStepsCardBinding = null
    }
}
