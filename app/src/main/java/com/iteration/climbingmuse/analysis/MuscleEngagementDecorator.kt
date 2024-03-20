package com.iteration.climbingmuse.analysis

import android.graphics.Color
import android.graphics.Paint
import androidx.lifecycle.MutableLiveData
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

class MuscleEngagementDecorator(
    private val showMuscleMarkers: MutableLiveData<Boolean>,
    private val showMuscleEngagement: MutableLiveData<Boolean>
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
            val isShoulder = (it.start() == VideoProcessor.MediaPipeJoint.RIGHT_SHOULDER || it.start() == VideoProcessor.MediaPipeJoint.LEFT_SHOULDER) ||
                    (it.end() == VideoProcessor.MediaPipeJoint.RIGHT_SHOULDER || it.end() == VideoProcessor.MediaPipeJoint.LEFT_SHOULDER)
            lines.add(ComputerVisionDecorator.CanvasLineInfo(
                data.landmarks()[0][it!!.start()].x(),
                data.landmarks()[0][it.start()].y(),
                data.landmarks()[0][it.end()].x(),
                data.landmarks()[0][it.end()].y(),
                getColor(AngleDecorator.calculateJointAngle(if (it.start() == 0) it.end() else it.start(), data), isShoulder)
            ))
        }
    }

    private fun getColor(angle: Float, isShoulder: Boolean) : Paint {
        val paint = if (isShoulder) {
            when {
                angle < 30f ->  Paint().apply { color = Color.GREEN }
                angle in 30f..60f -> Paint().apply { color = Color.YELLOW }
                else -> Paint().apply { color = Color.RED }
            }
        } else {
            when (angle) {
                in 100f..150f -> Paint().apply { color = Color.YELLOW }
                in 150f..180f -> Paint().apply { color = Color.GREEN }
                else -> Paint().apply { color = Color.RED }
            }
        }
        paint.apply {
            strokeWidth = 12F
            style = Paint.Style.STROKE
        }
        return paint
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