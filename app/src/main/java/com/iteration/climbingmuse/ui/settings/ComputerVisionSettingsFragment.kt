package com.iteration.climbingmuse.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.MaterialAutoCompleteTextView
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
//        val notificationsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentComputerVisionSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val dropdown = binding.menuModel

        (dropdown.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(arrayOf("Item A", "Item B", "Item C"))


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}