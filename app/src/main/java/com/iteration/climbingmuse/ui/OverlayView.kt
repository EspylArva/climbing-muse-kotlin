package com.iteration.climbingmuse.ui
/*
 * Copyright 2023 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import com.iteration.climbingmuse.R
import com.iteration.climbingmuse.analysis.ComputerVisionDecorator
import timber.log.Timber
import java.util.ArrayList
import kotlin.math.max
import kotlin.math.min

class OverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private var decorators = arrayListOf<ComputerVisionDecorator>()
    private var pointPaint = Paint()
    private var linePaint = Paint()

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    fun clear() {
        decorators = arrayListOf()
        pointPaint.reset()
        linePaint.reset()
        invalidate()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        val xScalingFactor = imageWidth * scaleFactor
        val yScalingFactor = imageHeight * scaleFactor
        Timber.d("Scale: %sx%s. Decorator list size: %s (%s)", xScalingFactor, yScalingFactor, decorators.size, decorators)

        decorators.forEach { decorator ->
            Timber.v("Overlay displaying data from %s", decorator)
            decorator.pathsToDraw.forEach { pathInfo ->
                Timber.v("    - Drawing %s", pathInfo)
                val path = Path().apply {
                    moveTo(pathInfo.path[0].first * xScalingFactor, pathInfo.path[0].second * yScalingFactor)
                    for (i in 0..pathInfo.path.size-2) {
                        lineTo(pathInfo.path[i+1].first * xScalingFactor, pathInfo.path[i+1].second * yScalingFactor)
                    }
                    lineTo(pathInfo.path[0].first * xScalingFactor, pathInfo.path[0].second * yScalingFactor)
                }
                path.close()
                canvas.drawPath(path, pathInfo.paint)
            } //TODO
            decorator.linesToDraw.forEach {
                Timber.v("    - Drawing %s", it)
                canvas.drawLine(it.normalizedStartX * xScalingFactor, it.normalizedStartY * yScalingFactor,
                    it.normalizedEndX * xScalingFactor, it.normalizedEndY * yScalingFactor,
                    it.paint.apply { color =  ContextCompat.getColor(context!!, R.color.md_theme_light_primary)}) // FIXME: should be removed
            }
            decorator.pointsToDraw.forEach {
                Timber.v("    - Drawing %s", it)
                canvas.drawPoint(it.normalizedX * xScalingFactor, it.normalizedY * yScalingFactor, it.paint)
            }
            decorator.textsToDraw.forEach {
                Timber.v("    - Drawing %s", it)
                canvas.drawText(it.text, it.normalizedX * xScalingFactor, it.normalizedY * yScalingFactor, it.paint)
            }
        }
    }

    fun setResults(
        decorators: ArrayList<ComputerVisionDecorator>,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.IMAGE
    ) {
        // results = poseLandmarkerResults
        this.decorators = decorators

        this.imageHeight = imageHeight
        this.imageWidth = imageWidth

        scaleFactor = when (runningMode) {
            RunningMode.IMAGE,
            RunningMode.VIDEO -> {
                min(width * 1f / imageWidth, height * 1f / imageHeight)
            }
            RunningMode.LIVE_STREAM -> {
                // PreviewView is in FILL_START mode. So we need to scale up the
                // landmarks to match with the size that the captured images will be
                // displayed.
                max(width * 1f / imageWidth, height * 1f / imageHeight)
            }
        }
        invalidate()
    }

    companion object {
        private const val LANDMARK_STROKE_WIDTH = 12F
    }
}