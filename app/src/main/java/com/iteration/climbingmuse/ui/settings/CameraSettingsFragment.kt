package com.iteration.climbingmuse.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.iteration.climbingmuse.R
import com.iteration.climbingmuse.databinding.FragmentCameraSettingsBinding
import timber.log.Timber

class CameraSettingsFragment : Fragment() {

    private var _binding: FragmentCameraSettingsBinding? = null

    private val vm: CameraViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera_settings, container, false)
        binding.lifecycleOwner = this;
        binding.viewmodel = vm
        return binding.root

    }

    override fun onResume() {
        setupDropdown(binding.menuCamera)
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupDropdown(modelDropdown: MaterialAutoCompleteTextView) {
        Timber.d("CameraDropDown should have these items selectable: ${resources.getStringArray(CameraViewModel.AVAILABLE_CAMERAS_ARRAY_ID).toList()}. Selected item should be ${CameraViewModel.Converter.getCameraName(vm.cameraSelection.value!!)}")
        val selectableValues = resources.getStringArray(CameraViewModel.AVAILABLE_CAMERAS_ARRAY_ID)
        modelDropdown.setSimpleItems(selectableValues)
    }
}