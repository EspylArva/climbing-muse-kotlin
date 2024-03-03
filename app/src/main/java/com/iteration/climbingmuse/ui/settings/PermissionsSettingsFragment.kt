package com.iteration.climbingmuse.ui.settings

import android.Manifest
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.iteration.climbingmuse.R
import com.iteration.climbingmuse.databinding.FragmentPermissionsSettingsBinding
import com.iteration.climbingmuse.databinding.PermissionCardViewBinding

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
        val vm = ViewModelProvider(this).get(SettingsViewModel::class.java)


        _binding = FragmentPermissionsSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        


        return root
    }

    override fun onStart() {
        super.onStart()
        val recyclerPermissions = binding.recyclerPermissions.apply {
            adapter = PermissionCardAdapter(listOf(
                PermissionCardInfo(requireParentFragment(), "Camera", Manifest.permission.CAMERA).apply {
                    icon = R.drawable.ic_baseline_photo_camera_24
                    description = "Please give me access to camera, I shoot nice vids"
                },
                PermissionCardInfo(requireParentFragment(), "File Access", Manifest.permission.CAMERA).apply {
                    icon = R.drawable.ic_baseline_drive_file_move_24
                    description = "Should not be required yet, I'm gonna need to take a look at your vids soon tho"
                } // FIXME: should require file access
                       ))
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            // Decorations
            PagerSnapHelper().attachToRecyclerView(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

