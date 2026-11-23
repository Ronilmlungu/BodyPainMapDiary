package com.ronil.body_painmap_diary.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun TrendChart(
    data: List<Int>,
    modifier: Modifier = Modifier
) {
    val lineColor = Color(0xFF0D47A1) // Deep Navy from design system
    val gridColor = Color(0xFFB0BEC5) // Soft grey for grid

    // The gradient fill under the line
    val gradientFill = Brush.verticalGradient(
        colors = listOf(
            lineColor.copy(alpha = 0.25f),
            lineColor.copy(alpha = 0.0f)
        )
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 1. Draw dotted horizontal grid lines
        val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        for (level in 0..4) {
            val y = height - (height * (level / 4f))
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dashPathEffect
            )
        }

        if (data.isEmpty()) return@Canvas

        val stepX = if (data.size > 1) width / (data.size - 1) else width
        val linePath = Path()
        val fillPath = Path()

        fillPath.moveTo(0f, height) // Start fill at bottom left

        data.forEachIndexed { index, level ->
            val x = index * stepX
            val y = height - (height * (level / 4f))
            val currentPoint = Offset(x, y)

            if (index == 0) linePath.moveTo(currentPoint.x, currentPoint.y)
            else linePath.lineTo(currentPoint.x, currentPoint.y)

            fillPath.lineTo(currentPoint.x, currentPoint.y)

            // Draw the dot: White center, Blue stroke
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = currentPoint
            )
            drawCircle(
                color = lineColor,
                radius = 3.dp.toPx(),
                center = currentPoint,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        fillPath.lineTo(width, height)
        fillPath.close()

        drawPath(path = fillPath, brush = gradientFill)
        drawPath(path = linePath, color = lineColor, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
    }
}