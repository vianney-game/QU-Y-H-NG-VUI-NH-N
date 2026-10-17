package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin

class SoundManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    var isSoundEnabled: Boolean = true

    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error initializing TTS", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val vietnameseLocale = Locale("vi", "VN")
            val result = tts?.setLanguage(vietnameseLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Try generic Vietnamese
                val genericVi = Locale("vi")
                val res2 = tts?.setLanguage(genericVi)
                isTtsReady = res2 != TextToSpeech.LANG_MISSING_DATA && res2 != TextToSpeech.LANG_NOT_SUPPORTED
            } else {
                isTtsReady = true
            }
            tts?.setSpeechRate(0.85f) // Slower, clearer cadence for preschoolers
            tts?.setPitch(1.15f) // Friendly, warm slightly elevated pitch
        } else {
            isTtsReady = false
        }
    }

    fun speak(text: String) {
        if (!isSoundEnabled) return
        if (isTtsReady && tts != null) {
            try {
                tts?.stop()
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_ID_${System.currentTimeMillis()}")
            } catch (e: Exception) {
                Log.w("SoundManager", "TTS speak failed", e)
            }
        }
    }

    fun playItemAdd() {
        if (!isSoundEnabled) return
        vibrate(30)
        scope.launch {
            // Ascending playful pop (523 Hz to 659 Hz)
            playSweepTone(523.0, 784.0, 110)
        }
    }

    fun playItemRemove() {
        if (!isSoundEnabled) return
        vibrate(25)
        scope.launch {
            // Descending slide (659 Hz to 440 Hz)
            playSweepTone(659.0, 440.0, 110)
        }
    }

    fun playCelebration() {
        if (!isSoundEnabled) return
        vibrate(100)
        scope.launch {
            // Cheerful victory arpeggio C5 -> E5 -> G5 -> C6
            playTone(523.25, 120)
            kotlinx.coroutines.delay(100)
            playTone(659.25, 120)
            kotlinx.coroutines.delay(100)
            playTone(783.99, 140)
            kotlinx.coroutines.delay(120)
            playTone(1046.50, 280)
        }
    }

    fun playThinkTone() {
        if (!isSoundEnabled) return
        vibrate(40)
        scope.launch {
            // Gentle encouragement two-tone
            playTone(493.88, 140)
            kotlinx.coroutines.delay(130)
            playTone(440.00, 180)
        }
    }

    fun playButtonClick() {
        if (!isSoundEnabled) return
        vibrate(20)
        scope.launch {
            playTone(880.0, 40)
        }
    }

    private fun vibrate(millis: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(millis)
            }
        } catch (_: Exception) {}
    }

    private fun playTone(freqHz: Double, durationMs: Int) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val time = i.toDouble() / sampleRate
            // Apply attack and decay envelope to prevent audio popping
            val envelope = when {
                i < sampleRate * 0.01 -> i / (sampleRate * 0.01)
                i > numSamples - sampleRate * 0.02 -> (numSamples - i) / (sampleRate * 0.02)
                else -> 1.0
            }
            val sample = (sin(2.0 * PI * freqHz * time) * 32767.0 * 0.6 * envelope).toInt().toShort()
            buffer[i] = sample
        }

        playBuffer(buffer, sampleRate)
    }

    private fun playSweepTone(startFreq: Double, endFreq: Double, durationMs: Int) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)

        var phase = 0.0
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val currentFreq = startFreq + (endFreq - startFreq) * progress
            val phaseInc = 2.0 * PI * currentFreq / sampleRate
            phase += phaseInc

            val envelope = when {
                i < sampleRate * 0.01 -> i / (sampleRate * 0.01)
                i > numSamples - sampleRate * 0.02 -> (numSamples - i) / (sampleRate * 0.02)
                else -> 1.0
            }
            val sample = (sin(phase) * 32767.0 * 0.55 * envelope).toInt().toShort()
            buffer[i] = sample
        }

        playBuffer(buffer, sampleRate)
    }

    private fun playBuffer(buffer: ShortArray, sampleRate: Int) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            // Clean up after playing
            scope.launch {
                kotlinx.coroutines.delay((buffer.size * 1000L / sampleRate) + 80)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (e: Exception) {
            Log.w("SoundManager", "Error in AudioTrack synthesis", e)
        }
    }

    fun destroy() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
    }
}
