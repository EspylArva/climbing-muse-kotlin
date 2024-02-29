package com.iteration.climbingmuse.analysis

import android.graphics.Point
import com.google.mediapipe.tasks.components.containers.Landmark
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import timber.log.Timber
import kotlin.math.acos
import kotlin.math.sqrt

class AngleDecorator : ComputerVisionDecorator {

    // According to tedbergstrand's description:
    // The numbers by the joints are estimated joint angles.
    override fun process(data: PoseLandmarkerResult) {
        //FIXME: This should be bound to the technology, not the logic
        if(data.worldLandmarks().size > 0 && data.worldLandmarks()[0].size > 0) {
            val head = data.worldLandmarks()[0][VideoProcessor.Joint.HEAD.mpId]
            val lShoulder = data.worldLandmarks()[0][VideoProcessor.Joint.LEFT_SHOULDER.mpId]
            val lElbow = data.worldLandmarks()[0][VideoProcessor.Joint.LEFT_ELBOW.mpId]
            val lHand = data.worldLandmarks()[0][VideoProcessor.Joint.LEFT_HAND.mpId]
            val lHip = data.worldLandmarks()[0][VideoProcessor.Joint.LEFT_HIP.mpId]
            val lKnee = data.worldLandmarks()[0][VideoProcessor.Joint.LEFT_KNEE.mpId]
            val lAnkle = data.worldLandmarks()[0][VideoProcessor.Joint.LEFT_ANKLE.mpId]
            val lHeel = data.worldLandmarks()[0][VideoProcessor.Joint.LEFT_HEEL.mpId]
            val lToe = data.worldLandmarks()[0][VideoProcessor.Joint.LEFT_TOE.mpId]
            val rShoulder = data.worldLandmarks()[0][VideoProcessor.Joint.RIGHT_SHOULDER.mpId]
            val rElbow = data.worldLandmarks()[0][VideoProcessor.Joint.RIGHT_ELBOW.mpId]
            val rHand = data.worldLandmarks()[0][VideoProcessor.Joint.RIGHT_HAND.mpId]
            val rHip = data.worldLandmarks()[0][VideoProcessor.Joint.RIGHT_HIP.mpId]
            val rKnee = data.worldLandmarks()[0][VideoProcessor.Joint.RIGHT_KNEE.mpId]
            val rAnkle = data.worldLandmarks()[0][VideoProcessor.Joint.RIGHT_ANKLE.mpId]
            val rHeel = data.worldLandmarks()[0][VideoProcessor.Joint.RIGHT_HEEL.mpId]
            val rToe = data.worldLandmarks()[0][VideoProcessor.Joint.RIGHT_TOE.mpId]

            val left_knee_angle = calculateJointAngle(lHip, lKnee, lAnkle)
            val right_knee_angle = calculateJointAngle(rHip, rKnee, rAnkle)
            val left_hip_angle = calculateJointAngle(lShoulder, lHip, lKnee)
            val right_hip_angle = calculateJointAngle(rShoulder, rHip, rKnee)
            val left_shoulder_angle = calculateJointAngle(lElbow, lShoulder, lHip)
            val right_shoulder_angle = calculateJointAngle(rElbow, rShoulder, rHip)
            val left_elbow_angle = calculateJointAngle(lShoulder, lElbow, lHand)
            val right_elbow_angle = calculateJointAngle(rShoulder, rElbow, rHand)
            val left_ankle_angle = calculateJointAngle(lToe, lAnkle, lKnee)
            val right_ankle_angle = calculateJointAngle(rToe, rAnkle, rKnee)

            Timber.d("Angles | [left knee:%s] [right knee:%s]", left_knee_angle, right_knee_angle)
        } else {
            Timber.d("Angles | Nothing detected?")
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
    private fun calculateJointAngle(pointA: Landmark, pointB: Landmark, pointC: Landmark) : Float {
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