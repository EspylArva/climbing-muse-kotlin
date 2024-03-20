package com.iteration.climbingmuse.analysis

import android.graphics.Color
import android.graphics.Paint
import androidx.lifecycle.MutableLiveData
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import timber.log.Timber
import kotlin.math.acos
import kotlin.math.sqrt
import com.iteration.climbingmuse.analysis.ComputerVisionDecorator.CanvasTextInfo

class AngleDecorator(private val showAngles: MutableLiveData<Boolean>) : ComputerVisionDecorator {

    private val anglePaint = Paint().apply {
        color = Color.YELLOW
        strokeWidth = 12F
        style = Paint.Style.FILL
    }

    // According to tedbergstrand's description:
    // The numbers by the joints are estimated joint angles.
    override fun process(data: PoseLandmarkerResult) {
        super.process(data)
        Timber.d("Show angles: %s", showAngles.value)
        if (showAngles.value == false) {
            return
        }

        //FIXME: This should be bound to the technology, not the logic
        if(data.landmarks().size > 0 && data.landmarks()[0].size > 0) {
            val lShoulder = data.landmarks()[0][VideoProcessor.MediaPipeJoint.LEFT_SHOULDER]
            val lElbow = data.landmarks()[0][VideoProcessor.MediaPipeJoint.LEFT_ELBOW]
            val lHip = data.landmarks()[0][VideoProcessor.MediaPipeJoint.LEFT_HIP]
            val lKnee = data.landmarks()[0][VideoProcessor.MediaPipeJoint.LEFT_KNEE]
            val lAnkle = data.landmarks()[0][VideoProcessor.MediaPipeJoint.LEFT_ANKLE]
            val rShoulder = data.landmarks()[0][VideoProcessor.MediaPipeJoint.RIGHT_SHOULDER]
            val rHip = data.landmarks()[0][VideoProcessor.MediaPipeJoint.RIGHT_HIP]
            val rKnee = data.landmarks()[0][VideoProcessor.MediaPipeJoint.RIGHT_KNEE]
            val rAnkle = data.landmarks()[0][VideoProcessor.MediaPipeJoint.RIGHT_ANKLE]


            texts.addAll(
                arrayOf(
                    CanvasTextInfo(calculateJointAngle(VideoProcessor.MediaPipeJoint.LEFT_KNEE, data).toString(), lKnee.x(), lKnee.y(), anglePaint),
                    CanvasTextInfo(calculateJointAngle(VideoProcessor.MediaPipeJoint.RIGHT_KNEE, data).toString(), rKnee.x(), rKnee.y(), anglePaint),
                    CanvasTextInfo(calculateJointAngle(VideoProcessor.MediaPipeJoint.LEFT_HIP, data).toString(), lHip.x(), lHip.y(), anglePaint),
                    CanvasTextInfo(calculateJointAngle(VideoProcessor.MediaPipeJoint.RIGHT_HIP, data).toString(), rHip.x(), rHip.y(), anglePaint),
                    CanvasTextInfo(calculateJointAngle(VideoProcessor.MediaPipeJoint.LEFT_SHOULDER, data).toString(), lShoulder.x(), lShoulder.y(), anglePaint),
                    CanvasTextInfo(calculateJointAngle(VideoProcessor.MediaPipeJoint.RIGHT_SHOULDER, data).toString(), rShoulder.x(), rShoulder.y(), anglePaint),
                    CanvasTextInfo(calculateJointAngle(VideoProcessor.MediaPipeJoint.LEFT_ELBOW, data).toString(), lElbow.x(), lElbow.y(), anglePaint),
                    CanvasTextInfo(calculateJointAngle(VideoProcessor.MediaPipeJoint.RIGHT_ELBOW, data).toString(), rShoulder.x(), rShoulder.y(), anglePaint),
                    CanvasTextInfo(calculateJointAngle(VideoProcessor.MediaPipeJoint.LEFT_ANKLE, data).toString(), lAnkle.x(), lAnkle.y(), anglePaint),
                    CanvasTextInfo(calculateJointAngle(VideoProcessor.MediaPipeJoint.RIGHT_ANKLE, data).toString(), rAnkle.x(), rAnkle.y(), anglePaint)
            ))
        } else {
            Timber.d("Angles | Nothing detected?")
        }
    }

    private val texts = arrayListOf<CanvasTextInfo>()
    override val textsToDraw: ArrayList<CanvasTextInfo>
        get() = texts
    override val pointsToDraw: ArrayList<ComputerVisionDecorator.CanvasPointInfo>
        get() = arrayListOf()
    override val linesToDraw: ArrayList<ComputerVisionDecorator.CanvasLineInfo>
        get() = arrayListOf()
    override val pathsToDraw: ArrayList<ComputerVisionDecorator.CanvasPathInfo>
        get() = arrayListOf()

    companion object {

        fun calculateJointAngle(middleJointId: Int, data: PoseLandmarkerResult) : Float {
            //            val head = data.landmarks()[0][VideoProcessor.Joint.HEAD.mpId]
            val lShoulder   = data.landmarks()[0][VideoProcessor.MediaPipeJoint.LEFT_SHOULDER]
            val lElbow      = data.landmarks()[0][VideoProcessor.MediaPipeJoint.LEFT_ELBOW]
            val lHand       = data.landmarks()[0][VideoProcessor.MediaPipeJoint.LEFT_HAND]
            val lHip        = data.landmarks()[0][VideoProcessor.MediaPipeJoint.LEFT_HIP]
            val lKnee       = data.landmarks()[0][VideoProcessor.MediaPipeJoint.LEFT_KNEE]
            val lAnkle      = data.landmarks()[0][VideoProcessor.MediaPipeJoint.LEFT_ANKLE]
//            val lHeel      = data.landmarks()[0][VideoProcessor.Joint.LEFT_HEEL]
            val lToe        = data.landmarks()[0][VideoProcessor.MediaPipeJoint.LEFT_TOE]
            val rShoulder   = data.landmarks()[0][VideoProcessor.MediaPipeJoint.RIGHT_SHOULDER]
            val rElbow      = data.landmarks()[0][VideoProcessor.MediaPipeJoint.RIGHT_ELBOW]
            val rHand       = data.landmarks()[0][VideoProcessor.MediaPipeJoint.RIGHT_HAND]
            val rHip        = data.landmarks()[0][VideoProcessor.MediaPipeJoint.RIGHT_HIP]
            val rKnee       = data.landmarks()[0][VideoProcessor.MediaPipeJoint.RIGHT_KNEE]
            val rAnkle      = data.landmarks()[0][VideoProcessor.MediaPipeJoint.RIGHT_ANKLE]
//            val rHeel      = data.landmarks()[0][VideoProcessor.Joint.RIGHT_HEEL]
            val rToe        = data.landmarks()[0][VideoProcessor.MediaPipeJoint.RIGHT_TOE]

            return when(middleJointId) {
                VideoProcessor.MediaPipeJoint.LEFT_KNEE      -> calculateJointAngle(lHip, lKnee, lAnkle)
                VideoProcessor.MediaPipeJoint.RIGHT_KNEE     -> calculateJointAngle(rHip, rKnee, rAnkle)
                VideoProcessor.MediaPipeJoint.LEFT_HIP       -> calculateJointAngle(lShoulder, lHip, lKnee)
                VideoProcessor.MediaPipeJoint.RIGHT_HIP      -> calculateJointAngle(rShoulder, rHip, rKnee)
                VideoProcessor.MediaPipeJoint.LEFT_SHOULDER  -> calculateJointAngle(lElbow, lShoulder, lHip)
                VideoProcessor.MediaPipeJoint.RIGHT_SHOULDER -> calculateJointAngle(rElbow, rShoulder, rHip)
                VideoProcessor.MediaPipeJoint.LEFT_ELBOW     -> calculateJointAngle(lShoulder, lElbow, lHand)
                VideoProcessor.MediaPipeJoint.RIGHT_ELBOW    -> calculateJointAngle(rShoulder, rElbow, rHand)
                VideoProcessor.MediaPipeJoint.LEFT_ANKLE     -> calculateJointAngle(lToe, lAnkle, lKnee)
                VideoProcessor.MediaPipeJoint.RIGHT_ANKLE    -> calculateJointAngle(rToe, rAnkle, rKnee)
                else -> return 0f
            }

        }

        /**
         * Calculates the angle between three 3D points which represent joints.
         * This method returns a positive value, always inferior to 180, because:
         * - the unit used are degrees
         * - as this methods calculates joint angles, the angle should never bend over 180°
         *
         * @param pointA the first outer joint
         * @param pointB the inner joint, which angle we want
         * @param pointC the second outer joint
         * @return the angle value, in degrees
         */
        private fun calculateJointAngle(pointA: NormalizedLandmark, pointB: NormalizedLandmark, pointC: NormalizedLandmark) : Float {
        // Based on https://stackoverflow.com/questions/19729831/angle-between-3-points-in-3d-space
            val v1 = object {
                val x = pointA.x() - pointB.x()
                val y = pointA.y() - pointB.y()
                val z = pointA.z() - pointB.z()
            }
            val v2 = object {
                val x = pointC.x() - pointB.x()
                val y = pointC.y() - pointB.y()
                val z = pointC.z() - pointB.z()
            }

            val v1mag = sqrt(v1.x * v1.x + v1.y * v1.y + v1.z * v1.z)
            val v1norm = object {
                val x = v1.x / v1mag
                val y = v1.y / v1mag
                val z = v1.z / v1mag
            }
            val v2mag = sqrt(v2.x * v2.x + v2.y * v2.y + v2.z * v2.z)
            val v2norm = object {
                val x = v2.x / v2mag
                val y = v2.y / v2mag
                val z = v2.z / v2mag
            }
            // Then calculate the dot product:
            val res = v1norm.x * v2norm.x + v1norm.y * v2norm.y + v1norm.z * v2norm.z
            val degree: Float = (acos(res) * 180 / kotlin.math.PI).toFloat()
            // And finally, recover the angle
            return kotlin.math.min(degree, 360-degree)
        }
    }
}