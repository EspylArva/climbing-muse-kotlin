@file:JvmName("BindingUtils")
package com.iteration.climbingmuse.ui.settings

import androidx.databinding.InverseMethod
import com.iteration.climbingmuse.R

// CameraSettings
@InverseMethod("buttonIdToCameraAction")
fun cameraActionToButtonId(delegate: CameraViewModel.Companion.CameraAction): Int {
    val selectedButtonId = when (delegate) {
        CameraViewModel.Companion.CameraAction.RECORD_VIDEO -> R.id.radio_take_video
        CameraViewModel.Companion.CameraAction.TAKE_PICTURE -> R.id.radio_take_photo
    }

    return selectedButtonId
}

fun buttonIdToCameraAction(selectedButtonId: Int): CameraViewModel.Companion.CameraAction {
    val cameraAction = when (selectedButtonId) {
        R.id.radio_take_video -> CameraViewModel.Companion.CameraAction.RECORD_VIDEO
        R.id.radio_take_photo -> CameraViewModel.Companion.CameraAction.TAKE_PICTURE
        else -> CameraViewModel.DEFAULT_CAMERA_ACTION
    }
    return cameraAction
}

@InverseMethod("buttonIdToChipVisibility")
fun chipVisibilityToButtonId(delegate: CameraViewModel.Companion.ChipVisibility): Int {
    val selectedButtonId = when (delegate) {
        CameraViewModel.Companion.ChipVisibility.FULL -> R.id.radio_full
        CameraViewModel.Companion.ChipVisibility.PARTIAL -> R.id.radio_partial
        CameraViewModel.Companion.ChipVisibility.HIDDEN -> R.id.radio_hidden
    }

    return selectedButtonId
}

fun buttonIdToChipVisibility(selectedButtonId: Int): CameraViewModel.Companion.ChipVisibility {
    val visibility = when (selectedButtonId) {
        R.id.radio_full -> CameraViewModel.Companion.ChipVisibility.FULL
        R.id.radio_partial -> CameraViewModel.Companion.ChipVisibility.PARTIAL
        R.id.radio_hidden -> CameraViewModel.Companion.ChipVisibility.HIDDEN
        else -> CameraViewModel.DEFAULT_CHIP_VISIBILITY
    }
    return visibility
}

// MediaPipeSettings
@InverseMethod("buttonIdToDelegate")
fun delegateToButtonId(delegate: Int): Int {
    val selectedButtonId = when (delegate) {
        MediaPipeViewModel.DELEGATE_CPU -> R.id.radio_cpu
        MediaPipeViewModel.DELEGATE_GPU -> R.id.radio_gpu
        else -> R.id.radio_cpu
    }

    return selectedButtonId
}

fun buttonIdToDelegate(selectedButtonId: Int): Int {
    val delegate = when (selectedButtonId) {
        R.id.radio_cpu -> MediaPipeViewModel.DELEGATE_CPU
        R.id.radio_gpu -> MediaPipeViewModel.DELEGATE_GPU
        else -> MediaPipeViewModel.DELEGATE_CPU
    }
    return delegate
}

