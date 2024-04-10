@file:JvmName("BindingUtils")
package com.iteration.climbingmuse.ui.settings

import androidx.databinding.InverseMethod
import com.iteration.climbingmuse.R

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