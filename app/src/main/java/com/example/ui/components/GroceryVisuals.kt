package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GroceryItemType

@Composable
fun GroceryItemGraphic(
    type: GroceryItemType,
    modifier: Modifier = Modifier,
    size: Dp = 54.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        type.lightColor,
                        type.primaryColor.copy(alpha = 0.25f)
                    ),
                    center = Offset(20f, 20f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.toPx()
            val canvasHeight = size.toPx()
            // Top-left shine highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.4f),
                radius = canvasWidth * 0.28f,
                center = Offset(canvasWidth * 0.32f, canvasHeight * 0.32f)
            )
        }

        // Distinct large emoji icon that kids instantly recognize
        Text(
            text = type.emoji,
            fontSize = (size.value * 0.58f).sp
        )
    }
}
