package com.iteration.climbingmuse.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.iteration.climbingmuse.databinding.FragmentComputerVisionSettingsBinding

class ComputerVisionSettingsFragment : Fragment() {

    private var _binding: FragmentComputerVisionSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val vm = ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentComputerVisionSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val modelDropdown = binding.menuModel
        setupDropdown(modelDropdown)


        return root
    }

    private fun setupDropdown(modelDropdown: TextInputLayout) {
        (modelDropdown.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(arrayOf("Item A", "Item B", "Item C"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}