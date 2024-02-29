package com.iteration.climbingmuse.analysis

import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

class MuscleEngagementDecorator : ComputerVisionDecorator {

    // According to tedbergstrand's description:
    // The limb colors show how flexed/engaged that joint is -- red is heavily flexed, green is extended.
    override fun process(data: PoseLandmarkerResult) {

    }
}