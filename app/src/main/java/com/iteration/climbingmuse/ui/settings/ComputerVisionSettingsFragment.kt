package com.iteration.climbingmuse.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.iteration.climbingmuse.R
import com.iteration.climbingmuse.analysis.PoseLandmarkerHelper
import com.iteration.climbingmuse.databinding.FragmentComputerVisionSettingsBinding
import com.iteration.climbingmuse.ui.MaterialViewHelper
import com.iteration.climbingmuse.ui.MaterialViewHelper.Companion.setSubCheckboxes
import timber.log.Timber

class ComputerVisionSettingsFragment : Fragment() {

    private var _binding: FragmentComputerVisionSettingsBinding? = null
    private val vm: SettingsViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_computer_vision_settings, container, false)
        binding.lifecycleOwner = this;
        binding.viewmodel = vm
        Timber.d("SettingsVM hash: %s", vm.hashCode())
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setupDropdown(binding.menuModel)
        setupCheckboxes()
    }

    private fun setupDropdown(modelDropdown: MaterialAutoCompleteTextView) {
        modelDropdown.setSimpleItems(R.array.models_spinner_titles)
    }

    @SuppressWarnings("unchecked")
    private fun setupCheckboxes() {
        //val angles = binding.checkAngleDecorator

        val centerOfGravity = binding.checkGravityCenterDecorator
        val cogChildren = binding.cogSubchecksContainer.children as Sequence<MaterialCheckBox>
        centerOfGravity.setSubCheckboxes(cogChildren)

        //val joints = binding.checkJointDecorator

        val muscles = binding.checkMuscleDecorator
        val musclesChildren = binding.musclesSubchecksContainer.children as Sequence<MaterialCheckBox>
        muscles.setSubCheckboxes(musclesChildren)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}