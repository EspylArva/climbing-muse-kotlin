package com.iteration.climbingmuse.analysis

import android.graphics.Color
import android.graphics.Paint
import androidx.lifecycle.MutableLiveData
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.abs

class GravityCenterDecorator(
    val showCOGMarker: MutableLiveData<Boolean>,
    val showCOGTrail: MutableLiveData<Boolean>, // TODO implement this function
    val showBalanceMarker: MutableLiveData<Boolean>
) : ComputerVisionDecorator {
    private val cogPaint = Paint().apply {
        color = Color.YELLOW
        strokeWidth = 18F
        style = Paint.Style.FILL
    }

    // According to tedbergstrand's description:
    // The triangle shows when your Center of Gravity tips outside your feet (base of support).
    // The white line is the Center of Gravity, extended straight down.
    // And the blue squiggly line is tracking the center of gravity -- which doesn't work perfectly in this case because the camera moves.
    // But it's still pretty neat.
    override fun process(data: PoseLandmarkerResult) {
        super.process(data)
        if(showCOGMarker.value == false && showCOGTrail.value == false && showBalanceMarker.value == false) {
            return
        }

        if(data.landmarks().size > 0) {
            val cog = processCOG(data.landmarks()[0])

            if(showCOGMarker.value == true) {
                points.add(ComputerVisionDecorator.CanvasPointInfo(cog.x(), cog.y(), cogPaint))
            }
            if(showBalanceMarker.value == true) {
                addBalanceMarker(cog, data.landmarks()[0])
            }
            if(showCOGTrail.value == true) {
                addCOGTrail(data.landmarks()[0])
            }
        }

    }

    private fun addBalanceMarker(cog: NormalizedLandmark, poi: List<NormalizedLandmark>) {
        val lAnkle = poi[VideoProcessor.Joint.LEFT_ANKLE.mpId]
        val rAnkle = poi[VideoProcessor.Joint.RIGHT_ANKLE.mpId]

        val balancePaint = Paint().apply {
            color = if (lAnkle.x() <= cog.x() && cog.x() <= rAnkle.x() || rAnkle.x() <= cog.x() && cog.x() <= lAnkle.x())
                Color.argb(64, 0, 0, 255) else  Color.argb(64, 255, 0, 0)
            strokeWidth = 12F
            style = Paint.Style.FILL
        }

        paths.add(
            ComputerVisionDecorator.CanvasPathInfo(
            arrayListOf(
                Pair(cog.x(), cog.y()),
                Pair(lAnkle.x(), lAnkle.y()),
                Pair(rAnkle.x(), rAnkle.y())
            ),
            balancePaint)
        )
    }

    private fun addCOGTrail(poi: List<NormalizedLandmark>) {
        // TODO
    }


    private fun processCOG(poi: List<NormalizedLandmark>) : NormalizedLandmark {
        // Center of Gravity (CoG) is situated around the belly button, which should be 1/4 of the trunk above the hips
        val lShoulder = poi[VideoProcessor.Joint.LEFT_SHOULDER.mpId]
        val rShoulder = poi[VideoProcessor.Joint.RIGHT_SHOULDER.mpId]
        val neck = NormalizedLandmark.create(
            abs(lShoulder.x() + rShoulder.x())/2,
            abs(lShoulder.y() + rShoulder.y())/2,
            abs(lShoulder.z() + rShoulder.z())/2
        )
        val lHip = poi[VideoProcessor.Joint.LEFT_HIP.mpId]
        val rHip = poi[VideoProcessor.Joint.RIGHT_HIP.mpId]
        val crotch = NormalizedLandmark.create(
            abs(lHip.x() + rHip.x())/2,
            abs(lHip.y() + rHip.y())/2,
            abs(lHip.z() + rHip.z())/2
        )

        return NormalizedLandmark.create(
            crotch.x() + 0.25f * (neck.x() - crotch.x()),
            crotch.y() + 0.25f * (neck.y() - crotch.y()),
            crotch.z() + 0.25f * (neck.z() - crotch.z())
        )
    }

    private val points = arrayListOf<ComputerVisionDecorator.CanvasPointInfo>()
    private val paths = arrayListOf<ComputerVisionDecorator.CanvasPathInfo>()

    override val textsToDraw: ArrayList<ComputerVisionDecorator.CanvasTextInfo>
        get() = arrayListOf()
    override val pointsToDraw: ArrayList<ComputerVisionDecorator.CanvasPointInfo>
        get() = points
    override val linesToDraw: ArrayList<ComputerVisionDecorator.CanvasLineInfo>
        get() = arrayListOf()
    override val pathsToDraw: ArrayList<ComputerVisionDecorator.CanvasPathInfo>
        get() = paths
}