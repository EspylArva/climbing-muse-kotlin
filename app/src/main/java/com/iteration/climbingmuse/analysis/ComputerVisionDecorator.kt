package com.iteration.climbingmuse.analysis

import android.graphics.Canvas
import android.graphics.Paint
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

interface ComputerVisionDecorator {
    abstract fun process(data: PoseLandmarkerResult)
    val textsToDraw: ArrayList<CanvasTextInfo>
    val pointsToDraw: ArrayList<CanvasPointInfo>

    class CanvasTextInfo(val text: String, val normalizedX: Float, val normalizedY: Float, val paint: Paint)
    class CanvasPointInfo(val normalizedX: Float, val normalizedY: Float, val paint: Paint)
}