package com.iteration.climbingmuse.ui.settings

import android.app.Application
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.iteration.climbingmuse.R
import com.iteration.climbingmuse.analysis.PoseLandmarkerHelper

class ComputerVisionViewModel(application: Application) : AndroidViewModel(application), Observable {
    /// Computer Vision settings
    // Angle options
    @Bindable val showAngles = MutableLiveData<Boolean>()

    // Center of Gravity options
    @Bindable val showCOGTrail = MutableLiveData<Boolean>()
    @Bindable val showCOGMarker = MutableLiveData<Boolean>()
    @Bindable val showBalanceMarker = MutableLiveData<Boolean>()

    // Joints options
    @Bindable val showJointMarkers = MutableLiveData<Boolean>()

    // Muscles options
    @Bindable val showMuscleMarkers = MutableLiveData<Boolean>()
    @Bindable val showMuscleEngagement = MutableLiveData<Boolean>()
    ///

    init {
        val res = application.resources
        val sp = application.getSharedPreferences(res.getString(R.string.app_name), Context.MODE_PRIVATE)

        /// Computer Vision settings
        showAngles.postValue(sp.getBoolean(res.getString(R.string.sp_cv_showAngles), ComputerVisionViewModel.DEFAULT_SHOW_ANGLES))
        showCOGTrail.postValue(sp.getBoolean(res.getString(R.string.sp_cv_showCogTrail), ComputerVisionViewModel.DEFAULT_SHOW_COG_TRAIL))
        showCOGMarker.postValue(sp.getBoolean(res.getString(R.string.sp_cv_showCogMarker), ComputerVisionViewModel.DEFAULT_SHOW_COG_MARKER))
        showBalanceMarker.postValue(sp.getBoolean(res.getString(R.string.sp_cv_showBalanceMarker), ComputerVisionViewModel.DEFAULT_SHOW_BALANCE_MARKER))
        showJointMarkers.postValue(sp.getBoolean(res.getString(R.string.sp_cv_showJoints), ComputerVisionViewModel.DEFAULT_SHOW_JOINTS_MARKER))
        showMuscleMarkers.postValue(sp.getBoolean(res.getString(R.string.sp_cv_showMusclesMarker), ComputerVisionViewModel.DEFAULT_SHOW_MUSCLES_MARKER))
        showMuscleEngagement.postValue(sp.getBoolean(res.getString(R.string.sp_cv_showMuscleEngagement), ComputerVisionViewModel.DEFAULT_SHOW_MUSCLES_ENGAGEMENT))
    }

    fun resetParams() {
        TODO("Not yet implemented")
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

    companion object {
        const val DEFAULT_SHOW_ANGLES = true
        const val DEFAULT_SHOW_COG_TRAIL = true
        const val DEFAULT_SHOW_COG_MARKER = false
        const val DEFAULT_SHOW_BALANCE_MARKER = false
        const val DEFAULT_SHOW_JOINTS_MARKER = true
        const val DEFAULT_SHOW_MUSCLES_MARKER = true
        const val DEFAULT_SHOW_MUSCLES_ENGAGEMENT = true
    }
}