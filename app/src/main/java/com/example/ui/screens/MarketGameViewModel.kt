package com.example.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.audio.SoundManager
import com.example.data.CheckResult
import com.example.data.GameLevel
import com.example.data.GroceryItem
import com.example.data.LevelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class GameUiState(
    val levelNumber: Int = 1,
    val currentLevel: GameLevel = LevelRepository.getLevel(1),
    val stallItems: List<GroceryItem> = emptyList(),
    val reserveItems: List<GroceryItem> = emptyList(),
    val checkResult: CheckResult = CheckResult.Idle,
    val dialogueText: String = "Chào bé! Hôm nay chúng mình cùng bán hàng nhé! Bé hãy làm cho quầy có đúng 6 món nào!",
    val isCelebrating: Boolean = false,
    val isSoundEnabled: Boolean = true,
    val completedLevels: Set<Int> = emptySet(),
    val showLevelPicker: Boolean = false,
    val bannerAnnouncement: String? = null
)

class MarketGameViewModel(application: Application) : AndroidViewModel(application) {

    val soundManager = SoundManager(application)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        loadLevel(1)
        soundManager.speak("Chào bé! Hôm nay chúng mình cùng bán hàng nhé! Bé hãy làm cho quầy có đúng 6 món nào!")
    }

    fun loadLevel(levelNum: Int) {
        val level = LevelRepository.getLevel(levelNum)
        val (stall, reserve) = LevelRepository.generateItemsForLevel(level)

        val welcomeMsg = when {
            stall.size == 6 -> "Bé đếm xem quầy đang có bao nhiêu món nhé!"
            stall.size < 6 -> "Quầy mới có ${stall.size} món thôi. Bé hãy giúp cô cho đủ 6 món nhé!"
            else -> "Quầy đang có ${stall.size} món, hơi nhiều rồi! Bé bớt ra cho đúng 6 món nhé!"
        }

        _uiState.update {
            it.copy(
                levelNumber = levelNum,
                currentLevel = level,
                stallItems = stall,
                reserveItems = reserve,
                checkResult = CheckResult.Idle,
                dialogueText = welcomeMsg,
                isCelebrating = false,
                bannerAnnouncement = null
            )
        }
        soundManager.speak(welcomeMsg)
    }

    fun addItemToStall(item: GroceryItem) {
        val currentReserve = _uiState.value.reserveItems
        val currentStall = _uiState.value.stallItems

        if (!currentReserve.contains(item)) return

        val newReserve = currentReserve.filter { it.id != item.id }
        val newStall = currentStall + item

        soundManager.playItemAdd()

        val prompt = "Bé vừa thêm ${item.label}. Quầy đang có: ${newStall.size} món!"

        _uiState.update {
            it.copy(
                reserveItems = newReserve,
                stallItems = newStall,
                checkResult = CheckResult.Idle,
                dialogueText = prompt,
                bannerAnnouncement = null
            )
        }
    }

    fun removeItemFromStall(item: GroceryItem) {
        val currentStall = _uiState.value.stallItems
        val currentReserve = _uiState.value.reserveItems

        if (!currentStall.contains(item)) return

        val newStall = currentStall.filter { it.id != item.id }
        val newReserve = currentReserve + item

        soundManager.playItemRemove()

        val prompt = "Bé đã bớt ${item.label}. Quầy đang có: ${newStall.size} món!"

        _uiState.update {
            it.copy(
                stallItems = newStall,
                reserveItems = newReserve,
                checkResult = CheckResult.Idle,
                dialogueText = prompt,
                bannerAnnouncement = null
            )
        }
    }

    fun checkStall() {
        val count = _uiState.value.stallItems.size
        soundManager.playButtonClick()

        when {
            count == 6 -> {
                // Exact 6 items!
                val speech = "Giỏi quá! Bé đã giúp cô có đúng 6 món hàng rồi!"
                soundManager.playCelebration()
                soundManager.speak(speech)

                val completed = _uiState.value.completedLevels + _uiState.value.levelNumber

                _uiState.update {
                    it.copy(
                        checkResult = CheckResult.Correct(total = 6),
                        dialogueText = speech,
                        isCelebrating = true,
                        bannerAnnouncement = "CHÍNH XÁC! 6 MÓN HÀNG 🎉",
                        completedLevels = completed
                    )
                }
            }
            count < 6 -> {
                // Missing items
                val needed = 6 - count
                val speech = "Quầy còn thiếu hàng rồi! Bé hãy thêm $needed món nữa nhé!"
                soundManager.playThinkTone()
                soundManager.speak(speech)

                _uiState.update {
                    it.copy(
                        checkResult = CheckResult.NeedMore(current = count, needed = needed),
                        dialogueText = speech,
                        isCelebrating = false,
                        bannerAnnouncement = "Quầy còn thiếu $needed món nữa!"
                    )
                }
            }
            else -> {
                // Excess items
                val excess = count - 6
                val speech = "Ôi, quầy hơi nhiều hàng rồi! Bé hãy bớt $excess món nhé!"
                soundManager.playThinkTone()
                soundManager.speak(speech)

                _uiState.update {
                    it.copy(
                        checkResult = CheckResult.NeedLess(current = count, excess = excess),
                        dialogueText = speech,
                        isCelebrating = false,
                        bannerAnnouncement = "Quầy đang thừa $excess món, bớt ra nhé!"
                    )
                }
            }
        }
    }

    fun replayLevel() {
        soundManager.playButtonClick()
        loadLevel(_uiState.value.levelNumber)
    }

    fun nextLevel() {
        soundManager.playButtonClick()
        val next = _uiState.value.levelNumber + 1
        loadLevel(next)
    }

    fun toggleSound() {
        val newState = !_uiState.value.isSoundEnabled
        soundManager.isSoundEnabled = newState
        _uiState.update { it.copy(isSoundEnabled = newState) }
    }

    fun speakCurrentDialogue() {
        soundManager.speak(_uiState.value.dialogueText)
    }

    fun openLevelPicker() {
        soundManager.playButtonClick()
        _uiState.update { it.copy(showLevelPicker = true) }
    }

    fun closeLevelPicker() {
        _uiState.update { it.copy(showLevelPicker = false) }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.destroy()
    }
}
