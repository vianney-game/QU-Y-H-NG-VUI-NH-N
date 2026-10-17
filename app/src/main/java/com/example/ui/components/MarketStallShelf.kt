package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CheckResult
import com.example.data.GroceryItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MarketStallShelf(
    stallItems: List<GroceryItem>,
    checkResult: CheckResult,
    onRemoveItem: (GroceryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val count = stallItems.size
    val isExactSix = count == 6

    val countBadgeBg by animateColorAsState(
        targetValue = when {
            isExactSix -> Color(0xFF2E7D32)
            count < 6 -> Color(0xFFEF6C00)
            else -> Color(0xFFC62828)
        },
        label = "countBadgeColor"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "shelf_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .shadow(6.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFFFFF9E6))
            .border(3.dp, Color(0xFF8D6E63), RoundedCornerShape(22.dp))
            .testTag("market_stall_shelf")
    ) {
        // Market Awning (Canopy roof)
        MarketAwningHeader()

        // Shelf Counter Header with Big Count Badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF6D4C41))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🏬 Quầy Hàng",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mục tiêu: ĐỦ 6 MÓN",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFFE082)
                )
            }

            // Big Count Badge: "Quầy đang có: X món"
            Box(
                modifier = Modifier
                    .scale(if (isExactSix) pulseScale else 1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(countBadgeBg)
                    .border(1.5.dp, Color.White, RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .testTag("stall_count_badge")
            ) {
                Text(
                    text = "Quầy đang có: $count món ${if (isExactSix) "⭐" else ""}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }

        // Shelf Counter Surface (Wooden texture style)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFD7CCC8),
                            Color(0xFFBCAAA4),
                            Color(0xFFA1887F)
                        )
                    )
                )
                .padding(10.dp)
        ) {
            // First 6 Target Slots (Slots 1 to 6)
            Text(
                text = "Kệ hàng chính (Đủ 6 chỗ):",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E2723),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 6
            ) {
                for (slotIndex in 0 until 6) {
                    val item = stallItems.getOrNull(slotIndex)
                    if (item != null) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            DraggableGroceryItem(
                                item = item,
                                isInStall = true,
                                onMoveRequested = { onRemoveItem(item) }
                            )
                            // Slot index tag
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2E7D32)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${slotIndex + 1}",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        // Empty Slot Placeholder with slot number
                        EmptySlotPlaceholder(slotNumber = slotIndex + 1)
                    }
                }
            }

            // Overflow items row if count > 6
            if (count > 6) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFEBEE))
                        .border(1.5.dp, Color(0xFFEF5350), RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "⚠️ Đang dư ${count - 6} món (Kéo hoặc chạm để bớt ra):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFC62828)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (overflowIndex in 6 until count) {
                                val item = stallItems[overflowIndex]
                                Box(contentAlignment = Alignment.TopEnd) {
                                    DraggableGroceryItem(
                                        item = item,
                                        isInStall = true,
                                        onMoveRequested = { onRemoveItem(item) }
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFC62828)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "+",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Wooden shelf bottom ledge
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF5D4037))
            )
        }
    }
}

@Composable
private fun EmptySlotPlaceholder(slotNumber: Int) {
    Box(
        modifier = Modifier
            .width(66.dp)
            .height(76.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.5f))
            .border(
                width = 1.5.dp,
                color = Color(0xFF8D6E63).copy(alpha = 0.6f),
                shape = RoundedCornerShape(14.dp)
            )
            .testTag("empty_slot_$slotNumber"),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$slotNumber",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF757575)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ô trống",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF8D6E63)
            )
        }
    }
}

@Composable
private fun MarketAwningHeader() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
    ) {
        val stripeCount = 12
        val stripeWidth = size.width / stripeCount
        val redColor = Color(0xFFE53935)
        val yellowColor = Color(0xFFFFD54F)

        for (i in 0 until stripeCount) {
            val color = if (i % 2 == 0) redColor else yellowColor
            val left = i * stripeWidth

            val path = Path().apply {
                moveTo(left, 0f)
                lineTo(left + stripeWidth, 0f)
                lineTo(left + stripeWidth, size.height - 8f)
                // Scalloped wavy bottom
                quadraticBezierTo(
                    left + stripeWidth / 2f,
                    size.height,
                    left,
                    size.height - 8f
                )
                close()
            }
            drawPath(path, color)
        }
    }
}
