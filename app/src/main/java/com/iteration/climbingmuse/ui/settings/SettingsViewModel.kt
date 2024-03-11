package com.iteration.climbingmuse.ui.settings

import androidx.databinding.Bindable
import androidx.databinding.InverseMethod
import androidx.databinding.Observable
import androidx.databinding.ObservableInt
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.checkbox.MaterialCheckBox
import com.iteration.climbingmuse.analysis.PoseLandmarkerHelper
import com.iteration.climbingmuse.analysis.PoseLandmarkerHelper.Companion.MODEL_POSE_LANDMARKER_FULL
import com.iteration.climbingmuse.analysis.PoseLandmarkerHelper.Companion.MODEL_POSE_LANDMARKER_HEAVY
import com.iteration.climbingmuse.analysis.PoseLandmarkerHelper.Companion.MODEL_POSE_LANDMARKER_LITE
import timber.log.Timber

class SettingsViewModel : ViewModel(), Observable {

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
    init {
        model.postValue(MODEL_POSE_LANDMARKER_FULL)

        showAngles.postValue(true)
        showCOGTrail.postValue(true)
        showCOGMarker.postValue(false)
        showBalanceMarker.postValue(false)
        showJointMarkers.postValue(true)
        showMuscleMarkers.postValue(true)
        showMuscleEngagement.postValue(true)

    }

    private var _delegate: Int = PoseLandmarkerHelper.DELEGATE_CPU
    private var _minPoseDetectionConfidence: Float = PoseLandmarkerHelper.DEFAULT_POSE_DETECTION_CONFIDENCE
    private var _minPoseTrackingConfidence: Float = PoseLandmarkerHelper.DEFAULT_POSE_TRACKING_CONFIDENCE
    private var _minPosePresenceConfidence: Float = PoseLandmarkerHelper.DEFAULT_POSE_PRESENCE_CONFIDENCE

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


    object MaterialCheckBoxConverter {

        enum class State {
            indeterminate,
            checked,
            unchecked
        }


        @InverseMethod("stringToState")
        @JvmStatic
        fun stateToString(state: Int) : State {
            return when(state) {
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
        fun stringToState(state: String) : Int {
            return when(state) {
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

}