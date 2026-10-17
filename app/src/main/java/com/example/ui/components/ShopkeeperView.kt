package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CheckResult

@Composable
fun ShopkeeperView(
    dialogueText: String,
    checkResult: CheckResult,
    onSpeakClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shopkeeper_anim")
    val gentleBounce by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    val bubbleBg = when (checkResult) {
        is CheckResult.Correct -> Color(0xFFE8F5E9)
        is CheckResult.NeedMore -> Color(0xFFFFF8E1)
        is CheckResult.NeedLess -> Color(0xFFFFEBEE)
        CheckResult.Idle -> Color.White
    }

    val bubbleBorder = when (checkResult) {
        is CheckResult.Correct -> Color(0xFF4CAF50)
        is CheckResult.NeedMore -> Color(0xFFFFA000)
        is CheckResult.NeedLess -> Color(0xFFE53935)
        CheckResult.Idle -> Color(0xFFFFB74D)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cute Shopkeeper Avatar
        Box(
            modifier = Modifier
                .size(76.dp)
                .scale(gentleBounce)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .border(3.dp, Color(0xFFFFB74D), CircleShape)
                .background(Color(0xFFFFF3E0))
                .clickable(onClick = onSpeakClick)
                .testTag("shopkeeper_avatar"),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_shopkeeper),
                contentDescription = "Cô bán hàng dễ thương",
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Speech Bubble with arrow pointing to shopkeeper
        Box(
            modifier = Modifier
                .weight(1f)
                .shadow(3.dp, RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp))
                .border(2.dp, bubbleBorder, RoundedCornerShape(18.dp))
                .background(bubbleBg)
                .clickable(onClick = onSpeakClick)
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("speech_bubble")
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "👩‍🌾 Cô Bán Hàng",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = Color(0xFFE65100),
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Nghe cô nói",
                        tint = Color(0xFFFB8C00),
                        modifier = Modifier
                            .size(18.dp)
                            .clickable(onClick = onSpeakClick)
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = dialogueText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    color = Color(0xFF263238)
                )
            }
        }
    }
}
