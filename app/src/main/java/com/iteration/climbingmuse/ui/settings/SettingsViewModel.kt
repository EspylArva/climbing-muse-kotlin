package com.iteration.climbingmuse.ui.settings

import android.app.Application
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.databinding.Bindable
import androidx.databinding.InverseMethod
import androidx.databinding.Observable
import androidx.databinding.ObservableInt
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.checkbox.MaterialCheckBox
import com.iteration.climbingmuse.R
import com.iteration.climbingmuse.analysis.PoseLandmarkerHelper
import com.iteration.climbingmuse.analysis.PoseLandmarkerHelper.Companion.MODEL_POSE_LANDMARKER_FULL
import com.iteration.climbingmuse.analysis.PoseLandmarkerHelper.Companion.MODEL_POSE_LANDMARKER_HEAVY
import com.iteration.climbingmuse.analysis.PoseLandmarkerHelper.Companion.MODEL_POSE_LANDMARKER_LITE
import timber.log.Timber

class SettingsViewModel(application: Application) : AndroidViewModel(application), Observable {

    /// Computer Vision settings
    // MediaPipe model used
    @Bindable
    val model = MutableLiveData<String>()

    // Angle options
    @Bindable
    val showAngles = MutableLiveData<Boolean>()

    // Center of Gravity options
    @Bindable
    val showCOGTrail = MutableLiveData<Boolean>()

    @Bindable
    val showCOGMarker = MutableLiveData<Boolean>()

    @Bindable
    val showBalanceMarker = MutableLiveData<Boolean>()

    // Joints options
    @Bindable
    val showJointMarkers = MutableLiveData<Boolean>()

    // Muscles options
    @Bindable
    val showMuscleMarkers = MutableLiveData<Boolean>()

    @Bindable
    val showMuscleEngagement = MutableLiveData<Boolean>()
    ///

    /// Camera settings
    val cameraSelection = MutableLiveData<Int>()
    ///

    init {
        val res = application.resources
        val sp =
            application.getSharedPreferences(res.getString(R.string.app_name), Context.MODE_PRIVATE)


        /// Computer Vision settings
        showAngles.postValue(sp.getBoolean(res.getString(R.string.sp_cv_showAngles), ComputerVisionViewModel.showAngles))
        showCOGTrail.postValue(sp.getBoolean(res.getString(R.string.sp_cv_showCogTrail), ComputerVisionViewModel.showCOGTrail))
        showCOGMarker.postValue(sp.getBoolean(res.getString(R.string.sp_cv_showCogMarker), ComputerVisionViewModel.showCOGMarker))
        showBalanceMarker.postValue(sp.getBoolean(res.getString(R.string.sp_cv_showBalanceMarker), ComputerVisionViewModel.showBalanceMarker))
        showJointMarkers.postValue(sp.getBoolean(res.getString(R.string.sp_cv_showJoints), ComputerVisionViewModel.showJointMarkers))
        showMuscleMarkers.postValue(sp.getBoolean(res.getString(R.string.sp_cv_showMusclesMarker), ComputerVisionViewModel.showMuscleMarkers))
        showMuscleEngagement.postValue(sp.getBoolean(res.getString(R.string.sp_cv_showMuscleEngagement), ComputerVisionViewModel.showMuscleEngagement))

        showAngles.observeForever {
            sp.edit()
                .putBoolean(res.getString(R.string.sp_cv_showAngles), it)
                .apply()
        }
        showCOGTrail.observeForever {
            sp.edit()
                .putBoolean(res.getString(R.string.sp_cv_showCogTrail), it)
                .apply()
        }
        showCOGMarker.observeForever {
            sp.edit()
                .putBoolean(res.getString(R.string.sp_cv_showCogMarker), it)
                .apply()
        }
        showBalanceMarker.observeForever {
            sp.edit()
                .putBoolean(res.getString(R.string.sp_cv_showBalanceMarker), it)
                .apply()
        }
        showJointMarkers.observeForever {
            sp.edit()
                .putBoolean(res.getString(R.string.sp_cv_showJoints), it)
                .apply()
        }
        showMuscleMarkers.observeForever {
            sp.edit()
                .putBoolean(res.getString(R.string.sp_cv_showMusclesMarker), it)
                .apply()
        }
        showMuscleEngagement.observeForever {
            sp.edit()
                .putBoolean(res.getString(R.string.sp_cv_showMuscleEngagement), it)
                .apply()
        }

        /// MediaPipe settings
        model.postValue(sp.getString(
            res.getString(R.string.sp_mediapipe_model), MediaPipeViewModel.MODEL_POSE_LANDMARKER_FULL)
        )
        model.observeForever {
            sp.edit()
                .putString(res.getString(R.string.sp_mediapipe_model), it)
                .apply()
        }

        /// Camera settings
        cameraSelection.postValue(
            sp.getInt(
                res.getString(R.string.sp_camera_cameraSelection),
                CameraSelector.LENS_FACING_BACK
            )
        )
    }


    private var _delegate: Int = PoseLandmarkerHelper.DELEGATE_CPU
    private var _minPoseDetectionConfidence: Float =
        PoseLandmarkerHelper.DEFAULT_POSE_DETECTION_CONFIDENCE
    private var _minPoseTrackingConfidence: Float =
        PoseLandmarkerHelper.DEFAULT_POSE_TRACKING_CONFIDENCE
    private var _minPosePresenceConfidence: Float =
        PoseLandmarkerHelper.DEFAULT_POSE_PRESENCE_CONFIDENCE

    val currentDelegate: Int get() = _delegate
    val currentMinPoseDetectionConfidence: Float
        get() = _minPoseDetectionConfidence
    val currentMinPoseTrackingConfidence: Float
        get() = _minPoseTrackingConfidence
    val currentMinPosePresenceConfidence: Float
        get() = _minPosePresenceConfidence

    fun setDelegate(delegate: Int) {
        _delegate = delegate
    }

    fun setMinPoseDetectionConfidence(confidence: Float) {
        _minPoseDetectionConfidence = confidence
    }

    fun setMinPoseTrackingConfidence(confidence: Float) {
        _minPoseTrackingConfidence = confidence
    }

    fun setMinPosePresenceConfidence(confidence: Float) {
        _minPosePresenceConfidence = confidence
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


    override fun toString(): String {
        return """
                    Settings: ${this.hashCode()}
                    - Model: ${model.value}
                    - Decorators:
                        - Angle: ${showAngles.value}
                        - Center of Gravity:
                            - Marker: ${showCOGMarker.value}
                            - Trail: ${showCOGTrail.value}
                            - Balance: ${showBalanceMarker.value}
                        - Joints: ${showJointMarkers.value}
                        - Muscles:
                            - Marker: ${showMuscleMarkers.value}
                            - Engagement: ${showMuscleEngagement.value}
                """.trimIndent()
    }

    object MaterialCheckBoxConverter {

        enum class State {
            indeterminate,
            checked,
            unchecked
        }


        @InverseMethod("stringToState")
        @JvmStatic
        fun stateToString(state: Int): State {
            return when (state) {
                MaterialCheckBox.STATE_CHECKED -> State.checked
                MaterialCheckBox.STATE_INDETERMINATE -> State.indeterminate
                MaterialCheckBox.STATE_UNCHECKED -> State.unchecked
                else -> {
                    Timber.e("State [%s] should not exist", state)
                    return State.indeterminate
                }
            }
        }

        @JvmStatic
        fun stringToState(state: String): Int {
            return when (state) {
                "checked" -> MaterialCheckBox.STATE_CHECKED
                "indeterminate" -> MaterialCheckBox.STATE_INDETERMINATE
                "unchecked" -> MaterialCheckBox.STATE_UNCHECKED
                else -> {
                    Timber.e("State [%s] should not exist", state)
                    return MaterialCheckBox.STATE_INDETERMINATE
                }
            }

        }

    }
}

class ComputerVisionViewModel {
    companion object {
        const val showAngles = true
        const val showCOGTrail = true
        const val showCOGMarker = false
        const val showBalanceMarker = false
        const val showJointMarkers = true
        const val showMuscleMarkers = true
        const val showMuscleEngagement = true
    }

}

class MediaPipeViewModel {
    companion object {
        const val TAG = "PoseLandmarkerHelper"

        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DEFAULT_POSE_DETECTION_CONFIDENCE = 0.5F
        const val DEFAULT_POSE_TRACKING_CONFIDENCE = 0.5F
        const val DEFAULT_POSE_PRESENCE_CONFIDENCE = 0.5F
        const val DEFAULT_NUM_POSES = 1
        const val OTHER_ERROR = 0
        const val GPU_ERROR = 1

        // Options for model should be contained at resources.getStringArray(R.array.models_spinner_titles)
        const val MODEL_POSE_LANDMARKER_FULL = "Pose Landmarker Full"
        const val MODEL_POSE_LANDMARKER_LITE = "Pose Landmarker Lite"
        const val MODEL_POSE_LANDMARKER_HEAVY ="Pose Landmarker Heavy"

    }
}