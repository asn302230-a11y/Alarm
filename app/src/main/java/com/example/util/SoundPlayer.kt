package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundPlayer {
    private const val TAG = "SoundPlayer"
    private var mediaPlayer: MediaPlayer? = null
    private var audioTrack: AudioTrack? = null
    private var synthesisJob: Job? = null
    private var vibrator: Vibrator? = null

    val AVAILABLE_TONES = listOf(
        "Mesopotamian Dawn",
        "Crescent Clarion",
        "Baghdad Pulse",
        "Classic Bell",
        "System Default"
    )

    fun playTone(context: Context, toneName: String, loop: Boolean = true) {
        stopTone()

        if (toneName == "System Default") {
            try {
                val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(context, alertUri)
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    isLooping = loop
                    prepare()
                    start()
                }
                return
            } catch (e: Exception) {
                Log.e(TAG, "Error playing system ringtone, falling back to synthesized tone", e)
            }
        }

        // Play synthesized melodic tone via AudioTrack
        playSynthesizedTone(toneName, loop)
    }

    private fun playSynthesizedTone(toneName: String, loop: Boolean) {
        val sampleRate = 44100
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()

        synthesisJob = CoroutineScope(Dispatchers.Default).launch {
            // Melodic sequences for each tone
            val notes = when (toneName) {
                "Mesopotamian Dawn" -> listOf(
                    293.66, 329.63, 349.23, 392.00, 440.00, 523.25, 440.00, 392.00 // D4 - E4 - F4 - G4 - A4 - C5 - A4 - G4
                )
                "Crescent Clarion" -> listOf(
                    523.25, 659.25, 783.99, 1046.50, 783.99, 659.25 // C5 - E5 - G5 - C6 - G5 - E5
                )
                "Baghdad Pulse" -> listOf(
                    440.00, 440.00, 0.0, 554.37, 554.37, 0.0, 659.25, 880.00
                )
                else -> listOf(
                    880.00, 987.77, 880.00, 987.77 // Classic alarm twin-bell
                )
            }

            val noteDurationMs = if (toneName == "Mesopotamian Dawn") 320 else 220

            while (isActive) {
                for (freq in notes) {
                    if (!isActive) break
                    val numSamples = (sampleRate * (noteDurationMs / 1000.0)).toInt()
                    val buffer = ShortArray(numSamples)

                    if (freq > 0.0) {
                        for (i in 0 until numSamples) {
                            val time = i.toDouble() / sampleRate
                            // Add slight harmonic richness (1st + 2nd overtone) + envelope decay
                            val envelope = (1.0 - (i.toDouble() / numSamples) * 0.4)
                            val wave = sin(2.0 * Math.PI * freq * time) * 0.7 +
                                    sin(2.0 * Math.PI * (freq * 2) * time) * 0.2 +
                                    sin(2.0 * Math.PI * (freq * 3) * time) * 0.1
                            buffer[i] = (wave * Short.MAX_VALUE * 0.75 * envelope).toInt().toShort()
                        }
                    }
                    audioTrack?.write(buffer, 0, buffer.size)
                }

                // Small pause between pattern repeats
                val pauseSamples = (sampleRate * 0.25).toInt()
                val silence = ShortArray(pauseSamples)
                audioTrack?.write(silence, 0, silence.size)

                if (!loop) break
            }
        }
    }

    fun startVibration(context: Context) {
        stopVibration()
        val v = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
        vibrator = v

        val pattern = longArrayOf(0, 400, 250, 400, 600)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            v?.vibrate(pattern, 0)
        }
    }

    fun stopVibration() {
        vibrator?.cancel()
        vibrator = null
    }

    fun stopTone() {
        synthesisJob?.cancel()
        synthesisJob = null

        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null

        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null

        stopVibration()
    }
}
