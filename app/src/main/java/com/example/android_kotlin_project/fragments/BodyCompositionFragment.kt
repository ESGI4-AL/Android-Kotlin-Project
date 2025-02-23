package com.example.android_kotlin_project.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.databinding.FragmentBodyCompositionBinding
import com.example.android_kotlin_project.databinding.HeightCardBinding
import com.example.android_kotlin_project.databinding.WeightCardBinding
import com.example.android_kotlin_project.utils.HealthSaveStatus
import com.example.android_kotlin_project.viewmodels.HealthViewModel
import com.example.android_kotlin_project.views.BmiView

class BodyCompositionFragment : Fragment() {
    private lateinit var healthViewModel: HealthViewModel

    private var _fragmentBinding: FragmentBodyCompositionBinding? = null
    private val fragmentBinding get() = _fragmentBinding!!

    private var _heightCardBinding: HeightCardBinding? = null
    private val heightCardBinding get() = _heightCardBinding!!

    private var _weightCardBinding: WeightCardBinding? = null
    private val weightCardBinding get() = _weightCardBinding!!

    private var _bmiCardBinding: BmiView? = null
    private val bmiCardBinding get() = _bmiCardBinding!!

    companion object {
        private const val MIN_HEIGHT = 1
        private const val MAX_HEIGHT = 350
        private const val MIN_WEIGHT = 1
        private const val MAX_WEIGHT = 500
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentBinding = FragmentBodyCompositionBinding.inflate(inflater, container, false)

        val rootView = fragmentBinding.root
        _heightCardBinding = HeightCardBinding.bind(rootView.findViewById(R.id.height_card))
        _weightCardBinding = WeightCardBinding.bind(rootView.findViewById(R.id.weight_card))
        _bmiCardBinding = BmiView(rootView.findViewById(R.id.bmi_calculator_card))

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        healthViewModel = ViewModelProvider(requireActivity())[HealthViewModel::class.java]

        healthViewModel.resetSaveStatus()

        healthViewModel.loadBodyComposition()

        fragmentBinding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        setupNumberPickers()
        setupClickListeners()
        observeViewModelData()
    }

    /**
     * Setup NumberPickers for height and weight
     */
    private fun setupNumberPickers() {
        with(heightCardBinding.heightPicker) {
            minValue = MIN_HEIGHT
            maxValue = MAX_HEIGHT
            wrapSelectorWheel = false
        }

        with(weightCardBinding.weightPicker) {
            minValue = MIN_WEIGHT
            maxValue = MAX_WEIGHT
            wrapSelectorWheel = false
        }
    }

    /**
     * Setup click listeners for back and save buttons
     */
    private fun setupClickListeners() {
        fragmentBinding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        fragmentBinding.saveButton.setOnClickListener {
            saveBodyComposition()
        }
    }

    /**
     * Observe changes in height, weight, and save status
     */
    private fun observeViewModelData() {
        // Observe height changes
        healthViewModel.height.observe(viewLifecycleOwner) { height ->
            val heightValue = height.toIntOrNull() ?: 175
            if (heightValue in MIN_HEIGHT..MAX_HEIGHT) {
                heightCardBinding.heightPicker.value = heightValue
            }
        }

        // Observe weight changes
        healthViewModel.weight.observe(viewLifecycleOwner) { weight ->
            val weightValue = weight.toIntOrNull() ?: 65
            if (weightValue in MIN_WEIGHT..MAX_WEIGHT) {
                weightCardBinding.weightPicker.value = weightValue
            }
        }

        // Observe save status
        healthViewModel.saveStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                HealthSaveStatus.SAVING -> {
                    fragmentBinding.progressBar.visibility = View.VISIBLE
                    fragmentBinding.saveButton.isEnabled = false
                }
                HealthSaveStatus.SUCCESS -> {
                    fragmentBinding.progressBar.visibility = View.GONE
                    fragmentBinding.saveButton.isEnabled = true
                    Toast.makeText(context, "Body composition saved successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                HealthSaveStatus.ERROR -> {
                    fragmentBinding.progressBar.visibility = View.GONE
                    fragmentBinding.saveButton.isEnabled = true
                    Toast.makeText(context, "Failed to save data. Please try again.", Toast.LENGTH_SHORT).show()
                }
                HealthSaveStatus.IDLE -> {
                    fragmentBinding.progressBar.visibility = View.GONE
                    fragmentBinding.saveButton.isEnabled = true
                }
            }
        }

        // update bmi
        healthViewModel.apply {
            height.observe(viewLifecycleOwner) { height ->
                updateBmi()
            }
            weight.observe(viewLifecycleOwner) { weight ->
                updateBmi()
            }
        }
    }

    /**
     * Update BMI value based on height and weight
     */
    private fun updateBmi() {
        val height = heightCardBinding.heightPicker.value.toFloat() / 100
        val weight = weightCardBinding.weightPicker.value.toFloat()

        if (height > 0) {
            val bmi = weight / (height * height)
            bmiCardBinding.updateBmiDisplay(bmi)
        }
    }

    /**
     * Saves body composition data with NumberPicker values
     */
    private fun saveBodyComposition() {
        val heightValue = heightCardBinding.heightPicker.value.toString()
        val weightValue = weightCardBinding.weightPicker.value.toString()

        val currentHeight = healthViewModel.height.value ?: "0"
        val currentWeight = healthViewModel.weight.value ?: "0"

        if (heightValue == currentHeight && weightValue == currentWeight) {
            Toast.makeText(context, "No changes detected", Toast.LENGTH_SHORT).show()
            return
        }

        healthViewModel.saveBodyComposition(heightValue, weightValue)
    }

    /**
     * Clean up the view
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _heightCardBinding = null
        _weightCardBinding = null
        _fragmentBinding = null
    }
}
