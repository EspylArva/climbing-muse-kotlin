package com.iteration.climbingmuse.analysis

import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import java.util.ArrayList

class VideoProcessor {

    class MediaPipeJoint {
        companion object {
            const val HEAD              = 0
            const val LEFT_SHOULDER     = 11
            const val LEFT_ELBOW        = 13
            const val LEFT_HAND         = 15
            const val LEFT_HIP          = 23
            const val LEFT_KNEE         = 25
            const val LEFT_ANKLE        = 27
            const val LEFT_HEEL         = 29
            const val LEFT_TOE          = 31
            const val RIGHT_SHOULDER    = 12
            const val RIGHT_ELBOW       = 14
            const val RIGHT_HAND        = 16
            const val RIGHT_HIP         = 24
            const val RIGHT_KNEE        = 26
            const val RIGHT_ANKLE       = 28
            const val RIGHT_HEEL        = 30
            const val RIGHT_TOE         = 32

        }
    }

    var decorators: ArrayList<ComputerVisionDecorator> = arrayListOf()

    fun processData(data: PoseLandmarkerResult): Any {
        decorators.forEach { it.process(data) }
        return data
    }
}