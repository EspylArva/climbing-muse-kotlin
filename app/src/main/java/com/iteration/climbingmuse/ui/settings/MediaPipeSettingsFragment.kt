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
import com.iteration.climbingmuse.databinding.FragmentMediaPipeSettingsBinding
import timber.log.Timber

class MediaPipeSettingsFragment : Fragment() {
    private var _binding: FragmentMediaPipeSettingsBinding? = null

    private val vm: SettingsViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_media_pipe_settings, container, false)
        binding.lifecycleOwner = this;
        binding.viewmodel = vm
        return binding.root

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