package com.iteration.climbingmuse.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.iteration.climbingmuse.databinding.FragmentPermissionsSettingsBinding

class PermissionsSettingsFragment : Fragment() {

    private var _binding: FragmentPermissionsSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val notificationsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentPermissionsSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
