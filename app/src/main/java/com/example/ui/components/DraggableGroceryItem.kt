package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.data.GroceryItem
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun DraggableGroceryItem(
    item: GroceryItem,
    isInStall: Boolean,
    onMoveRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    var isDragging by remember { mutableStateOf(false) }

    // Threshold in pixels to commit drag to stall or reserve
    val dragCommitThreshold = 100f

    val cardScale = if (isDragging) 1.18f else 1.0f
    val cardZIndex = if (isDragging) 10f else 1f
    val shadowElevation = if (isDragging) 12.dp else 3.dp

    Box(
        modifier = modifier
            .zIndex(cardZIndex)
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .scale(cardScale)
            .shadow(shadowElevation, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(
                width = if (isDragging) 2.5.dp else 1.5.dp,
                color = if (isDragging) item.type.primaryColor else Color(0xFFFFCC80),
                shape = RoundedCornerShape(16.dp)
            )
            .pointerInput(item.id, isInStall) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                    },
                    onDragEnd = {
                        isDragging = false
                        val currentY = offsetY.value
                        // If item in reserve was dragged UP by threshold -> move to stall!
                        // If item on stall was dragged DOWN by threshold -> move to reserve!
                        val shouldMove = if (isInStall) {
                            currentY > dragCommitThreshold
                        } else {
                            currentY < -dragCommitThreshold
                        }

                        if (shouldMove) {
                            onMoveRequested()
                            coroutineScope.launch {
                                offsetX.snapTo(0f)
                                offsetY.snapTo(0f)
                            }
                        } else {
                            // Snap smoothly back with spring physics
                            coroutineScope.launch {
                                launch { offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
                                launch { offsetY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
                            }
                        }
                    },
                    onDragCancel = {
                        isDragging = false
                        coroutineScope.launch {
                            launch { offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
                            launch { offsetY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                            offsetY.snapTo(offsetY.value + dragAmount.y)
                        }
                    }
                )
            }
            .clickable {
                // Tapping also moves item immediately for preschooler convenience!
                onMoveRequested()
            }
            .padding(horizontal = 6.dp, vertical = 6.dp)
            .testTag("item_${item.id}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(66.dp)
        ) {
            GroceryItemGraphic(type = item.type, size = 46.dp)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF37474F),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (isInStall) "▼ Bớt" else "▲ Thêm",
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isInStall) Color(0xFFD32F2F) else Color(0xFF388E3C),
                textAlign = TextAlign.Center
            )
        }
    }
}
