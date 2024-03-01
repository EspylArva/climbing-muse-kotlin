package com.iteration.climbingmuse.analysis

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

interface ComputerVisionDecorator {
    fun process(data: PoseLandmarkerResult)
    val textsToDraw: ArrayList<CanvasTextInfo>
    val pointsToDraw: ArrayList<CanvasPointInfo>
    val linesToDraw: ArrayList<CanvasLineInfo>
    val pathsToDraw: ArrayList<CanvasPathInfo>

    class CanvasTextInfo(val text: String, val normalizedX: Float, val normalizedY: Float, val paint: Paint)
    class CanvasPointInfo(val normalizedX: Float, val normalizedY: Float, val paint: Paint)
    class CanvasLineInfo(val normalizedStartX: Float, val normalizedStartY: Float, val normalizedEndX: Float, val normalizedEndY: Float, val paint: Paint)
    class CanvasPathInfo(val path: Path, val paint: Paint) // TODO
}