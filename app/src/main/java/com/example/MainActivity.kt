package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.audio.CyberAudioEngine
import com.example.ui.components.CyberBottomNav
import com.example.ui.components.CyberHeader
import com.example.ui.components.CyberTab
import com.example.ui.screens.DeckPlayerScreen
import com.example.ui.screens.EqualizerDspScreen
import com.example.ui.screens.FileBrowserScreen
import com.example.ui.screens.MixtapesScreen
import com.example.ui.theme.ChassisSurfaceDim
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private var audioEngine: CyberAudioEngine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val engine = CyberAudioEngine(applicationContext)
        audioEngine = engine

        setContent {
            MyApplicationTheme {
                CyberDeckApp(engine = engine)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioEngine?.release()
    }
}

@Composable
fun CyberDeckApp(engine: CyberAudioEngine) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(CyberTab.DECK) }

    // Request permissions for scanning local audio
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        engine.scanMediaStoreAudio()
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
            )
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            )
        }
    }

    // State bindings
    val tracks by engine.tracks.collectAsState()
    val currentTrack by engine.currentTrack.collectAsState()
    val isPlaying by engine.isPlaying.collectAsState()
    val isRecording by engine.isRecording.collectAsState()
    val currentPositionMs by engine.currentPositionMs.collectAsState()
    val totalDurationMs by engine.totalDurationMs.collectAsState()
    val progress by engine.progress.collectAsState()
    val vuLeftDb by engine.vuLeftDb.collectAsState()
    val vuRightDb by engine.vuRightDb.collectAsState()
    val isPeakClip by engine.isPeakClip.collectAsState()

    val eqBandsDb by engine.eqBandsDb.collectAsState()
    val isDspEnabled by engine.isDspEnabled.collectAsState()
    val fftBars by engine.fftBars.collectAsState()
    val selectedPresetName by engine.selectedPresetName.collectAsState()
    val dspPresets = engine.dspPresets

    val mixtapes by engine.mixtapes.collectAsState()
    val selectedMixtape by engine.selectedMixtape.collectAsState()

    val outputLevelDb by engine.outputLevelDb.collectAsState()
    val isBassBoost by engine.isBassBoost.collectAsState()
    val isDolbyB by engine.isDolbyB.collectAsState()
    val isLoopEnabled by engine.isLoopEnabled.collectAsState()
    val tubeDrive by engine.tubeDrive.collectAsState()
    val stereoWidth by engine.stereoWidth.collectAsState()
    val isSubsonicFilter by engine.isSubsonicFilter.collectAsState()

    val selectedDriveBay by engine.selectedDriveBay.collectAsState()
    val selectedCodecFilter by engine.selectedCodecFilter.collectAsState()
    val searchQuery by engine.searchQuery.collectAsState()
    val isScanning by engine.isScanning.collectAsState()
    val scanStatusText by engine.scanStatusText.collectAsState()
    val scanProgress by engine.scanProgress.collectAsState()

    fun formatMs(ms: Long): String {
        val totalSec = (ms / 1000).coerceAtLeast(0)
        val m = totalSec / 60
        val s = totalSec % 60
        return "%02d:%02d".format(m, s)
    }

    val headerTitle = when (selectedTab) {
        CyberTab.DECK -> "Deck Player"
        CyberTab.FILES -> "File Browser"
        CyberTab.PLAYLISTS -> "Mixtapes"
        CyberTab.EQUALIZER -> "Equalizer Dsp"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CyberHeader(title = headerTitle)
        },
        bottomBar = {
            CyberBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = ChassisSurfaceDim
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ChassisSurfaceDim)
        ) {
            when (selectedTab) {
                CyberTab.DECK -> {
                    DeckPlayerScreen(
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        isRecording = isRecording,
                        currentPosMs = currentPositionMs,
                        totalDurMs = totalDurationMs,
                        progress = progress,
                        vuLeftDb = vuLeftDb,
                        vuRightDb = vuRightDb,
                        isPeakClip = isPeakClip,
                        outputLevelDb = outputLevelDb,
                        isBassBoost = isBassBoost,
                        isDolbyB = isDolbyB,
                        isLoopEnabled = isLoopEnabled,
                        onTogglePlay = { engine.togglePlay() },
                        onStop = { engine.stop() },
                        onRewind = { engine.rewind() },
                        onFastForward = { engine.fastForward() },
                        onToggleRecord = { engine.toggleRecord() },
                        onSeek = { engine.seekToFraction(it) },
                        onToggleFavorite = { engine.toggleFavorite(currentTrack.id) },
                        onSetLoopA = { engine.setLoopA() },
                        onSetLoopB = { engine.setLoopB() },
                        onToggleLoop = { engine.toggleLoop() },
                        onOutputLevelChange = { engine.setOutputLevelDb(it) },
                        onBassBoostToggle = { engine.setBassBoost(it) },
                        onDolbyBToggle = { engine.setDolbyB(it) }
                    )
                }
                CyberTab.FILES -> {
                    FileBrowserScreen(
                        tracks = tracks,
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        progress = progress,
                        currentPosFormatted = formatMs(currentPositionMs),
                        totalDurFormatted = formatMs(totalDurationMs),
                        selectedDriveBay = selectedDriveBay,
                        selectedCodecFilter = selectedCodecFilter,
                        searchQuery = searchQuery,
                        isScanning = isScanning,
                        scanStatusText = scanStatusText,
                        scanProgress = scanProgress,
                        onSelectDriveBay = { engine.setSelectedDriveBay(it) },
                        onSelectCodecFilter = { engine.setSelectedCodecFilter(it) },
                        onSearchChange = { engine.setSearchQuery(it) },
                        onTriggerScan = { engine.scanMediaStoreAudio() },
                        onLoadTrackToDeck = { track ->
                            engine.loadTrack(track)
                            selectedTab = CyberTab.DECK
                        },
                        onImportLocalFile = { uri ->
                            val imported = engine.importLocalAudioUri(uri)
                            if (imported != null) {
                                engine.loadTrack(imported)
                                selectedTab = CyberTab.DECK
                            }
                        },
                        onTogglePlay = { engine.togglePlay() },
                        onRewind = { engine.rewind() },
                        onFastForward = { engine.fastForward() },
                        onSeek = { engine.seekToFraction(it) },
                        onOpenDeck = { selectedTab = CyberTab.DECK }
                    )
                }
                CyberTab.PLAYLISTS -> {
                    MixtapesScreen(
                        mixtapes = mixtapes,
                        selectedMixtape = selectedMixtape,
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        progress = progress,
                        currentPosFormatted = formatMs(currentPositionMs),
                        totalDurFormatted = formatMs(totalDurationMs),
                        onSelectMixtape = { engine.setSelectedMixtape(it) },
                        onInsertIntoDeck = { tape ->
                            engine.insertMixtapeToDeck(tape)
                            selectedTab = CyberTab.DECK
                        },
                        onTogglePlay = { engine.togglePlay() },
                        onRewind = { engine.rewind() },
                        onFastForward = { engine.fastForward() },
                        onSeek = { engine.seekToFraction(it) },
                        onOpenDeck = { selectedTab = CyberTab.DECK }
                    )
                }
                CyberTab.EQUALIZER -> {
                    EqualizerDspScreen(
                        presets = dspPresets,
                        selectedPresetName = selectedPresetName,
                        eqBandsDb = eqBandsDb,
                        isDspEnabled = isDspEnabled,
                        fftBars = fftBars,
                        tubeDrive = tubeDrive,
                        stereoWidth = stereoWidth,
                        isSubsonicFilter = isSubsonicFilter,
                        onToggleDsp = { engine.toggleDsp(it) },
                        onSelectPreset = { engine.applyPreset(it) },
                        onBandChange = { band, db -> engine.updateBandGain(band, db) },
                        onTubeDriveChange = { engine.setTubeDrive(it) },
                        onStereoWidthChange = { engine.setStereoWidth(it) },
                        onSubsonicFilterToggle = { engine.setSubsonicFilter(it) },
                        onResetEq = { engine.resetEq() }
                    )
                }
            }
        }
    }
}
