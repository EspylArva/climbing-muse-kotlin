package com.iteration.climbingmuse.analysis

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import com.iteration.climbingmuse.ui.OverlayView

class JointDecorator() : ComputerVisionDecorator {
    override fun process(data: PoseLandmarkerResult) {
        val jointPaint = Paint().apply {
            color = Color.YELLOW
            strokeWidth = 12F
            style = Paint.Style.FILL
        }
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
}