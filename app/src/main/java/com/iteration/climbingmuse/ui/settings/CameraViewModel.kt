package com.iteration.climbingmuse.ui.settings

import android.app.Application
import android.content.Context
import androidx.camera.core.Camera
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
    @Bindable val computerVisionActivated = MutableLiveData<Boolean>().apply { value = DEFAULT_CV_ACTIVATED }
    @Bindable val cameraAction = MutableLiveData<CameraAction>().apply { value = DEFAULT_CAMERA_ACTION }
    @Bindable val chipVisibility = MutableLiveData<ChipVisibility>().apply { value = DEFAULT_CHIP_VISIBILITY }

    // Displayed Chips
    @Bindable val recordChip = MutableLiveData<Boolean>().apply { value = DEFAULT_RECORD_CHIP_VISIBILITY }
    @Bindable val cvChip = MutableLiveData<Boolean>().apply { value = DEFAULT_CV_CHIP_VISIBILITY }
    @Bindable val chipVisibilityChip = MutableLiveData<Boolean>().apply { value = DEFAULT_CHIPS_CHIP_VISIBILITY }
    ///

    init {
        val res = application.resources
        val sp = application.getSharedPreferences(res.getString(R.string.app_name), Context.MODE_PRIVATE)

        cameraSelection.postValue(sp.getInt(res.getString(R.string.sp_camera_cameraSelection), CameraSelector.LENS_FACING_BACK))
        computerVisionActivated.postValue(sp.getBoolean(res.getString(R.string.sp_camera_cvActivated), DEFAULT_CV_ACTIVATED))
        cameraAction.postValue(CameraAction.valueOf(sp.getString(res.getString(R.string.sp_camera_cameraAction), DEFAULT_CAMERA_ACTION.toString())!!))
        chipVisibility.postValue(ChipVisibility.valueOf(sp.getString(res.getString(R.string.sp_camera_chipVisibility), DEFAULT_CHIP_VISIBILITY.toString())!!))

        recordChip.postValue(sp.getBoolean(res.getString(R.string.sp_camera_recordChipVisibility), DEFAULT_RECORD_CHIP_VISIBILITY))
        cvChip.postValue(sp.getBoolean(res.getString(R.string.sp_camera_cvChipVisibility), DEFAULT_CV_CHIP_VISIBILITY))
        chipVisibilityChip.postValue(sp.getBoolean(res.getString(R.string.sp_camera_showChipsChipVisibility), DEFAULT_CHIPS_CHIP_VISIBILITY))
    }

    fun resetParams() {
        cameraSelection.postValue(CameraSelector.LENS_FACING_BACK)
        computerVisionActivated.postValue(DEFAULT_CV_ACTIVATED)
        cameraAction.postValue(DEFAULT_CAMERA_ACTION)
        chipVisibility.postValue(DEFAULT_CHIP_VISIBILITY)

        recordChip.postValue(DEFAULT_RECORD_CHIP_VISIBILITY)
        cvChip.postValue(DEFAULT_CV_CHIP_VISIBILITY)
        chipVisibilityChip.postValue(DEFAULT_CHIPS_CHIP_VISIBILITY)
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

    companion object {
        enum class CameraAction {
            RECORD_VIDEO,
            TAKE_PICTURE
        }

        enum class ChipVisibility {
            FULL,
            PARTIAL,
            HIDDEN
        }

        const val TAG = "CameraViewModel"

        const val DEFAULT_CV_ACTIVATED = true
        val DEFAULT_CAMERA_ACTION = CameraAction.RECORD_VIDEO
        val DEFAULT_CHIP_VISIBILITY = ChipVisibility.PARTIAL

        const val DEFAULT_RECORD_CHIP_VISIBILITY = true
        const val DEFAULT_CV_CHIP_VISIBILITY = true
        const val DEFAULT_CHIPS_CHIP_VISIBILITY = true


    }
}
