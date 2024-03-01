package com.iteration.climbingmuse.analysis

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

interface ComputerVisionDecorator {
    fun process(data: PoseLandmarkerResult)
    val textsToDraw: ArrayList<CanvasTextInfo>
    val pointsToDraw: ArrayList<CanvasPointInfo>
    val linesToDraw: ArrayList<CanvasLineInfo>
    val pathsToDraw: ArrayList<CanvasPathInfo>

    class CanvasTextInfo(val text: String, val normalizedX: Float, val normalizedY: Float, val paint: Paint) {
        override fun toString() = "[$text]@(${normalizedX}x${normalizedY})"
    }
    class CanvasPointInfo(val normalizedX: Float, val normalizedY: Float, val paint: Paint) {
        override fun toString() = "[Point⌀${paint.strokeWidth}]@(${normalizedX}x${normalizedY})"
    }
    class CanvasLineInfo(val normalizedStartX: Float, val normalizedStartY: Float, val normalizedEndX: Float, val normalizedEndY: Float, val paint: Paint) {
        override fun toString() = "[Line]@(${normalizedStartX}:${normalizedStartY}x${normalizedEndX}:${normalizedEndY})"
    }
    class CanvasPathInfo(val path: ArrayList<Pair<Float, Float>>, val paint: Paint) {
        override fun toString() = "[Path]@(${path.joinToString("->") { pair -> "${pair.first}:${pair.second}" }})"
    }
}