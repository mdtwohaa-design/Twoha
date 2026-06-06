package com.example.engine

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class AnatomyLine(
    val start: Vector3D,
    val end: Vector3D,
    val system: String, // "skeletal", "nervous", "cardio_artery", "cardio_vein", "respiratory", "digestive", "body_cage"
    val isDotted: Boolean = false
)

object Anatomy3DEngine {

    /**
     * Generates all 3D line connectors based on the active state of systems.
     */
    fun generateLines(): List<AnatomyLine> {
        val lines = mutableListOf<AnatomyLine>()

        // ------------------ 1. SKELETAL SYSTEM ------------------
        // Spine segments
        val spinePoints = listOf(
            Vector3D(0.0f, 1.55f, 0.0f),  // Base of skull
            Vector3D(0.0f, 1.35f, -0.05f), // Cervical
            Vector3D(0.0f, 1.10f, -0.07f), // Thoracic upper
            Vector3D(0.0f, 0.85f, -0.05f), // Thoracic lower
            Vector3D(0.0f, 0.55f, -0.02f), // Lumbar upper
            Vector3D(0.0f, 0.25f, -0.04f), // Lumbar lower
            Vector3D(0.0f, -0.10f, -0.06f),// Sacral
            Vector3D(0.0f, -0.35f, -0.08f) // Coccyx
        )
        for (i in 0 until spinePoints.size - 1) {
            lines.add(AnatomyLine(spinePoints[i], spinePoints[i + 1], "skeletal"))
        }

        // Ribcage hoops (ellipses surrounding thoracics)
        val ribYLevels = listOf(1.30f, 1.15f, 1.00f, 0.85f)
        val ribHalfWidths = listOf(0.40f, 0.44f, 0.42f, 0.35f)
        val ribHalfDepths = listOf(0.24f, 0.26f, 0.25f, 0.20f)
        for (h in ribYLevels.indices) {
            val y = ribYLevels[h]
            val rx = ribHalfWidths[h]
            val rz = ribHalfDepths[h]
            // We connect 6 ribs on left, 6 ribs on right (curving back to front)
            val spineOrigin = spinePoints.find { (it.y - y).absoluteDifference() < 0.1f } ?: Vector3D(0f, y, -0.06f)
            
            // Left ribs
            var lastPtLeft = Vector3D(spineOrigin.x, spineOrigin.y, spineOrigin.z)
            // Right ribs
            var lastPtRight = Vector3D(spineOrigin.x, spineOrigin.y, spineOrigin.z)

            for (angleStep in 1..4) {
                val angleRad = (angleStep * (PI / 4.0)).toFloat()
                // Left Rib Point
                val ptL = Vector3D(-rx * sin(angleRad), y, -0.06f + rz * cos(angleRad))
                lines.add(AnatomyLine(lastPtLeft, ptL, "skeletal"))
                lastPtLeft = ptL

                // Right Rib Point
                val ptR = Vector3D(rx * sin(angleRad), y, -0.06f + rz * cos(angleRad))
                lines.add(AnatomyLine(lastPtRight, ptR, "skeletal"))
                lastPtRight = ptR
            }
            // Connect front ends to a Sternum line
            val sternumTopY = 1.35f
            val sternumBotY = 0.80f
            // (Sternum runs from (0, 1.35, 0.2) to (0, 0.8, 0.2))
            val sternumPointAtY = Vector3D(0f, y, 0.18f)
            lines.add(AnatomyLine(lastPtLeft, sternumPointAtY, "skeletal"))
            lines.add(AnatomyLine(lastPtRight, sternumPointAtY, "skeletal"))
        }
        // Sternum Bone central line
        lines.add(AnatomyLine(Vector3D(0f, 1.35f, 0.18f), Vector3D(0f, 0.80f, 0.18f), "skeletal"))

        // Clavicle & Shoulders
        val leftShoulder = Vector3D(-0.52f, 1.42f, -0.02f)
        val rightShoulder = Vector3D(0.52f, 1.42f, -0.02f)
        val neckC = Vector3D(0.0f, 1.38f, -0.05f)
        lines.add(AnatomyLine(neckC, leftShoulder, "skeletal"))
        lines.add(AnatomyLine(neckC, rightShoulder, "skeletal"))

        // Arms (Upper and Lower)
        val leftElbow = Vector3D(-0.68f, 0.90f, -0.08f)
        val leftWrist = Vector3D(-0.76f, 0.40f, 0.05f)
        lines.add(AnatomyLine(leftShoulder, leftElbow, "skeletal"))
        lines.add(AnatomyLine(leftElbow, leftWrist, "skeletal"))

        val rightElbow = Vector3D(0.68f, 0.90f, -0.08f)
        val rightWrist = Vector3D(0.76f, 0.40f, 0.05f)
        lines.add(AnatomyLine(rightShoulder, rightElbow, "skeletal"))
        lines.add(AnatomyLine(rightElbow, rightWrist, "skeletal"))

        // Pelvis Structural Hoop
        val leftHip = Vector3D(-0.26f, -0.32f, -0.05f)
        val rightHip = Vector3D(0.26f, -0.32f, -0.05f)
        val sacrumBase = Vector3D(0.0f, -0.35f, -0.08f)
        val pubisSymp = Vector3D(0.0f, -0.42f, 0.12f)
        
        lines.add(AnatomyLine(sacrumBase, leftHip, "skeletal"))
        lines.add(AnatomyLine(sacrumBase, rightHip, "skeletal"))
        lines.add(AnatomyLine(leftHip, pubisSymp, "skeletal"))
        lines.add(AnatomyLine(rightHip, pubisSymp, "skeletal"))

        // Legs (Thigh and Shin bone)
        val leftKnee = Vector3D(-0.28f, -1.00f, 0.05f)
        val leftAnkle = Vector3D(-0.30f, -1.68f, -0.02f)
        val leftFootToe = Vector3D(-0.30f, -1.75f, 0.20f)
        lines.add(AnatomyLine(leftHip, leftKnee, "skeletal"))
        lines.add(AnatomyLine(leftKnee, leftAnkle, "skeletal"))
        lines.add(AnatomyLine(leftAnkle, leftFootToe, "skeletal"))

        val rightKnee = Vector3D(0.28f, -1.00f, 0.05f)
        val rightAnkle = Vector3D(0.30f, -1.68f, -0.02f)
        val rightFootToe = Vector3D(0.30f, -1.75f, 0.20f)
        lines.add(AnatomyLine(rightHip, rightKnee, "skeletal"))
        lines.add(AnatomyLine(rightKnee, rightAnkle, "skeletal"))
        lines.add(AnatomyLine(rightAnkle, rightFootToe, "skeletal"))

        // Skull structural outline points (a 3D wireframe head envelope)
        generateCerebralOutline(lines)


        // ------------------ 2. NERVOUS SYSTEM ------------------
        // Brain central core is Brainstem running down as Spinal Cord
        val brainstem = Vector3D(0.0f, 1.65f, -0.02f)
        val t8Spine = Vector3D(0.0f, 0.85f, -0.05f)
        val nerveBaseSpine = Vector3D(0.0f, 0.0f, -0.05f)
        
        // Spinal Cord running inside vertebrae
        lines.add(AnatomyLine(brainstem, nerveBaseSpine, "nervous"))

        // Brachial Plexus (branching into arms)
        val leftBrachialStart = Vector3D(0.0f, 1.30f, -0.05f)
        lines.add(AnatomyLine(leftBrachialStart, Vector3D(-0.30f, 1.15f, -0.04f), "nervous"))
        lines.add(AnatomyLine(Vector3D(-0.30f, 1.15f, -0.04f), leftElbow, "nervous"))
        lines.add(AnatomyLine(leftElbow, leftWrist, "nervous", isDotted = true))

        val rightBrachialStart = Vector3D(0.0f, 1.30f, -0.05f)
        lines.add(AnatomyLine(rightBrachialStart, Vector3D(0.30f, 1.15f, -0.04f), "nervous"))
        lines.add(AnatomyLine(Vector3D(0.30f, 1.15f, -0.04f), rightElbow, "nervous"))
        lines.add(AnatomyLine(rightElbow, rightWrist, "nervous", isDotted = true))

        // Intercostal abdominal nerves branching from spine
        val thoracicNervesY = listOf(1.10f, 0.95f, 0.80f, 0.65f)
        for (ny in thoracicNervesY) {
            lines.add(AnatomyLine(Vector3D(0.0f, ny, -0.05f), Vector3D(-0.25f, ny - 0.05f, 0.05f), "nervous"))
            lines.add(AnatomyLine(Vector3D(0.0f, ny, -0.05f), Vector3D(0.25f, ny - 0.05f, 0.05f), "nervous"))
        }

        // Lumbar plexus / Sciatic nerve branching down hips and back of legs
        val sciaticSacral = Vector3D(0.0f, -0.15f, -0.05f)
        val lSciaticElongated = Vector3D(-0.25f, -0.6f, -0.02f)
        lines.add(AnatomyLine(sciaticSacral, lSciaticElongated, "nervous"))
        lines.add(AnatomyLine(lSciaticElongated, leftKnee, "nervous"))
        lines.add(AnatomyLine(leftKnee, leftAnkle, "nervous", isDotted = true))

        val rSciaticElongated = Vector3D(0.25f, -0.6f, -0.02f)
        lines.add(AnatomyLine(sciaticSacral, rSciaticElongated, "nervous"))
        lines.add(AnatomyLine(rSciaticElongated, rightKnee, "nervous"))
        lines.add(AnatomyLine(rightKnee, rightAnkle, "nervous", isDotted = true))

        // Sympathetic trunks running adjacent to spine
        lines.add(AnatomyLine(Vector3D(-0.06f, 1.30f, -0.05f), Vector3D(-0.06f, 0.10f, -0.05f), "nervous", isDotted = true))
        lines.add(AnatomyLine(Vector3D(0.06f, 1.30f, -0.05f), Vector3D(0.06f, 0.10f, -0.05f), "nervous", isDotted = true))


        // ------------------ 3. CARDIOVASCULAR (CIRCULATORY) ------------------
        val heartCenter = Vector3D(-0.1f, 1.05f, 0.17f)
        
        // Red Arterial Tree (Aortic Arch & Descending Aorta)
        val aorticArchPeak = Vector3D(-0.08f, 1.18f, 0.15f)
        val carotidBranchY = Vector3D(-0.04f, 1.35f, 0.10f)
        // Link heart to arch
        lines.add(AnatomyLine(heartCenter, aorticArchPeak, "cardio_artery"))
        // Left/Right Carotid arteries branching upwards to brain
        lines.add(AnatomyLine(aorticArchPeak, carotidBranchY, "cardio_artery"))
        lines.add(AnatomyLine(carotidBranchY, Vector3D(-0.07f, 1.55f, 0.05f), "cardio_artery"))
        lines.add(AnatomyLine(carotidBranchY, Vector3D(0.07f, 1.55f, 0.05f), "cardio_artery"))

        // Subclavian Arteries supplying arms
        lines.add(AnatomyLine(aorticArchPeak, Vector3D(-0.35f, 1.25f, 0.02f), "cardio_artery"))
        lines.add(AnatomyLine(Vector3D(-0.35f, 1.25f, 0.02f), leftElbow, "cardio_artery"))
        lines.add(AnatomyLine(aorticArchPeak, Vector3D(0.35f, 1.25f, 0.02f), "cardio_artery"))
        lines.add(AnatomyLine(Vector3D(0.35f, 1.25f, 0.02f), rightElbow, "cardio_artery"))

        // Descending Abdominal Aorta
        val renalArterySplit = Vector3D(-0.03f, 0.45f, 0.05f)
        lines.add(AnatomyLine(aorticArchPeak, renalArterySplit, "cardio_artery"))
        // Renal branches supplying kidneys
        lines.add(AnatomyLine(renalArterySplit, Vector3D(-0.20f, 0.45f, -0.14f), "cardio_artery")) // left kidney
        lines.add(AnatomyLine(renalArterySplit, Vector3D(0.20f, 0.42f, -0.14f), "cardio_artery"))  // right kidney

        // Iliac arteries branching down to legs
        val iliacBifurcation = Vector3D(-0.02f, 0.10f, 0.04f)
        lines.add(AnatomyLine(renalArterySplit, iliacBifurcation, "cardio_artery"))
        lines.add(AnatomyLine(iliacBifurcation, leftHip, "cardio_artery"))
        lines.add(AnatomyLine(leftHip, leftKnee, "cardio_artery"))
        lines.add(AnatomyLine(iliacBifurcation, rightHip, "cardio_artery"))
        lines.add(AnatomyLine(rightHip, rightKnee, "cardio_artery"))

        // Blue Venous Tree (Superior and Inferior Vena Cava)
        val rightAtrium = Vector3D(-0.08f, 1.05f, 0.13f)
        val svcTop = Vector3D(-0.06f, 1.25f, 0.11f)
        lines.add(AnatomyLine(svcTop, rightAtrium, "cardio_vein"))
        // Jugular veins coming down from cerebral tissue
        lines.add(AnatomyLine(Vector3D(-0.1f, 1.55f, 0.08f), svcTop, "cardio_vein"))
        lines.add(AnatomyLine(Vector3D(0.1f, 1.55f, 0.08f), svcTop, "cardio_vein"))

        // Subclavian Veins from arms
        lines.add(AnatomyLine(leftElbow, Vector3D(-0.35f, 1.23f, -0.01f), "cardio_vein"))
        lines.add(AnatomyLine(Vector3D(-0.35f, 1.23f, -0.01f), svcTop, "cardio_vein"))
        lines.add(AnatomyLine(rightElbow, Vector3D(0.35f, 1.23f, -0.01f), "cardio_vein"))
        lines.add(AnatomyLine(Vector3D(0.35f, 1.23f, -0.01f), svcTop, "cardio_vein"))

        // Inferior Vena Cava (IVC)
        val ivcRenalUnion = Vector3D(-0.08f, 0.45f, 0.04f)
        lines.add(AnatomyLine(ivcRenalUnion, rightAtrium, "cardio_vein"))
        // Renal veins
        lines.add(AnatomyLine(Vector3D(-0.20f, 0.45f, -0.14f), ivcRenalUnion, "cardio_vein")) // from left kidney
        lines.add(AnatomyLine(Vector3D(0.20f, 0.42f, -0.14f), ivcRenalUnion, "cardio_vein"))  // from right kidney

        // Iliac veins
        val ivcBifurcation = Vector3D(-0.08f, 0.10f, 0.02f)
        lines.add(AnatomyLine(ivcBifurcation, ivcRenalUnion, "cardio_vein"))
        lines.add(AnatomyLine(leftHip, ivcBifurcation, "cardio_vein"))
        lines.add(AnatomyLine(leftKnee, leftHip, "cardio_vein"))
        lines.add(AnatomyLine(rightHip, ivcBifurcation, "cardio_vein"))
        lines.add(AnatomyLine(rightKnee, rightHip, "cardio_vein"))


        // ------------------ 4. RESPIRATORY SYSTEM ------------------
        val mouthNose = Vector3D(0.0f, 1.62f, 0.18f)
        val larynx = Vector3D(0.0f, 1.48f, 0.14f)
        val carinaTrachea = Vector3D(0.0f, 1.15f, 0.12f)
        
        // Trachea airway tube
        lines.add(AnatomyLine(mouthNose, larynx, "respiratory"))
        lines.add(AnatomyLine(larynx, carinaTrachea, "respiratory"))

        // Left Bronchus branching to Left Lung center
        val leftBronchusBr = Vector3D(-0.16f, 1.08f, 0.12f)
        lines.add(AnatomyLine(carinaTrachea, leftBronchusBr, "respiratory"))
        lines.add(AnatomyLine(leftBronchusBr, Vector3D(-0.24f, 1.03f, 0.12f), "respiratory"))

        // Right Bronchus branching to Right Lung center
        val rightBronchusBr = Vector3D(0.16f, 1.08f, 0.12f)
        lines.add(AnatomyLine(carinaTrachea, rightBronchusBr, "respiratory"))
        lines.add(AnatomyLine(rightBronchusBr, Vector3D(0.24f, 1.03f, 0.12f), "respiratory"))


        // ------------------ 5. DIGESTIVE SYSTEM ------------------
        // Pharynx
        val pharynx = Vector3D(0.0f, 1.55f, 0.05f)
        lines.add(AnatomyLine(mouthNose, pharynx, "digestive"))
        
        // Esophagus tube passing posterior to cardio
        val gastroesophagealJunc = Vector3D(-0.06f, 0.76f, 0.04f)
        lines.add(AnatomyLine(pharynx, gastroesophagealJunc, "digestive"))

        // Stomach outline pathway leading to Duodenum
        val stomachGaster = Vector3D(-0.16f, 0.68f, 0.14f)
        val pylorusJunc = Vector3D(0.02f, 0.52f, 0.12f)
        lines.add(AnatomyLine(gastroesophagealJunc, stomachGaster, "digestive"))
        lines.add(AnatomyLine(stomachGaster, pylorusJunc, "digestive"))

        // Small Intestine winding paths
        val loops = listOf(
            Vector3D(-0.05f, 0.38f, 0.12f),
            Vector3D(0.05f, 0.34f, 0.14f),
            Vector3D(-0.08f, 0.28f, 0.15f),
            Vector3D(0.06f, 0.24f, 0.12f),
            Vector3D(-0.03f, 0.20f, 0.16f)
        )
        lines.add(AnatomyLine(pylorusJunc, loops[0], "digestive"))
        for (i in 0 until loops.size - 1) {
            lines.add(AnatomyLine(loops[i], loops[i + 1], "digestive"))
        }

        // Connect small intestine to Large Intestine at cecum
        val cecumIleo = Vector3D(0.18f, 0.12f, 0.10f)
        lines.add(AnatomyLine(loops.last(), cecumIleo, "digestive"))

        // Large Colon Ascent-Transverse-Descent Frame
        val colicFlexureR = Vector3D(0.18f, 0.40f, 0.10f)
        val colicFlexureL = Vector3D(-0.18f, 0.40f, 0.10f)
        val sigmoidColon = Vector3D(-0.10f, -0.05f, 0.12f)
        val rectumAnal = Vector3D(0.0f, -0.15f, 0.08f)

        lines.add(AnatomyLine(cecumIleo, colicFlexureR, "digestive")) // ascending colon
        lines.add(AnatomyLine(colicFlexureR, colicFlexureL, "digestive")) // transverse colon
        lines.add(AnatomyLine(colicFlexureL, sigmoidColon, "digestive")) // descending colon
        lines.add(AnatomyLine(sigmoidColon, rectumAnal, "digestive"))


        // ------------------ 6. AMBIENT BODY HOLOGRAPHIC CAGE ------------------
        generateBodyContourHoops(lines)

        return lines
    }

    private fun generateCerebralOutline(lines: MutableList<AnatomyLine>) {
        val yCenter = 1.80f
        val radius = 0.21f
        // Vertical crown hoop
        var lastPtCrown = Vector3D(0f, yCenter + radius, 0f)
        for (step in 1..8) {
            val theta = (step * (2 * PI / 8.0)).toFloat()
            val pt = Vector3D(0f, yCenter + radius * cos(theta), radius * sin(theta))
            lines.add(AnatomyLine(lastPtCrown, pt, "body_cage", isDotted = true))
            lastPtCrown = pt
        }
        
        // Transverse horizontal circumference hoop
        var lastPtTransv = Vector3D(radius, yCenter, 0f)
        for (step in 1..8) {
            val theta = (step * (2 * PI / 8.0)).toFloat()
            val pt = Vector3D(radius * cos(theta), yCenter, radius * sin(theta))
            lines.add(AnatomyLine(lastPtTransv, pt, "body_cage", isDotted = true))
            lastPtTransv = pt
        }
    }

    private fun generateBodyContourHoops(lines: MutableList<AnatomyLine>) {
        // Horizontal anatomical sections running along the body to construct a beautiful neon-glowing mesh
        val cageYLevels = listOf(1.58f, 1.45f, 1.25f, 1.10f, 0.90f, 0.70f, 0.50f, 0.25f, -0.10f, -0.35f)
        val cageHalfWidths = listOf(0.14f, 0.50f, 0.52f, 0.50f, 0.44f, 0.38f, 0.36f, 0.35f, 0.35f, 0.36f)
        val cageHalfDepths = listOf(0.13f, 0.22f, 0.24f, 0.25f, 0.24f, 0.22f, 0.20f, 0.18f, 0.17f, 0.18f)

        for (h in cageYLevels.indices) {
            val y = cageYLevels[h]
            val rx = cageHalfWidths[h]
            val rz = cageHalfDepths[h]

            // We construct an elliptical loop of 8 vertices
            val loopPoints = mutableListOf<Vector3D>()
            for (step in 0 until 8) {
                val theta = (step * (2 * PI / 8.0)).toFloat()
                loopPoints.add(Vector3D(rx * cos(theta), y, rz * sin(theta)))
            }

            for (p in 0 until 8) {
                val nextP = (p + 1) % 8
                lines.add(AnatomyLine(loopPoints[p], loopPoints[nextP], "body_cage", isDotted = true))
            }
        }
    }

    private fun Float.absoluteDifference(): Float {
        return if (this < 0) -this else this
    }
}
