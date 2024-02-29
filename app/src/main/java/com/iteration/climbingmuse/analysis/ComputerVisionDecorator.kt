package com.iteration.climbingmuse.analysis

import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

interface ComputerVisionDecorator {
    abstract fun process(data: PoseLandmarkerResult)

}