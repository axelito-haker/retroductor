package com.example.audio

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.sin

object WavAudioGenerator {

    /**
     * Generates a PCM 16-bit WAV file in cacheDir if not already generated.
     */
    fun getOrCreateSynthTrack(context: Context, trackIndex: Int): File {
        val dir = File(context.cacheDir, "cyber_tracks").apply { mkdirs() }
        val filename = when (trackIndex) {
            1 -> "01_Hyperbeam_Pulse.wav"
            2 -> "02_Midnight_Runner.wav"
            3 -> "03_Neon_Ghost_District.wav"
            else -> "04_Analog_Memory_Tape.wav"
        }
        val targetFile = File(dir, filename)
        if (targetFile.exists() && targetFile.length() > 1000) {
            return targetFile
        }

        val sampleRate = 22050
        val durationSeconds = 12
        val totalSamples = sampleRate * durationSeconds
        val numChannels = 2 // Stereo
        val bitsPerSample = 16
        val byteRate = sampleRate * numChannels * (bitsPerSample / 8)
        val blockAlign = numChannels * (bitsPerSample / 8)
        val dataSize = totalSamples * blockAlign
        val chunkSize = 36 + dataSize

        val pcmData = ByteArray(dataSize)
        val buffer = ByteBuffer.wrap(pcmData).order(ByteOrder.LITTLE_ENDIAN)

        // Musical frequencies (pentatonic / synthwave scales)
        val chordNotes = when (trackIndex) {
            1 -> doubleArrayOf(110.0, 130.81, 164.81, 196.0, 220.0, 261.63, 329.63) // A Minor Synthwave
            2 -> doubleArrayOf(146.83, 174.61, 220.0, 261.63, 349.23, 440.0) // D Minor Lofi
            3 -> doubleArrayOf(261.63, 293.66, 329.63, 392.0, 440.0, 523.25) // C Major 8-bit
            else -> doubleArrayOf(98.0, 123.47, 146.83, 196.0, 246.94) // G Warm Tape
        }

        val tempoBpm = when (trackIndex) {
            1 -> 120
            2 -> 85
            3 -> 140
            else -> 72
        }
        val samplesPerBeat = (sampleRate * 60) / tempoBpm

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val beatIndex = (i / samplesPerBeat) % chordNotes.size
            val baseFreq = chordNotes[beatIndex]

            // Arpeggio note frequency
            val arpIndex = (i / (samplesPerBeat / 4)) % chordNotes.size
            val arpFreq = chordNotes[arpIndex] * (if (trackIndex == 3) 2.0 else 1.0)

            // Synthesizer wave generation
            val bass = sin(2.0 * PI * (baseFreq * 0.5) * t) * 0.4
            val arp = sin(2.0 * PI * arpFreq * t) * 0.3
            // Analog harmonic shimmer / tape warmth
            val harmonic = sin(4.0 * PI * baseFreq * t) * 0.15

            // Stereo panning
            val leftSample = ((bass + arp * 0.8 + harmonic) * 16000).toInt().coerceIn(-32768, 32767).toShort()
            val rightSample = ((bass + arp * 1.2 + harmonic * 0.9) * 16000).toInt().coerceIn(-32768, 32767).toShort()

            buffer.putShort(leftSample)
            buffer.putShort(rightSample)
        }

        FileOutputStream(targetFile).use { fos ->
            // WAV Header
            fos.write("RIFF".toByteArray())
            fos.write(intToByteArray(chunkSize))
            fos.write("WAVE".toByteArray())
            fos.write("fmt ".toByteArray())
            fos.write(intToByteArray(16)) // Subchunk1Size (16 for PCM)
            fos.write(shortToByteArray(1.toShort())) // AudioFormat 1 = PCM
            fos.write(shortToByteArray(numChannels.toShort()))
            fos.write(intToByteArray(sampleRate))
            fos.write(intToByteArray(byteRate))
            fos.write(shortToByteArray(blockAlign.toShort()))
            fos.write(shortToByteArray(bitsPerSample.toShort()))
            fos.write("data".toByteArray())
            fos.write(intToByteArray(dataSize))
            fos.write(pcmData)
        }

        return targetFile
    }

    private fun intToByteArray(value: Int): ByteArray {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array()
    }

    private fun shortToByteArray(value: Short): ByteArray {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value).array()
    }
}
