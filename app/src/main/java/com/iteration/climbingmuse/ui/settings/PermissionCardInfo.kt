package com.iteration.climbingmuse.ui.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.properties.Delegates

class PermissionCardInfo(val fragment: Fragment, val title: String, private val permission: String) {
    lateinit var description: String
    var icon by Delegates.notNull<Int>()
    fun getPermission() {
//            when {
//                ContextCompat.checkSelfPermission(fragment.requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
//                    // You can use the API that requires the permission.
//                }
//
//                ActivityCompat.shouldShowRequestPermissionRationale(fragment.requireActivity(), permission) -> {
//                    // In an educational UI, explain to the user why your app requires this
//                    // permission for a specific feature to behave as expected, and what
//                    // features are disabled if it's declined. In this UI, include a
//                    // "cancel" or "no thanks" button that lets the user continue
//                    // using your app without granting the permission.
//                }
//
//                else -> {
//                    requestPermissionLauncher.launch(permission)
//                }
//            }
        Toast.makeText(fragment.requireContext(), "Feature not available yet", Toast.LENGTH_SHORT).show()
    }

    fun revokePermission() {
        Toast.makeText(fragment.requireContext(), "Feature not available yet", Toast.LENGTH_SHORT).show()
    }

//    private val requestPermissionLauncher = fragment.requireActivity().registerForActivityResult(ActivityResultContracts.RequestPermission()) {
//        isGranted : Boolean ->
//        if (isGranted) {
//            // Do stuff
//        } else {
//            // Do other stuff
//        }
//    }

    private val _authorized = MutableLiveData<Boolean>().apply { value = false }
    val authorized: LiveData<Boolean> = _authorized
}