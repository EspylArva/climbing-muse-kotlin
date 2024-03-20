package com.iteration.climbingmuse.analysis

import android.graphics.Color
import android.graphics.Paint
import androidx.lifecycle.MutableLiveData
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

class MuscleEngagementDecorator(
    val showMuscleMarkers: MutableLiveData<Boolean>,
    val showMuscleEngagement: MutableLiveData<Boolean> //TODO Implement this function
) : ComputerVisionDecorator {

    private val paint = Paint().apply {
        strokeWidth = 12F
        style = Paint.Style.STROKE
    }

    // According to tedbergstrand's description:
    // The limb colors show how flexed/engaged that joint is -- red is heavily flexed, green is extended.
    override fun process(data: PoseLandmarkerResult) {
        super.process(data)
        if(showMuscleMarkers.value == false && showMuscleEngagement.value == false) {
            return
        }

        if (data.landmarks().size == 0) return
        PoseLandmarker.POSE_LANDMARKS.forEach {
            lines.add(ComputerVisionDecorator.CanvasLineInfo(
                data.landmarks()[0][it!!.start()].x(),
                data.landmarks()[0][it.start()].y(),
                data.landmarks()[0][it.end()].x(),
                data.landmarks()[0][it.end()].y(),
                getColor(AngleDecorator.calculateJointAngle(if (it.start() == 0) it.end() else it.start(), data))
            ))
        }
    }

    private fun getColor(angle: Float) : Paint {
        val isShoulder = true
        if (isShoulder) {
            return when {
                angle < 30f ->  Paint().apply { color = Color.GREEN }
                angle in 30f..60f -> Paint().apply { color = Color.YELLOW }
                else -> Paint().apply { color = Color.RED }
            }
        } else {
            return when (angle) {
                in 100f..150f -> Paint().apply { color = Color.YELLOW }
                in 150f..180f -> Paint().apply { color = Color.GREEN }
                else -> Paint().apply { color = Color.RED }
            }

        }
//    def color_for_angle(angle, is_shoulder=False):
    //    if is_shoulder:
    //        # For shoulder joints, consider close to 0 as at rest
    //        if angle < 30:  # You can adjust this threshold
    //            return (0, 255, 0)  # Green
    //        elif 30 <= angle <= 60:
    //            return (0, 255, 255) # Yellow
    //        else:
    //            return (0, 0, 255)  # Red
    //    else:
    //        # For other joints
    //        if 150 <= angle <= 180:
    //            return (0, 255, 0)  # Green
    //        elif 100 <= angle < 150:
    //            return (0, 255, 255)  # Yellow
    //        else:
    //            return (0, 0, 255)  # Red
    }

    private val lines = arrayListOf<ComputerVisionDecorator.CanvasLineInfo>()
    override val textsToDraw: ArrayList<ComputerVisionDecorator.CanvasTextInfo>
        get() = arrayListOf()
    override val pointsToDraw: ArrayList<ComputerVisionDecorator.CanvasPointInfo>
        get() = arrayListOf()
    override val linesToDraw: ArrayList<ComputerVisionDecorator.CanvasLineInfo>
        get() = lines
    override val pathsToDraw: ArrayList<ComputerVisionDecorator.CanvasPathInfo>
        get() = arrayListOf()
}