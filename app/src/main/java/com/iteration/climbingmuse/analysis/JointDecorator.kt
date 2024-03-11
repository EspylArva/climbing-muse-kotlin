package com.iteration.climbingmuse.analysis

import android.graphics.Color
import android.graphics.Paint
import androidx.lifecycle.MutableLiveData
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

class JointDecorator(val showJointMarkers: MutableLiveData<Boolean>) : ComputerVisionDecorator {

    val jointPaint = Paint().apply {
        color = Color.YELLOW
        strokeWidth = 12F
        style = Paint.Style.FILL
    }

    override fun process(data: PoseLandmarkerResult) {
        super.process(data)
        if(data.landmarks().size > 0 && showJointMarkers.value == true) {
            data.landmarks()[0].forEach {
                points.add(ComputerVisionDecorator.CanvasPointInfo(it.x(), it.y(), jointPaint))
            }
        }
    }

    private val points = arrayListOf<ComputerVisionDecorator.CanvasPointInfo>()

    override val textsToDraw: ArrayList<ComputerVisionDecorator.CanvasTextInfo>
        get() = arrayListOf()
    override val pointsToDraw: ArrayList<ComputerVisionDecorator.CanvasPointInfo>
        get() = points
    override val linesToDraw: ArrayList<ComputerVisionDecorator.CanvasLineInfo>
        get() = arrayListOf()
    override val pathsToDraw: ArrayList<ComputerVisionDecorator.CanvasPathInfo>
        get() = arrayListOf()
}