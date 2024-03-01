package com.iteration.climbingmuse.analysis

import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

class MuscleEngagementDecorator : ComputerVisionDecorator {

    // According to tedbergstrand's description:
    // The limb colors show how flexed/engaged that joint is -- red is heavily flexed, green is extended.
    override fun process(data: PoseLandmarkerResult) {

    }

    override val textsToDraw: ArrayList<ComputerVisionDecorator.CanvasTextInfo>
        get() = TODO("Not yet implemented")
    override val pointsToDraw: ArrayList<ComputerVisionDecorator.CanvasPointInfo>
        get() = TODO("Not yet implemented")
}