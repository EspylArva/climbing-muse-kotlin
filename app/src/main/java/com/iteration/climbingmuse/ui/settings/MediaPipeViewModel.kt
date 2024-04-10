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

        model.postValue(sp.getString(res.getString(R.string.sp_mediapipe_model), MODEL_POSE_LANDMARKER_FULL))
        detectionThreshold.postValue(sp.getFloat(res.getString(R.string.sp_mediapipe_detection_threshold), DEFAULT_POSE_DETECTION_CONFIDENCE))
        trackableThreshold.postValue(sp.getFloat(res.getString(R.string.sp_mediapipe_trackable_threshold), DEFAULT_POSE_TRACKING_CONFIDENCE))
        presenceThreshold.postValue(sp.getFloat(res.getString(R.string.sp_mediapipe_presence_threshold), DEFAULT_POSE_PRESENCE_CONFIDENCE))
    }



    // TODO
    private var _delegate: Int = DELEGATE_CPU

    val currentDelegate: Int get() = _delegate
    // END

    fun resetParams() {
        model.postValue(MODEL_POSE_LANDMARKER_FULL)
        detectionThreshold.postValue(DEFAULT_POSE_DETECTION_CONFIDENCE)
        trackableThreshold.postValue(DEFAULT_POSE_TRACKING_CONFIDENCE)
        presenceThreshold.postValue(DEFAULT_POSE_PRESENCE_CONFIDENCE)
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
        const val DEFAULT_POSE_DETECTION_CONFIDENCE = 50f
        const val DEFAULT_POSE_TRACKING_CONFIDENCE = 50f
        const val DEFAULT_POSE_PRESENCE_CONFIDENCE = 50f

        // Options for model should be contained at resources.getStringArray(R.array.models_spinner_titles)
        const val MODEL_POSE_LANDMARKER_FULL = "Pose Landmarker Full"
        const val MODEL_POSE_LANDMARKER_LITE = "Pose Landmarker Lite"
        const val MODEL_POSE_LANDMARKER_HEAVY ="Pose Landmarker Heavy"

    }
}

