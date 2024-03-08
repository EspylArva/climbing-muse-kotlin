package com.iteration.climbingmuse.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.iteration.climbingmuse.R
import timber.log.Timber

class PermissionsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireCamera()
    }

    companion object {
        fun hasPermissions(context: Context, permission: String) = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("CommitPrefEdits")
    private fun requireCamera() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                // Navigate back to camera
                navigateToCamera()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Permission to access Camera")
                    .setMessage(resources.getString(R.string.camera_permission_justification))
                    .setPositiveButton(resources.getString(R.string.button_give_permission)) { dialog, which ->
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                    .setNegativeButton(resources.getString(R.string.button_refuse_permission)) { dialog, which ->
                        // Set shared preferences, so that this permission is not asked again
                        requireContext().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
                            .edit().putBoolean(getString(R.string.camera_permission_denied), true).apply()
                        navigateToCamera()

                    }
                    .show()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun navigateToCamera() {
        Timber.d("Back to camera")
        lifecycleScope.launchWhenStarted {
            Navigation.findNavController(
                requireActivity(),R.id.nav_host_fragment_activity_main
            ).navigate(R.id.action_navigation_permissions_to_navigation_home)
        }
    }



    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()) { isGranted : Boolean ->
                if (isGranted) {
                    Snackbar.make(requireActivity().findViewById(R.id.nav_host_fragment_activity_main), R.string.permission_granted, Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(requireActivity().findViewById(R.id.nav_host_fragment_activity_main), R.string.permission_denied, Snackbar.LENGTH_SHORT).show()
                }
                navigateToCamera()
            }
}
