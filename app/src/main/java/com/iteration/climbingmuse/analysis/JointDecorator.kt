package com.iteration.climbingmuse.analysis

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import com.iteration.climbingmuse.ui.OverlayView

class JointDecorator : ComputerVisionDecorator {

    val jointPaint = Paint().apply {
        color = Color.YELLOW
        strokeWidth = 12F
        style = Paint.Style.FILL
    }

    override fun process(data: PoseLandmarkerResult) {
        if(data.landmarks().size > 0) {
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