package com.iteration.climbingmuse.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.iteration.climbingmuse.R
import com.iteration.climbingmuse.databinding.FragmentMediaPipeSettingsBinding
import timber.log.Timber

class MediaPipeSettingsFragment : Fragment() {
    private var _binding: FragmentMediaPipeSettingsBinding? = null

    private val vm: MediaPipeViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_media_pipe_settings, container, false)
        binding.viewmodel = vm
        binding.lifecycleOwner = this

        binding.mediapipeResetButton.setOnClickListener {
            vm.resetParams()
            setupDropdown(binding.menuModel)
        }

        observe()
        return binding.root
    }

    private fun observe() {
        val sp = requireContext().getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE)

        /// MediaPipe settings
        vm.model.observe(viewLifecycleOwner) { sp.edit().putString(resources.getString(R.string.sp_mediapipe_model), it).apply() }
        vm.detectionThreshold.observe(viewLifecycleOwner) { sp.edit().putFloat(resources.getString(R.string.sp_mediapipe_detection_threshold), it).apply() }
        vm.trackableThreshold.observe(viewLifecycleOwner) { sp.edit().putFloat(resources.getString(R.string.sp_mediapipe_trackable_threshold), it).apply() }
        vm.presenceThreshold.observe(viewLifecycleOwner) { sp.edit().putFloat(resources.getString(R.string.sp_mediapipe_presence_threshold), it).apply() }
        vm.delegatePU.observe(viewLifecycleOwner) { sp.edit().putInt(resources.getString(R.string.sp_mediapipe_delegate), it).apply() }
    }

    override fun onResume() {
        super.onResume()
        setupDropdown(binding.menuModel)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupDropdown(modelDropdown: MaterialAutoCompleteTextView) {
        Timber.d("ModelDropDown should have these items selectable: ${resources.getStringArray(R.array.models_spinner_titles).toList()}. Selected item should be ${vm.model.value}")
        modelDropdown.setSimpleItems(R.array.models_spinner_titles)
    }
}