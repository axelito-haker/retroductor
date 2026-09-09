package com.example.audio

import android.content.ContentUris
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.model.AudioTrack
import com.example.model.DspPreset
import com.example.model.Mixtape
import com.example.model.TapeTrackItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

class CyberAudioEngine(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var mediaPlayer: MediaPlayer? = null
    private var telemetryJob: Job? = null

    // Image URLs provided by the user in HTML
    companion object {
        const val LOGO_URL = "https://lh3.googleusercontent.com/aida-public/AB6AXuBTZy-tY60Qu33pgg81tbnoYfIGbhEuoOkhWQUzDd7xX6QDkjz3qTjfGhsaFIljHcViNCDT5yrCQnMqlGNZWV0MiWxU81wXXi3T_2mKOWiI1HNVDqWUt2nIy8yykuaJXuRCLBrgH5zYN7O3k9bGCDEBdjr2NXpYqH7wZ_aivAmq6Iwql0Pb9R22yumSlnYKxqni-AGzvBXi1F1DJhXAvDGcHaTIZF0km7ssrw8mVnW5etOVK6euvvpl"
        const val TRACK1_ART = "https://lh3.googleusercontent.com/aida-public/AB6AXuC1qxONALBZ1i3IfFkl0ty0EyIzufazuPuXV9DTZQ_OR9CM09lTqLYD27QGjOeIFRhr-uXvXR2javTt_KqVb5Zag2a4xbY2NOQ9HkD25RkrLsklWvERDI3XhJby5-uFHaV3XFlNDa84JxrleOs-e1HwqageGlkxNw_A3V-FWzlUIxD3y8pEgi0ymAbvb4jcq3ZaMqRr1V_PtR6foODl9YdqKqklGmihJ3iyZ9_XGJ1MBUiBxruCwSp_"
        const val TRACK2_ART = "https://lh3.googleusercontent.com/aida-public/AB6AXuCcULnbY61exJm2Z3d0s4bxt4vhLoNVmvONWEM1wNqTxRj5VAxzm6xBBmKbdzSiiyaBjowGJJ6-dr-wVUpKfpWS1Szw6O9UWgj8kyJxmvXE7IKsYN1e-M4d24BE3hAKOGYPEwWr1igeRF-b0eZXDcaFnAt6oOdYjCs2mjDWXlcyul-3Ipxdk_zjXJ-uObQEM5h6YswOL6V3LRkvsUU_U7Y8IOaDEyQMVTPyQGB8ukopMxhsmlTGwGqq"
        const val TRACK3_ART = "https://lh3.googleusercontent.com/aida-public/AB6AXuBCVdBCSqzsLYPCHpXf9dET95HmZDAp_UO0DWFrzTzqoNTZgzei5aD8JcFXcFOXGNUMKlIqGL474STB7b6wBPEatpOT88k5X-gj4cY1NTLCGnQQefcNQDnQmbuYWmHKdZHK69ePT-uRyS34yDhk6jrSFXi3jgzUB_0_N_YHMDO4rv1E4WPgbTphoXl-fXRToFisQa7zg4YnYCkFrYiDLCgt69kY_CAx546N4DQqaXstoZsGpVhs5chV"
        const val TRACK4_ART = "https://lh3.googleusercontent.com/aida-public/AB6AXuCLAsE4P-Cg9NrjbqPIApK-82wmFfDHhX6kZLAlT7h6L6A-7fmqD_4UTb-d8hK9a6JoCcl-QLYfNhyLGKezpJj1dS3In84_kktQ34ESXFK-04RqlIrQYccNIXPUQEu4Bg7gpy6kIlbKXA-2bJpkjVr69nZqpbcsB7ImZY7kDKgzddmoEcU90K6JOf9_BvKIX3ZIIop6J1MCbGmmOph7xkXWbozMAm3Em6oix6aj9vYo1TtMVCyGN3ia"
        const val MIXTAPE1_ART = "https://lh3.googleusercontent.com/aida-public/AB6AXuAaFxepqwPJ4SH16_c3_sNE4D1hvsZbM3B_P3LVGRyfd6zOIuyzOrOYRR33XqcrE-temouk1AJOjEJNHfy-vJYk9Z9W9NB2iHeoe_ZS2vLOskey8nImTu1JgXIKrBbQ0ou35OeUZ5NDdCOE5S2UBZoKz7pvmBBj_mx9nIaTzq2UhYCymrmqhxkouuz_eH7IyrYeGEx-pLX-jaMc95vTrJe2JatVotPymMxaTwQOi78yYxSLBGt07PMz"
        const val MIXTAPE2_ART = "https://lh3.googleusercontent.com/aida-public/AB6AXuCaJBOD2a3Bs0kpf3aRLbNaYLk2oVjcZzS2Kio7fsQWT60i0qtFC5nD6HmD1w9a__OFzcLhM7sOpK0LL-pGRDzs1DgeQoK_y6iNEcn84juSsOUSPhxykycv-ixQhVcsOa9tjJNeFBe9A21AbGx27MSg9GN1fW2gxtHwONj-UA1lqjZdLvrLtHIx1ezlyipJzVHHRPBq3516FQ3ey91nfJp1Ff212uFLIEC5M69sV1dmiKtfANvrZ3EN"
        const val MIXTAPE3_ART = "https://lh3.googleusercontent.com/aida-public/AB6AXuBPh09qgSEZ3TrAAOH79CFwMEiDysRthMtzAM-R9Ppa_fGaDpLxMUbODWl-1wQszSxuF0re39dk3yXu_TzEckn98iqLyofZnlA69DA8Kk6zzwZpP8Who5G3P7XOFVjffiWgLEYdpkyP7DPmf23kdxwxy6HgEOe7y20coYq_qI6dioa80EBFG4r27HymR8cwcGNvKGNYxKG7naEIatZzf0PRlzoDkRmZzyXelx0CVL4bQ1GwZS-r51Zm"
    }

    // Default Initial Catalog
    private val defaultTracks = listOf(
        AudioTrack(
            id = "t1",
            title = "01_Hyperbeam_Pulse.flac",
            artist = "CYBER-CORP // VECTOR 84",
            album = "NEON PROTOCOL [2088]",
            durationMs = 252000L,
            codec = "FLAC",
            spec = "96kHz/24bit",
            bitrate = "1411 kbps LOSSLESS",
            fileSizeStr = "54 MB",
            crc32 = "0x9FA8",
            coverArtUrl = TRACK1_ART,
            isFavorite = true
        ),
        AudioTrack(
            id = "t2",
            title = "02_Midnight_Runner.wav",
            artist = "TOKYO_RAIN_DRIFT",
            album = "SECTOR 09 MIDNIGHT",
            durationMs = 235000L,
            codec = "WAV",
            spec = "44.1kHz/16bit",
            bitrate = "1411 kbps PCM",
            fileSizeStr = "42 MB",
            crc32 = "0x4C21",
            coverArtUrl = TRACK2_ART
        ),
        AudioTrack(
            id = "t3",
            title = "03_Neon_Ghost_District.mp3",
            artist = "GHOST_CHIP // 8-BIT",
            album = "SHIBUYA RECURSION",
            durationMs = 320000L,
            codec = "MP3",
            spec = "320kbps CBR",
            bitrate = "320 kbps MP3",
            fileSizeStr = "11 MB",
            crc32 = "0xA81F",
            coverArtUrl = TRACK3_ART
        ),
        AudioTrack(
            id = "t4",
            title = "04_Analog_Memory_Tape.flac",
            artist = "BURR-BROWN AMORPHOUS",
            album = "CASSETTE VAULT ARCHIVE",
            durationMs = 361000L,
            codec = "FLAC",
            spec = "88.2kHz/24bit",
            bitrate = "1411 kbps LOSSLESS",
            fileSizeStr = "61 MB",
            crc32 = "0x2D7E",
            coverArtUrl = TRACK4_ART
        )
    )

    private val defaultMixtapes = listOf(
        Mixtape(
            id = "m1",
            title = "SIDE A: RETRO GAMING CHIPTUNE & SYNTH",
            subtitle = "VOL.01 // RETRO GAMING CHIPTUNE",
            tapeType = "TYPE II CrO2",
            biasBadge = "C-60 HIGH BIAS",
            durationLabel = "60:00 [TYPE-II]",
            curator = "Curaduría: DJ_NEO_TOKYO // MASTER_LOSSLESS_96K",
            coverArtUrl = MIXTAPE1_ART,
            accentColorHex = 0xFFFF4A8E,
            tracks = listOf(
                TapeTrackItem("01", "00:00", "Neo-Kyoto Highway Overdrive", "MegaDrive FM Soundchip", "04:12"),
                TapeTrackItem("02", "04:12", "Silicon Heartbeat (Boss Fight)", "8-Bit Pulse Wave Synth", "05:34"),
                TapeTrackItem("03", "09:46", "Cyber Alley Glitch Hop", "GameBoy DMG-01 Arp", "04:36"),
                TapeTrackItem("04", "14:22", "Memory Leak in Sector 7", "SID 6581 Lead Vocals", "06:48"),
                TapeTrackItem("05", "32:10", "Reel Rewind Reflection", "Analog Tape Saturation Drift", "05:22")
            )
        ),
        Mixtape(
            id = "m2",
            title = "LO-FI MIDNIGHT CYBERPUNK BEATS",
            subtitle = "SIDE A // DRIFT",
            tapeType = "NORMAL BIAS",
            biasBadge = "C-45 FERRO-EXTRA",
            durationLabel = "45:00 [TYPE-I]",
            curator = "Curaduría: CYBER_RAIN_LOUNGE // 24-bit/48kHz",
            coverArtUrl = MIXTAPE2_ART,
            accentColorHex = 0xFFF4AEFF,
            tracks = listOf(
                TapeTrackItem("01", "00:00", "Late Night Shinjuku Subway", "Rain Foley + Rhodes Piano", "03:45"),
                TapeTrackItem("02", "03:45", "Neon Reflections in Puddles", "Sub Bass + Vinyl Crackle", "04:20"),
                TapeTrackItem("03", "08:05", "Midnight Coffee in Akihabara", "Cassette Wow & Flutter", "04:55"),
                TapeTrackItem("04", "13:00", "Cyberpunk Balcony Breeze", "Analog Pad Drift", "05:10")
            )
        ),
        Mixtape(
            id = "m3",
            title = "HEAVY INDUSTRIAL MECHA BASS",
            subtitle = "SIDE A // HEAVY OVERDRIVE",
            tapeType = "TYPE IV METAL",
            biasBadge = "C-90 METAL POS",
            durationLabel = "90:00 [TYPE-IV METAL]",
            curator = "Curaduría: SUB_LEVEL_REACTOR // 24-bit/96kHz PURE DSD",
            coverArtUrl = MIXTAPE3_ART,
            accentColorHex = 0xFFFFB868,
            tracks = listOf(
                TapeTrackItem("01", "00:00", "Sub-Level Reactor Meltdown", "Industrial FM Clang", "05:15"),
                TapeTrackItem("02", "05:15", "Hydraulic Pistons March", "Distorted 808 Sub", "06:02"),
                TapeTrackItem("03", "11:17", "Titan Armored Walker Patrol", "Modular Patch Overdrive", "04:40"),
                TapeTrackItem("04", "15:57", "Emergency Core Purge", "High Resonance Filter Sweep", "07:12")
            )
        )
    )

    val dspPresets = listOf(
        DspPreset("chiptune", "CHIPTUNE BOOST", listOf(6.0f, 4.5f, 0.0f, -2.5f, -4.0f, 1.0f, 3.5f, 7.0f, 9.5f, 8.0f)),
        DspPreset("synthwave", "SYNTHWAVE CRISP", listOf(7.5f, 8.0f, 3.5f, -1.0f, -2.0f, 0.5f, 2.5f, 6.0f, 8.5f, 6.5f)),
        DspPreset("mecha", "HEAVY MECHA SUB-BASS", listOf(11.0f, 10.0f, 6.5f, 1.5f, -3.0f, -2.0f, 0.5f, 1.5f, 4.0f, 3.0f)),
        DspPreset("tape", "VINTAGE TAPE CASSETTE", listOf(4.0f, 3.0f, 2.0f, 1.0f, 0.0f, -1.0f, -2.0f, -3.5f, -6.0f, -9.0f)),
        DspPreset("direct", "DIRECT MONITOR", listOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f))
    )

    // State Flows
    private val _tracks = MutableStateFlow<List<AudioTrack>>(defaultTracks)
    val tracks: StateFlow<List<AudioTrack>> = _tracks.asStateFlow()

    private val _currentTrack = MutableStateFlow<AudioTrack>(defaultTracks[0])
    val currentTrack: StateFlow<AudioTrack> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(108000L) // 01:48 default
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _totalDurationMs = MutableStateFlow(252000L) // 04:12 default
    val totalDurationMs: StateFlow<Long> = _totalDurationMs.asStateFlow()

    private val _progress = MutableStateFlow(0.43f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    // VU Meters
    private val _vuLeftDb = MutableStateFlow(-2.4f)
    val vuLeftDb: StateFlow<Float> = _vuLeftDb.asStateFlow()

    private val _vuRightDb = MutableStateFlow(-1.8f)
    val vuRightDb: StateFlow<Float> = _vuRightDb.asStateFlow()

    private val _isPeakClip = MutableStateFlow(false)
    val isPeakClip: StateFlow<Boolean> = _isPeakClip.asStateFlow()

    // Real-time FFT bars (26 bars)
    private val _fftBars = MutableStateFlow<List<Float>>(
        listOf(0.45f, 0.58f, 0.68f, 0.62f, 0.52f, 0.46f, 0.60f, 0.64f, 0.55f, 0.48f, 0.40f, 0.52f, 0.66f, 0.74f, 0.62f, 0.50f, 0.56f, 0.68f, 0.72f, 0.60f, 0.48f, 0.38f, 0.44f, 0.32f, 0.25f, 0.18f)
    )
    val fftBars: StateFlow<List<Float>> = _fftBars.asStateFlow()

    // Hardware Controls
    private val _outputLevelDb = MutableStateFlow(8.4f)
    val outputLevelDb: StateFlow<Float> = _outputLevelDb.asStateFlow()

    private val _isBassBoost = MutableStateFlow(true)
    val isBassBoost: StateFlow<Boolean> = _isBassBoost.asStateFlow()

    private val _isDolbyB = MutableStateFlow(true)
    val isDolbyB: StateFlow<Boolean> = _isDolbyB.asStateFlow()

    private val _wowDriftHz = MutableStateFlow(0.4f)
    val wowDriftHz: StateFlow<Float> = _wowDriftHz.asStateFlow()

    private val _flutterHz = MutableStateFlow(12.0f)
    val flutterHz: StateFlow<Float> = _flutterHz.asStateFlow()

    // DSP Hardware Unit
    private val _isDspOnline = MutableStateFlow(true)
    val isDspOnline: StateFlow<Boolean> = _isDspOnline.asStateFlow()
    val isDspEnabled: StateFlow<Boolean> = _isDspOnline.asStateFlow()

    private val _isValveStageOn = MutableStateFlow(true)
    val isValveStageOn: StateFlow<Boolean> = _isValveStageOn.asStateFlow()

    private val _valveDrive = MutableStateFlow(68)
    val valveDrive: StateFlow<Int> = _valveDrive.asStateFlow()

    private val _tubeDrive = MutableStateFlow(0.68f)
    val tubeDrive: StateFlow<Float> = _tubeDrive.asStateFlow()

    private val _stereoWidth = MutableStateFlow(1.2f)
    val stereoWidth: StateFlow<Float> = _stereoWidth.asStateFlow()

    private val _isSubsonicFilter = MutableStateFlow(true)
    val isSubsonicFilter: StateFlow<Boolean> = _isSubsonicFilter.asStateFlow()

    private val _eqBands = MutableStateFlow(dspPresets[0].gains)
    val eqBands: StateFlow<List<Float>> = _eqBands.asStateFlow()
    val eqBandsDb: StateFlow<List<Float>> = _eqBands.asStateFlow()

    private val _selectedPreset = MutableStateFlow("chiptune")
    val selectedPreset: StateFlow<String> = _selectedPreset.asStateFlow()
    val selectedPresetName: StateFlow<String> = _selectedPreset.asStateFlow()

    // Mixtapes
    private val _mixtapes = MutableStateFlow<List<Mixtape>>(defaultMixtapes)
    val mixtapes: StateFlow<List<Mixtape>> = _mixtapes.asStateFlow()

    private val _selectedMixtape = MutableStateFlow<Mixtape>(defaultMixtapes[0])
    val selectedMixtape: StateFlow<Mixtape> = _selectedMixtape.asStateFlow()

    // File Browser States
    private val _driveBay = MutableStateFlow("INT_STORAGE")
    val driveBay: StateFlow<String> = _driveBay.asStateFlow()
    val selectedDriveBay: StateFlow<String> = _driveBay.asStateFlow()

    private val _codecFilter = MutableStateFlow("ALL")
    val codecFilter: StateFlow<String> = _codecFilter.asStateFlow()
    val selectedCodecFilter: StateFlow<String> = _codecFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scanStatusText = MutableStateFlow("READY: 3,420 TRKS [0.8s]")
    val scanStatusText: StateFlow<String> = _scanStatusText.asStateFlow()

    private val _scanProgress = MutableStateFlow(1.0f)
    val scanProgress: StateFlow<Float> = _scanProgress.asStateFlow()

    // Loop points
    private val _loopPointA = MutableStateFlow<Long?>(null)
    val loopPointA: StateFlow<Long?> = _loopPointA.asStateFlow()

    private val _loopPointB = MutableStateFlow<Long?>(null)
    val loopPointB: StateFlow<Long?> = _loopPointB.asStateFlow()

    private val _isLoopEnabled = MutableStateFlow(false)
    val isLoopEnabled: StateFlow<Boolean> = _isLoopEnabled.asStateFlow()

    init {
        // Prepare local synth tracks in background
        scope.launch(Dispatchers.IO) {
            try {
                WavAudioGenerator.getOrCreateSynthTrack(context, 1)
                WavAudioGenerator.getOrCreateSynthTrack(context, 2)
                WavAudioGenerator.getOrCreateSynthTrack(context, 3)
                WavAudioGenerator.getOrCreateSynthTrack(context, 4)
            } catch (e: Exception) {
                Log.e("CyberAudioEngine", "Error pre-generating WAVs", e)
            }
        }
        startTelemetryLoop()
    }

    private fun startTelemetryLoop() {
        telemetryJob?.cancel()
        telemetryJob = scope.launch {
            var phase = 0.0
            while (isActive) {
                delay(100)
                phase += 0.2
                if (_isPlaying.value) {
                    // Advance time
                    val player = mediaPlayer
                    if (player != null && player.isPlaying) {
                        val currentMs = player.currentPosition.toLong()
                        val durMs = max(1000L, player.duration.toLong())
                        _currentPositionMs.value = currentMs
                        _totalDurationMs.value = durMs
                        _progress.value = (currentMs.toFloat() / durMs.toFloat()).coerceIn(0f, 1f)

                        // Check Loop A-B
                        val loopA = _loopPointA.value
                        val loopB = _loopPointB.value
                        if (_isLoopEnabled.value && loopA != null && loopB != null && loopB > loopA) {
                            if (currentMs >= loopB) {
                                player.seekTo(loopA.toInt())
                            }
                        }
                    } else {
                        // Simulated playback timer for mock duration
                        val nextPos = (_currentPositionMs.value + 100).coerceAtMost(_totalDurationMs.value)
                        _currentPositionMs.value = nextPos
                        _progress.value = (nextPos.toFloat() / _totalDurationMs.value.toFloat()).coerceIn(0f, 1f)
                        if (nextPos >= _totalDurationMs.value) {
                            _currentPositionMs.value = 0L
                            _progress.value = 0f
                        }
                    }

                    // Dynamic VU Meter fluctuations (-20 to +3 dB)
                    val baseL = -4.0 + sin(phase * 1.7) * 3.5 + (Random.nextDouble() * 2.0 - 1.0)
                    val baseR = -3.5 + sin(phase * 1.5 + 0.5) * 3.2 + (Random.nextDouble() * 2.0 - 1.0)
                    val valL = baseL.toFloat().coerceIn(-20f, 3f)
                    val valR = baseR.toFloat().coerceIn(-20f, 3f)
                    _vuLeftDb.value = (valL * 10).toInt() / 10f
                    _vuRightDb.value = (valR * 10).toInt() / 10f
                    _isPeakClip.value = (valL > 1.5f || valR > 1.5f)

                    // Dynamic FFT Bars
                    val newFft = _fftBars.value.mapIndexed { idx, cur ->
                        val target = 0.25f + (sin(phase * 2.0 + idx * 0.4).toFloat() * 0.35f + Random.nextFloat() * 0.3f).coerceIn(0.05f, 0.65f)
                        (cur * 0.6f + target * 0.4f).coerceIn(0.1f, 0.95f)
                    }
                    _fftBars.value = newFft
                } else {
                    // Resting state
                    _vuLeftDb.value = -22.0f
                    _vuRightDb.value = -22.0f
                    _isPeakClip.value = false
                    val decayedFft = _fftBars.value.map { max(0.05f, it * 0.85f) }
                    _fftBars.value = decayedFft
                }
            }
        }
    }

    fun play() {
        if (mediaPlayer == null) {
            setupMediaPlayerForTrack(_currentTrack.value)
        }
        try {
            mediaPlayer?.start()
            _isPlaying.value = true
        } catch (e: Exception) {
            Log.e("CyberAudioEngine", "Error playing media", e)
            _isPlaying.value = true // Keep UI reactive
        }
    }

    fun pause() {
        try {
            mediaPlayer?.pause()
        } catch (e: Exception) {
            Log.e("CyberAudioEngine", "Error pausing", e)
        }
        _isPlaying.value = false
    }

    fun togglePlay() {
        if (_isPlaying.value) {
            pause()
        } else {
            play()
        }
    }

    fun stop() {
        try {
            mediaPlayer?.pause()
            mediaPlayer?.seekTo(0)
        } catch (e: Exception) {
            Log.e("CyberAudioEngine", "Error stopping", e)
        }
        _isPlaying.value = false
        _currentPositionMs.value = 0L
        _progress.value = 0f
    }

    fun rewind() {
        val target = max(0L, _currentPositionMs.value - 10000L)
        seekToTime(target)
    }

    fun fastForward() {
        val target = min(_totalDurationMs.value, _currentPositionMs.value + 10000L)
        seekToTime(target)
    }

    fun seekTo(progressFraction: Float) {
        val targetMs = (progressFraction.coerceIn(0f, 1f) * _totalDurationMs.value).toLong()
        seekToTime(targetMs)
    }

    private fun seekToTime(timeMs: Long) {
        _currentPositionMs.value = timeMs
        _progress.value = (timeMs.toFloat() / _totalDurationMs.value.toFloat()).coerceIn(0f, 1f)
        try {
            mediaPlayer?.seekTo(timeMs.toInt())
        } catch (e: Exception) {
            Log.e("CyberAudioEngine", "Error seeking", e)
        }
    }

    fun loadTrack(track: AudioTrack, autoPlay: Boolean = true) {
        _currentTrack.value = track
        _currentPositionMs.value = 0L
        _progress.value = 0f
        _totalDurationMs.value = track.durationMs

        setupMediaPlayerForTrack(track)

        if (autoPlay) {
            play()
        }
    }

    private fun setupMediaPlayerForTrack(track: AudioTrack) {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            // ignore
        }
        mediaPlayer = MediaPlayer().apply {
            setOnCompletionListener {
                _isPlaying.value = false
                _currentPositionMs.value = 0L
                _progress.value = 0f
            }
            isLooping = true
        }

        try {
            if (track.uriString.isNotEmpty()) {
                val uri = Uri.parse(track.uriString)
                mediaPlayer?.setDataSource(context, uri)
                mediaPlayer?.prepare()
            } else {
                // Find or create synthetic track file
                val trackNum = when (track.id) {
                    "t1" -> 1
                    "t2" -> 2
                    "t3" -> 3
                    else -> 4
                }
                val synthFile = WavAudioGenerator.getOrCreateSynthTrack(context, trackNum)
                mediaPlayer?.setDataSource(synthFile.absolutePath)
                mediaPlayer?.prepare()
            }
            updateHardwareVolume()
        } catch (e: Exception) {
            Log.e("CyberAudioEngine", "Error preparing MediaPlayer for ${track.title}", e)
        }
    }

    fun insertMixtape(mixtape: Mixtape) {
        _selectedMixtape.value = mixtape
        // Load first track of mixtape
        val firstTrackItem = mixtape.tracks.firstOrNull()
        if (firstTrackItem != null) {
            val tapeTrack = AudioTrack(
                id = "tape_${mixtape.id}_01",
                title = "${firstTrackItem.index}_${firstTrackItem.title.replace(' ', '_')}.flac",
                artist = mixtape.curator,
                album = mixtape.title,
                durationMs = 252000L,
                codec = "FLAC",
                spec = "96kHz/24bit",
                bitrate = "1411 kbps LOSSLESS",
                fileSizeStr = "52 MB",
                coverArtUrl = mixtape.coverArtUrl
            )
            loadTrack(tapeTrack, autoPlay = true)
        }
    }

    fun toggleFavorite(trackId: String) {
        _tracks.update { list ->
            list.map { if (it.id == trackId) it.copy(isFavorite = !it.isFavorite) else it }
        }
        if (_currentTrack.value.id == trackId) {
            _currentTrack.update { it.copy(isFavorite = !it.isFavorite) }
        }
    }

    fun setOutputLevel(levelDb: Float) {
        _outputLevelDb.value = levelDb.coerceIn(-24f, 12f)
        updateHardwareVolume()
    }

    private fun updateHardwareVolume() {
        val gainDb = _outputLevelDb.value
        // Map dB (-24dB to +12dB) to volume float 0.0f..1.0f
        val vol = if (gainDb <= -20f) 0.0f else (1.0f + (gainDb / 12f) * 0.5f).coerceIn(0f, 1.0f)
        try {
            mediaPlayer?.setVolume(vol, vol)
        } catch (e: Exception) {
            // ignore
        }
    }

    fun setBassBoost(enabled: Boolean) {
        _isBassBoost.value = enabled
    }

    fun setDolbyB(enabled: Boolean) {
        _isDolbyB.value = enabled
    }

    fun setWowDrift(hz: Float) {
        _wowDriftHz.value = hz
    }

    fun setFlutter(hz: Float) {
        _flutterHz.value = hz
    }

    fun toggleMasterDsp() {
        _isDspOnline.value = !_isDspOnline.value
    }

    fun toggleValveStage() {
        _isValveStageOn.value = !_isValveStageOn.value
    }

    fun setValveDrive(drive: Int) {
        _valveDrive.value = drive.coerceIn(0, 100)
    }

    fun setEqBand(index: Int, gain: Float) {
        _eqBands.update { current ->
            val mutable = current.toMutableList()
            if (index in mutable.indices) {
                mutable[index] = gain.coerceIn(-12f, 12f)
            }
            mutable
        }
    }

    fun resetEq() {
        _eqBands.value = List(10) { 0.0f }
    }

    fun applyPreset(presetId: String) {
        _selectedPreset.value = presetId
        val preset = dspPresets.find { it.id == presetId }
        if (preset != null) {
            _eqBands.value = preset.gains
        }
    }

    fun setLoopA() {
        _loopPointA.value = _currentPositionMs.value
        _isLoopEnabled.value = true
    }

    fun setLoopB() {
        _loopPointB.value = _currentPositionMs.value
        _isLoopEnabled.value = true
    }

    fun toggleLoop() {
        _isLoopEnabled.value = !_isLoopEnabled.value
        if (!_isLoopEnabled.value) {
            _loopPointA.value = null
            _loopPointB.value = null
        }
    }

    fun setDriveBay(bay: String) {
        _driveBay.value = bay
    }

    fun setCodecFilter(codec: String) {
        _codecFilter.value = codec
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun triggerDiskScan() {
        scope.launch {
            _isScanning.value = true
            _scanStatusText.value = "READING_SECTOR_BLOCKS..."
            _scanProgress.value = 0.15f
            delay(300)

            _scanStatusText.value = "SCANNING LOCAL STORAGE & ID3..."
            _scanProgress.value = 0.65f

            // Scan device MediaStore for actual songs on device
            val scannedTracks = scanMediaStoreAudio()
            delay(400)

            if (scannedTracks.isNotEmpty()) {
                _tracks.value = scannedTracks + defaultTracks
                _scanStatusText.value = "INDEX COMPLETE: ${scannedTracks.size + defaultTracks.size} TRKS"
            } else {
                _scanStatusText.value = "INDEX COMPLETE: 3,420 TRKS [0.8s]"
            }
            _scanProgress.value = 1.0f
            _isScanning.value = false
        }
    }

    fun setOutputLevelDb(levelDb: Float) {
        setOutputLevel(levelDb)
    }

    fun toggleRecord() {
        triggerRec()
    }

    fun seekToFraction(progressFraction: Float) {
        seekTo(progressFraction)
    }

    fun setSelectedDriveBay(bay: String) {
        setDriveBay(bay)
    }

    fun setSelectedCodecFilter(codec: String) {
        setCodecFilter(codec)
    }

    fun setSelectedMixtape(mixtape: Mixtape) {
        _selectedMixtape.value = mixtape
    }

    fun insertMixtapeToDeck(mixtape: Mixtape) {
        insertMixtape(mixtape)
    }

    fun toggleDsp(enabled: Boolean) {
        _isDspOnline.value = enabled
    }

    fun updateBandGain(index: Int, gain: Float) {
        setEqBand(index, gain)
    }

    fun setTubeDrive(drive: Float) {
        _tubeDrive.value = drive.coerceIn(0f, 1f)
    }

    fun setStereoWidth(width: Float) {
        _stereoWidth.value = width.coerceIn(0.5f, 2.0f)
    }

    fun setSubsonicFilter(enabled: Boolean) {
        _isSubsonicFilter.value = enabled
    }

    fun importLocalAudioUri(uri: Uri): AudioTrack? {
        var createdTrack: AudioTrack? = null
        try {
            val filename = getFileNameFromUri(uri) ?: "Local_Track_${System.currentTimeMillis()}.mp3"
            val newTrack = AudioTrack(
                id = "local_${System.currentTimeMillis()}",
                title = filename,
                artist = "LOCAL PHONE STORAGE",
                album = "DISCO LOCAL",
                durationMs = 210000L,
                uriString = uri.toString(),
                codec = if (filename.endsWith(".flac", true)) "FLAC" else if (filename.endsWith(".wav", true)) "WAV" else "MP3",
                spec = "44.1kHz/16bit",
                bitrate = "320 kbps",
                fileSizeStr = "12 MB",
                coverArtUrl = TRACK1_ART,
                isLocal = true
            )
            createdTrack = newTrack
            _tracks.update { listOf(newTrack) + it }
            loadTrack(newTrack, autoPlay = true)
        } catch (e: Exception) {
            Log.e("CyberAudioEngine", "Error importing uri $uri", e)
        }
        return createdTrack
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        var name: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                if (index != -1) {
                    name = it.getString(index)
                }
            }
        }
        return name ?: uri.lastPathSegment
    }

    fun scanMediaStoreAudio(): List<AudioTrack> {
        val result = mutableListOf<AudioTrack>()
        try {
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE
            )
            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                "${MediaStore.Audio.Media.TITLE} ASC"
            )
            cursor?.use { c ->
                val idCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val sizeCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

                var count = 0
                while (c.moveToNext() && count < 25) {
                    val id = c.getLong(idCol)
                    val title = c.getString(titleCol) ?: "Track $id"
                    val artist = c.getString(artistCol) ?: "Unknown Artist"
                    val album = c.getString(albumCol) ?: "Local Audio"
                    val dur = c.getLong(durCol)
                    val sizeBytes = c.getLong(sizeCol)
                    val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

                    result.add(
                        AudioTrack(
                            id = "device_$id",
                            title = "$title.flac",
                            artist = artist,
                            album = album,
                            durationMs = if (dur > 0) dur else 180000L,
                            uriString = contentUri.toString(),
                            codec = "FLAC",
                            spec = "48kHz/24bit",
                            bitrate = "960 kbps",
                            fileSizeStr = "${sizeBytes / (1024 * 1024)} MB",
                            coverArtUrl = TRACK2_ART,
                            isLocal = true
                        )
                    )
                    count++
                }
            }
        } catch (e: Exception) {
            Log.e("CyberAudioEngine", "MediaStore scan exception", e)
        }
        if (result.isNotEmpty()) {
            _tracks.value = result + defaultTracks
        }
        return result
    }

    fun triggerRec() {
        _isRecording.value = !_isRecording.value
    }

    fun release() {
        telemetryJob?.cancel()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            // ignore
        }
        mediaPlayer = null
    }
}
