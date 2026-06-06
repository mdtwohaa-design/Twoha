package com.example.engine

import kotlin.math.cos
import kotlin.math.sin

data class Vector3D(val x: Float, val y: Float, val z: Float) {
    operator fun plus(other: Vector3D) = Vector3D(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vector3D) = Vector3D(x - other.x, y - other.y, z - other.z)
    operator fun times(factor: Float) = Vector3D(x * factor, y * factor, z * factor)

    /**
     * Rotates this vector in 3D around the X-axis (Pitch) and Y-axis (Yaw).
     * Pitch represents vertical rotation, Yaw represents horizontal rotation.
     */
    fun rotate(pitchRadians: Float, yawRadians: Float): Vector3D {
        // 1. Rotate around X-axis (Pitch)
        val cosP = cos(pitchRadians)
        val sinP = sin(pitchRadians)
        
        val y1 = y * cosP - z * sinP
        val z1 = y * sinP + z * cosP
        val x1 = x

        // 2. Rotate around Y-axis (Yaw)
        val cosY = cos(yawRadians)
        val sinY = sin(yawRadians)
        
        val x2 = x1 * cosY + z1 * sinY
        val z2 = -x1 * sinY + z1 * cosY
        val y2 = y1

        return Vector3D(x2, y2, z2)
    }

    /**
     * Projects a 3D coordinate point onto the 2D plane (represented as x and y)
     * using a perspective projection model.
     * returns a Pair of ScreenXOffset and ScreenYOffset, along with Depth (Z) for depth sorting.
     */
    fun project(
        centerX: Float,
        centerY: Float,
        scale: Float,
        cameraDistance: Float = 4.0f
    ): ProjectedPoint {
        val projectedFactor = cameraDistance / (cameraDistance - z)
        val px = centerX + x * projectedFactor * scale
        val py = centerY - y * projectedFactor * scale // Invert Y as computer screen is top-to-bottom
        return ProjectedPoint(px, py, z)
    }
}

data class ProjectedPoint(val x: Float, val y: Float, val depth: Float)
