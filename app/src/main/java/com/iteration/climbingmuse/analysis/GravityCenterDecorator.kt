package com.iteration.climbingmuse.analysis

import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

class GravityCenterDecorator : ComputerVisionDecorator {

    // According to tedbergstrand's description:
    // The triangle shows when your Center of Gravity tips outside your feet (base of support).
    // The white line is the Center of Gravity, extended straight down.
    // And the blue squiggly line is tracking the center of gravity -- which doesn't work perfectly in this case because the camera moves.
    // But it's still pretty neat.
    override fun process(data: PoseLandmarkerResult) {

    }

    override val textsToDraw: ArrayList<ComputerVisionDecorator.CanvasTextInfo>
        get() = TODO("Not yet implemented")
    override val pointsToDraw: ArrayList<ComputerVisionDecorator.CanvasPointInfo>
        get() = TODO("Not yet implemented")
    override val linesToDraw: ArrayList<ComputerVisionDecorator.CanvasLineInfo>
        get() = TODO("Not yet implemented")
    override val pathsToDraw: ArrayList<ComputerVisionDecorator.CanvasPathInfo>
        get() = TODO("Not yet implemented")
}