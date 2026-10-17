package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CheckResult
import com.example.ui.components.ConfettiCelebration
import com.example.ui.components.LevelPickerModal
import com.example.ui.components.MarketStallShelf
import com.example.ui.components.ReserveInventoryView
import com.example.ui.components.ShopkeeperView

@Composable
fun MarketGameScreen(
    viewModel: MarketGameViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "btn_anim")
    val nextBtnScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nextScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF9C4), // Soft sunny yellow top
                        Color(0xFFFFFDE7),
                        Color(0xFFE0F7FA)  // Soft sky/mint bottom
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP BAR: Title, Level Selector, Stars, Sound Toggle
            TopMarketNavBar(
                levelNumber = uiState.levelNumber,
                starsCount = uiState.completedLevels.size,
                isSoundEnabled = uiState.isSoundEnabled,
                onLevelClick = { viewModel.openLevelPicker() },
                onSoundToggle = { viewModel.toggleSound() }
            )

            // Announcement Banner
            AnnouncementBanner(
                bannerText = uiState.bannerAnnouncement,
                checkResult = uiState.checkResult
            )

            // Shopkeeper Character & Speech Bubble
            ShopkeeperView(
                dialogueText = uiState.dialogueText,
                checkResult = uiState.checkResult,
                onSpeakClick = { viewModel.speakCurrentDialogue() }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Main Market Stall Shelf
            MarketStallShelf(
                stallItems = uiState.stallItems,
                checkResult = uiState.checkResult,
                onRemoveItem = { item -> viewModel.removeItemFromStall(item) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Reserve Goods Area (Inventory)
            ReserveInventoryView(
                reserveItems = uiState.reserveItems,
                onAddItem = { item -> viewModel.addItemToStall(item) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: "Kiểm tra", "Chơi lại", "Màn tiếp theo"
            MarketActionButtons(
                isCelebrating = uiState.isCelebrating,
                nextBtnScale = nextBtnScale,
                onCheckClick = { viewModel.checkStall() },
                onReplayClick = { viewModel.replayLevel() },
                onNextClick = { viewModel.nextLevel() }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Confetti Fireworks layer when exactly 6 items achieved
        ConfettiCelebration(
            isActive = uiState.isCelebrating,
            modifier = Modifier.fillMaxSize()
        )

        // Level Picker Dialog
        if (uiState.showLevelPicker) {
            LevelPickerModal(
                currentLevelNumber = uiState.levelNumber,
                completedLevels = uiState.completedLevels,
                onSelectLevel = { levelNum -> viewModel.loadLevel(levelNum) },
                onDismiss = { viewModel.closeLevelPicker() }
            )
        }
    }
}

@Composable
private fun TopMarketNavBar(
    levelNumber: Int,
    starsCount: Int,
    isSoundEnabled: Boolean,
    onLevelClick: () -> Unit,
    onSoundToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // App title & Level selector pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .shadow(2.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(1.5.dp, Color(0xFFFFB74D), RoundedCornerShape(20.dp))
                .clickable(onClick = onLevelClick)
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("level_selector_button")
        ) {
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = "Chọn màn",
                tint = Color(0xFFE65100),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Màn $levelNumber / 10 ▾",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = Color(0xFFE65100)
            )
        }

        // Stars earned badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .shadow(2.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFFFF8E1))
                .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("stars_badge")
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Số sao đạt được",
                tint = Color(0xFFFFB300),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$starsCount ⭐",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                color = Color(0xFFE65100)
            )
        }

        // Sound Mute/Unmute button
        IconButton(
            onClick = onSoundToggle,
            modifier = Modifier
                .shadow(2.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.5.dp, Color(0xFFFFB74D), CircleShape)
                .size(40.dp)
                .testTag("sound_toggle_button")
        ) {
            Icon(
                imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                contentDescription = if (isSoundEnabled) "Tắt âm thanh" else "Bật âm thanh",
                tint = if (isSoundEnabled) Color(0xFF2E7D32) else Color(0xFF757575)
            )
        }
    }
}

@Composable
private fun AnnouncementBanner(
    bannerText: String?,
    checkResult: CheckResult
) {
    val defaultText = "Bé hãy giúp cô bán hàng để quầy có đúng 6 món nhé!"
    val displayText = bannerText ?: defaultText

    val bgColor = when (checkResult) {
        is CheckResult.Correct -> Color(0xFF2E7D32)
        is CheckResult.NeedMore -> Color(0xFFEF6C00)
        is CheckResult.NeedLess -> Color(0xFFC62828)
        CheckResult.Idle -> Color(0xFFE65100)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("announcement_banner"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayText,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = if (checkResult is CheckResult.Correct) 16.sp else 13.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MarketActionButtons(
    isCelebrating: Boolean,
    nextBtnScale: Float,
    onCheckClick: () -> Unit,
    onReplayClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "Chơi lại" (Replay) Button
        OutlinedButton(
            onClick = onReplayClick,
            modifier = Modifier
                .weight(0.9f)
                .height(52.dp)
                .testTag("replay_button"),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF5D4037)
            ),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFB74D))
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Chơi lại",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Chơi lại",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        // "Kiểm tra" (Check) Button
        Button(
            onClick = onCheckClick,
            modifier = Modifier
                .weight(1.3f)
                .height(52.dp)
                .shadow(4.dp, RoundedCornerShape(18.dp))
                .testTag("check_button"),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E7D32),
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Kiểm tra",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Kiểm tra",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp
            )
        }

        // "Màn tiếp theo" (Next Level) Button
        Button(
            onClick = onNextClick,
            modifier = Modifier
                .weight(1.2f)
                .height(52.dp)
                .scale(if (isCelebrating) nextBtnScale else 1f)
                .shadow(if (isCelebrating) 6.dp else 2.dp, RoundedCornerShape(18.dp))
                .testTag("next_level_button"),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCelebrating) Color(0xFFFF9800) else Color(0xFFFFB74D),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Màn sau",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Màn tiếp theo",
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
