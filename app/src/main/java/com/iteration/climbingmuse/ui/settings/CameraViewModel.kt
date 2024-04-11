package com.iteration.climbingmuse.ui.settings

import android.app.Application
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.databinding.Bindable
import androidx.databinding.InverseMethod
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.iteration.climbingmuse.R
import timber.log.Timber

class CameraViewModel(application: Application) : AndroidViewModel(application), Observable {

    /// Camera settings
    @Bindable val cameraSelection = MutableLiveData<Int>().apply { value = CameraSelector.LENS_FACING_BACK }
    ///

    init {
        val res = application.resources
        val sp = application.getSharedPreferences(res.getString(R.string.app_name), Context.MODE_PRIVATE)

        cameraSelection.postValue(sp.getInt(res.getString(R.string.sp_camera_cameraSelection), CameraSelector.LENS_FACING_BACK))
    }

    fun resetParams() {
        cameraSelection.postValue(CameraSelector.LENS_FACING_BACK)
    }

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

    object Converter {
        @JvmStatic fun getCameraName(cameraCode: Int): String {
            val label = when(cameraCode) {
                CameraSelector.LENS_FACING_BACK -> "Back Camera"
                CameraSelector.LENS_FACING_FRONT -> "Front Camera"
                else -> "UNKNOWN"
            }
            Timber.d("Camera code: $cameraCode ==> $label")
            return label
        }

        @InverseMethod("getCameraName")
        @JvmStatic fun getCameraCode(cameraLabel: String): Int {
            val cameraCode = when(cameraLabel) {
                "Back Camera" -> CameraSelector.LENS_FACING_BACK
                "Front Camera" -> CameraSelector.LENS_FACING_FRONT
                else -> CameraSelector.LENS_FACING_BACK
            }
            Timber.d("Camera code: $cameraLabel ==> $cameraCode")
            return cameraCode
        }
    }
}
