package com.iteration.climbingmuse.ui.settings

import android.app.Application
import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.material.slider.Slider
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.iteration.climbingmuse.R
import com.iteration.climbingmuse.analysis.PoseLandmarkerHelper
import timber.log.Timber

class MediaPipeViewModel(application: Application) : AndroidViewModel(application), Observable {

    // MediaPipe model used
    @Bindable
    val model = MutableLiveData<String>()
    @Bindable
    val detectionThreshold = MutableLiveData<Float>(50f)
    @Bindable
    val trackableThreshold = MutableLiveData<Float>(50f)
    @Bindable
    val presenceThreshold = MutableLiveData<Float>(50f)

    init {
        val res = application.resources
        val sp = application.getSharedPreferences(res.getString(R.string.app_name), Context.MODE_PRIVATE)

        /// MediaPipe settings
        model.observeForever { sp.edit().putString(res.getString(R.string.sp_mediapipe_model), it).apply() }
        detectionThreshold.observeForever { sp.edit().putFloat(res.getString(R.string.sp_mediapipe_detection_threshold), it).apply() }
        trackableThreshold.observeForever { sp.edit().putFloat(res.getString(R.string.sp_mediapipe_trackable_threshold), it).apply() }
        presenceThreshold.observeForever { sp.edit().putFloat(res.getString(R.string.sp_mediapipe_presence_threshold), it).apply() }

        model.postValue(sp.getString(res.getString(R.string.sp_mediapipe_model), MediaPipeViewModel.MODEL_POSE_LANDMARKER_FULL))
        detectionThreshold.postValue(sp.getFloat(res.getString(R.string.sp_mediapipe_detection_threshold), 50f))
        trackableThreshold.postValue(sp.getFloat(res.getString(R.string.sp_mediapipe_trackable_threshold), 50f))
        presenceThreshold.postValue(sp.getFloat(res.getString(R.string.sp_mediapipe_presence_threshold), 50f))

        val i = 50.0
    }



    // TODO
    private var _delegate: Int = DELEGATE_CPU
    private var _minPoseDetectionConfidence: Float =
        DEFAULT_POSE_DETECTION_CONFIDENCE
    private var _minPoseTrackingConfidence: Float =
        DEFAULT_POSE_TRACKING_CONFIDENCE
    private var _minPosePresenceConfidence: Float =
        DEFAULT_POSE_PRESENCE_CONFIDENCE

    private var minPoseDetectionConfidence: Float = DEFAULT_POSE_DETECTION_CONFIDENCE
    private var minPoseTrackingConfidence: Float = DEFAULT_POSE_TRACKING_CONFIDENCE
    private var minPosePresenceConfidence: Float = DEFAULT_POSE_PRESENCE_CONFIDENCE
    private var currentModel: String = MODEL_POSE_LANDMARKER_FULL
//    private var currentDelegate: Int = PoseLandmarkerHelper.DELEGATE_CPU
    private var runningMode: RunningMode = RunningMode.IMAGE


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
    // END



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


//    override fun toString(): String {
//        return """
//                    Settings: ${this.hashCode()}
//                    - Model: ${model.value}
//                    - Decorators:
//                        - Angle: ${showAngles.value}
//                        - Center of Gravity:
//                            - Marker: ${showCOGMarker.value}
//                            - Trail: ${showCOGTrail.value}
//                            - Balance: ${showBalanceMarker.value}
//                        - Joints: ${showJointMarkers.value}
//                        - Muscles:
//                            - Marker: ${showMuscleMarkers.value}
//                            - Engagement: ${showMuscleEngagement.value}
//                """.trimIndent()
//    }

    companion object {
        const val TAG = "MediaPipeViewModel"

        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DEFAULT_POSE_DETECTION_CONFIDENCE = 0.5F
        const val DEFAULT_POSE_TRACKING_CONFIDENCE = 0.5F
        const val DEFAULT_POSE_PRESENCE_CONFIDENCE = 0.5F

        // Options for model should be contained at resources.getStringArray(R.array.models_spinner_titles)
        const val MODEL_POSE_LANDMARKER_FULL = "Pose Landmarker Full"
        const val MODEL_POSE_LANDMARKER_LITE = "Pose Landmarker Lite"
        const val MODEL_POSE_LANDMARKER_HEAVY ="Pose Landmarker Heavy"

    }
}

