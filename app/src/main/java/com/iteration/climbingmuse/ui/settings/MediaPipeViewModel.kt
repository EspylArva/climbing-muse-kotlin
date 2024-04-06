package com.iteration.climbingmuse.ui.settings

import android.app.Application
import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.iteration.climbingmuse.R

class MediaPipeViewModel(application: Application) : AndroidViewModel(application), Observable {

    // MediaPipe model used
    @Bindable
    val model = MutableLiveData<String>()

    init {
        val res = application.resources
        val sp = application.getSharedPreferences(res.getString(R.string.app_name), Context.MODE_PRIVATE)

        /// MediaPipe settings
        model.postValue(sp.getString(res.getString(R.string.sp_mediapipe_model), MediaPipeViewModel.MODEL_POSE_LANDMARKER_FULL))
        model.observeForever { sp.edit().putString(res.getString(R.string.sp_mediapipe_model), it).apply() }
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

