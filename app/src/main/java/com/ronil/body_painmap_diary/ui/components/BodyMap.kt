package com.ronil.body_painmap_diary.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

private data class BodyPoint(val label: String, val x: Float, val y: Float)

// The points for the Front View
private val frontPoints = listOf(
    BodyPoint("Head",       0.50f, 0.08f),
    BodyPoint("Neck",       0.50f, 0.16f), // <-- Added Neck
    BodyPoint("Chest",      0.50f, 0.26f),
    BodyPoint("Belly",      0.50f, 0.40f),
    BodyPoint("Right Hand", 0.16f, 0.52f),
    BodyPoint("Left Hand",  0.84f, 0.52f),
    BodyPoint("Hips",       0.50f, 0.55f),
    BodyPoint("Right Knee", 0.40f, 0.76f),
    BodyPoint("Left Knee",  0.60f, 0.76f)
)

// The points for the Back View
private val backPoints = listOf(
    BodyPoint("Head (Back)", 0.50f, 0.08f),
    BodyPoint("Neck",        0.50f, 0.16f), // <-- Added Neck
    BodyPoint("Upper Back",  0.50f, 0.26f),
    BodyPoint("Lower Back",  0.50f, 0.40f),
    BodyPoint("Right Hand",  0.16f, 0.52f),
    BodyPoint("Left Hand",   0.84f, 0.52f),
    BodyPoint("Tailbone",    0.50f, 0.55f),
    BodyPoint("Right Calf",  0.40f, 0.76f),
    BodyPoint("Left Calf",   0.60f, 0.76f)
)

@Composable
fun BodyMap(
    selectedPart: String?,
    onPartSelected: (String) -> Unit,
    isFront: Boolean, // <-- New parameter to track the toggle state
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth().aspectRatio(0.55f),
        contentAlignment = Alignment.TopStart
    ) {
        val w = maxWidth
        val h = maxHeight

        // Draw the body outline behind everything
        BodySilhouette(Modifier.fillMaxSize())

        // Choose which list of points to use based on the toggle!
        val currentPoints = if (isFront) frontPoints else backPoints

        // Draw the touch circles
        currentPoints.forEach { point ->
            TouchTarget(
                selected = point.label == selectedPart,
                onClick = { onPartSelected(point.label) },
                modifier = Modifier.offset(
                    x = w * point.x - 22.dp,
                    y = h * point.y - 22.dp
                )
            )
        }
    }
}

@Composable
private fun BodySilhouette(modifier: Modifier = Modifier) {
    val stroke = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val sw = w * 0.025f

        // Head
        drawCircle(stroke, radius = w * 0.09f, center = Offset(w * 0.5f, h * 0.09f), style = Stroke(width = sw))
        // Torso
        drawRoundRect(stroke, topLeft = Offset(w * 0.40f, h * 0.18f), size = Size(w * 0.20f, h * 0.38f), cornerRadius = CornerRadius(w * 0.10f, w * 0.10f), style = Stroke(width = sw))
        // Arms
        drawLine(stroke, start = Offset(w * 0.40f, h * 0.24f), end = Offset(w * 0.16f, h * 0.52f), strokeWidth = sw, cap = StrokeCap.Round)
        drawLine(stroke, start = Offset(w * 0.60f, h * 0.24f), end = Offset(w * 0.84f, h * 0.52f), strokeWidth = sw, cap = StrokeCap.Round)
        // Legs
        drawLine(stroke, start = Offset(w * 0.44f, h * 0.54f), end = Offset(w * 0.38f, h * 0.94f), strokeWidth = sw, cap = StrokeCap.Round)
        drawLine(stroke, start = Offset(w * 0.56f, h * 0.54f), end = Offset(w * 0.62f, h * 0.94f), strokeWidth = sw, cap = StrokeCap.Round)
    }
}

@Composable
private fun TouchTarget(selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val ring = MaterialTheme.colorScheme.primary
    val fill = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val glowColor = Color(0xFF1976D2).copy(alpha = 0.25f)

    Box(
        modifier = modifier
            .size(44.dp)
            .then(if (selected) Modifier.background(glowColor, CircleShape) else Modifier)
            .background(fill, CircleShape)
            .border(2.dp, ring, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        } else {
            Box(Modifier.size(10.dp).background(ring, CircleShape))
        }
    }
}