package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(
    val initialX: Float,
    val initialY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float,
    val isStar: Boolean
)

@Composable
fun ConfettiCelebration(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isActive) return

    val progress = remember { Animatable(0f) }

    val particles = remember {
        val colors = listOf(
            Color(0xFFFFD700), // Gold
            Color(0xFFFF5722), // Deep Orange
            Color(0xFFE91E63), // Pink
            Color(0xFF4CAF50), // Green
            Color(0xFF03A9F4), // Light Blue
            Color(0xFF9C27B0), // Purple
            Color(0xFFFFEB3B)  // Yellow
        )
        List(70) {
            val random = Random(it * 31)
            Particle(
                initialX = random.nextFloat(), // 0.0 to 1.0 (screen width ratio)
                initialY = -0.1f - random.nextFloat() * 0.2f, // start slightly above
                velocityX = (random.nextFloat() - 0.5f) * 120f,
                velocityY = 600f + random.nextFloat() * 700f,
                size = 14f + random.nextFloat() * 18f,
                color = colors[it % colors.size],
                rotationSpeed = (random.nextFloat() - 0.5f) * 720f,
                isStar = it % 3 == 0
            )
        }
    }

    LaunchedEffect(isActive) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3200, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val p = progress.value
        val canvasWidth = size.width
        val canvasHeight = size.height

        particles.forEach { particle ->
            val x = (particle.initialX * canvasWidth) + (particle.velocityX * p) + (sin(p * 8f + particle.initialX * 10f) * 30f)
            val y = (particle.initialY * canvasHeight) + (particle.velocityY * p)
            val rotation = p * particle.rotationSpeed
            val alpha = (1f - (p * 0.85f)).coerceIn(0f, 1f)

            if (y in -50f..(canvasHeight + 50f)) {
                rotate(degrees = rotation, pivot = Offset(x, y)) {
                    if (particle.isStar) {
                        drawStar(
                            center = Offset(x, y),
                            radius = particle.size * 0.9f,
                            color = particle.color.copy(alpha = alpha)
                        )
                    } else {
                        drawRect(
                            color = particle.color.copy(alpha = alpha),
                            topLeft = Offset(x - particle.size / 2, y - particle.size / 3),
                            size = Size(particle.size, particle.size * 0.6f)
                        )
                    }
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStar(
    center: Offset,
    radius: Float,
    color: Color
) {
    val path = Path()
    val points = 5
    val innerRadius = radius * 0.45f
    val step = (2 * PI / points).toFloat()
    val halfStep = step / 2f

    for (i in 0 until points) {
        val outerAngle = i * step - (PI / 2).toFloat()
        val innerAngle = outerAngle + halfStep

        val ox = center.x + radius * cos(outerAngle)
        val oy = center.y + radius * sin(outerAngle)

        val ix = center.x + innerRadius * cos(innerAngle)
        val iy = center.y + innerRadius * sin(innerAngle)

        if (i == 0) {
            path.moveTo(ox, oy)
        } else {
            path.lineTo(ox, oy)
        }
        path.lineTo(ix, iy)
    }
    path.close()
    drawPath(path, color)
}
