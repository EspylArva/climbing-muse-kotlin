package com.iteration.climbingmuse.analysis

import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import com.iteration.climbingmuse.R
import com.iteration.climbingmuse.ui.OverlayView

class MuscleEngagementDecorator : ComputerVisionDecorator {

    private val paint = Paint().apply {
        strokeWidth = 12F
        style = Paint.Style.STROKE
    }

    // According to tedbergstrand's description:
    // The limb colors show how flexed/engaged that joint is -- red is heavily flexed, green is extended.
    override fun process(data: PoseLandmarkerResult) {

        for(landmark in data.landmarks()) {
            for (normalizedLandmark in landmark) {
                PoseLandmarker.POSE_LANDMARKS.forEach {
                    lines.add(ComputerVisionDecorator.CanvasLineInfo(
                        data.landmarks()[0][it!!.start()].x(),
                        data.landmarks()[0][it.start()].y(),
                        data.landmarks()[0][it.end()].x(),
                        data.landmarks()[0][it.end()].y(),
                        paint // TODO: no color, require context : paint.color = ContextCompat.getColor(context!!, R.color.mp_color_primary)
                    ))
                }
            }
        }

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