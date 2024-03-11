package com.iteration.climbingmuse.analysis

import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import java.util.ArrayList

class VideoProcessor {

    enum class Joint(val mpId: Int) {
        HEAD(0),
        LEFT_SHOULDER(11),
        LEFT_ELBOW(13),
        LEFT_HAND(15),
        LEFT_HIP(23),
        LEFT_KNEE(25),
        LEFT_ANKLE(27),
        LEFT_HEEL(29),
        LEFT_TOE(31),

        RIGHT_SHOULDER(12),
        RIGHT_ELBOW(14),
        RIGHT_HAND(16),
        RIGHT_HIP(24),
        RIGHT_KNEE(26),
        RIGHT_ANKLE(28),
        RIGHT_HEEL(30),
        RIGHT_TOE(32),
    }

    var decorators: ArrayList<ComputerVisionDecorator> = arrayListOf()

    fun processData(data: PoseLandmarkerResult): Any {
        decorators.forEach { it.process(data) }
        return data
    }
}