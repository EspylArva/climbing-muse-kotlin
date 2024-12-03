package com.iteration.climbingmuse.ui.settings

import android.content.Context
import android.opengl.Visibility
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

        binding.cameraResetButton.setOnClickListener {
            Timber.d("== onClick ==")
            Timber.d("   . Reset")
            vm.resetParams()
            Timber.d("   . Dropdown setup")
            setupDropdown(binding.menuCamera)
        }
        binding.showListOfChipsBtn.setOnClickListener {
            binding.chipsContainer.visibility = if(binding.chipsContainer.visibility == View.GONE) View.VISIBLE else View.GONE
        }
        observe()
        return binding.root
    }

    private fun observe() {
        val sp = requireContext().getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE)

        /// Camera settings
        vm.cameraSelection.observe(viewLifecycleOwner) { sp.edit().putInt(resources.getString(R.string.sp_camera_cameraSelection), it).apply() }
        vm.computerVisionActivated.observe(viewLifecycleOwner) { sp.edit().putBoolean(resources.getString(R.string.sp_camera_cvActivated), it).apply() }
        vm.cameraAction.observe(viewLifecycleOwner) { sp.edit().putString(resources.getString(R.string.sp_camera_cameraAction), it.toString()).apply() }
        vm.chipVisibility.observe(viewLifecycleOwner) { sp.edit().putString(resources.getString(R.string.sp_camera_chipVisibility), it.toString()).apply() }

        vm.recordChip.observe(viewLifecycleOwner) { sp.edit().putBoolean(resources.getString(R.string.sp_camera_recordChipVisibility), it).apply() }
        vm.cvChip.observe(viewLifecycleOwner) { sp.edit().putBoolean(resources.getString(R.string.sp_camera_cvChipVisibility), it).apply() }
        vm.chipVisibilityChip.observe(viewLifecycleOwner) { sp.edit().putBoolean(resources.getString(R.string.sp_camera_showChipsChipVisibility), it).apply() }
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
        Timber.d("CameraDropDown should have these items selectable: ${resources.getStringArray(R.array.camera_spinner_titles).toList()}. Selected item should be ${CameraViewModel.Converter.getCameraName(vm.cameraSelection.value!!)}")
        val selectableValues = resources.getStringArray(R.array.camera_spinner_titles)
        modelDropdown.setSimpleItems(selectableValues)
    }
}