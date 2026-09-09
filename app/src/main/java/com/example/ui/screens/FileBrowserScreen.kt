package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.AudioTrack
import com.example.ui.components.MiniPlayerDock
import com.example.ui.theme.ChassisSurfaceContainer
import com.example.ui.theme.ChassisSurfaceDim
import com.example.ui.theme.ChassisSurfaceHigh
import com.example.ui.theme.ChassisSurfaceHighest
import com.example.ui.theme.ChassisSurfaceLow
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkDeep
import com.example.ui.theme.NeonPinkHot
import com.example.ui.theme.SilkscreenDim
import com.example.ui.theme.SilkscreenMuted
import com.example.ui.theme.SilkscreenWhite
import com.example.ui.theme.ValveAmber
import com.example.ui.theme.ValveAmberDark

@Composable
fun FileBrowserScreen(
    tracks: List<AudioTrack>,
    currentTrack: AudioTrack,
    isPlaying: Boolean,
    progress: Float,
    currentPosFormatted: String,
    totalDurFormatted: String,
    selectedDriveBay: String,
    selectedCodecFilter: String,
    searchQuery: String,
    isScanning: Boolean,
    scanStatusText: String,
    scanProgress: Float,
    onSelectDriveBay: (String) -> Unit,
    onSelectCodecFilter: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onTriggerScan: () -> Unit,
    onLoadTrackToDeck: (AudioTrack) -> Unit,
    onImportLocalFile: (Uri) -> Unit,
    onTogglePlay: () -> Unit,
    onRewind: () -> Unit,
    onFastForward: () -> Unit,
    onSeek: (Float) -> Unit,
    onOpenDeck: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedTrackId by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            onImportLocalFile(uri)
        }
    }

    // Filter tracks based on search & codec
    val filteredTracks = tracks.filter { track ->
        val matchesSearch = searchQuery.isEmpty() ||
                track.title.contains(searchQuery, ignoreCase = true) ||
                track.artist.contains(searchQuery, ignoreCase = true)
        val matchesCodec = when (selectedCodecFilter) {
            "ALL" -> true
            ".FLAC (96k)" -> track.codec.equals("FLAC", true)
            ".WAV (PCM)" -> track.codec.equals("WAV", true)
            ".MP3 (320k)" -> track.codec.equals("MP3", true)
            else -> true
        }
        matchesSearch && matchesCodec
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Spacer(modifier = Modifier.height(6.dp)) }

            // 1. STORAGE MOUNT SELECTOR
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ChassisSurfaceDim)
                        .border(1.dp, ChassisSurfaceHighest, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(NeonPinkHot))
                                Text(
                                    text = "DRIVE_BAY // MOUNT_01",
                                    color = NeonPink,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = "SECTOR_ONLINE",
                                color = ValveAmber,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Storage switch buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ChassisSurfaceHigh, RoundedCornerShape(8.dp))
                                .padding(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // INT_STORAGE
                            val isInt = selectedDriveBay == "INT_STORAGE"
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isInt) ChassisSurfaceContainer else ChassisSurfaceDim)
                                    .clickable { onSelectDriveBay("INT_STORAGE") }
                                    .padding(vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Smartphone,
                                        contentDescription = "Internal",
                                        tint = if (isInt) NeonPink else SilkscreenDim,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "INT_STORAGE",
                                        color = if (isInt) NeonPink else SilkscreenDim,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "32.4GB FRE",
                                    color = SilkscreenMuted,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            // EXT_SD_1TB
                            val isSd = selectedDriveBay == "EXT_SD_1TB"
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSd) ChassisSurfaceContainer else ChassisSurfaceDim)
                                    .clickable { onSelectDriveBay("EXT_SD_1TB") }
                                    .padding(vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SdCard,
                                        contentDescription = "SD Card",
                                        tint = if (isSd) NeonPink else SilkscreenDim,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "EXT_SD_1TB",
                                        color = if (isSd) NeonPink else SilkscreenDim,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "FAT32_MNT",
                                    color = ValveAmber,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            // TREE_DIR
                            val isTree = selectedDriveBay == "TREE_DIR"
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isTree) ChassisSurfaceContainer else ChassisSurfaceDim)
                                    .clickable { onSelectDriveBay("TREE_DIR") }
                                    .padding(vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountTree,
                                        contentDescription = "Tree",
                                        tint = if (isTree) NeonPink else SilkscreenDim,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "TREE_DIR",
                                        color = if (isTree) NeonPink else SilkscreenDim,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "ROOT:/SYS",
                                    color = SilkscreenMuted,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Path Breadcrumb
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ChassisSurfaceLow, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Terminal,
                                    contentDescription = "Terminal",
                                    tint = NeonPink,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "MOUNT://sdcard/Music/Synthwave & Darksynth/",
                                    color = SilkscreenMuted,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = "IO_SPEED: 95MB/s",
                                color = ValveAmberDark,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 2. SCANNER MODULE & TOOLBAR
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ChassisSurfaceLow)
                        .border(1.dp, ChassisSurfaceHighest, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Scan Disk Button
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonPinkHot)
                                    .clickable { onTriggerScan() }
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Radar,
                                    contentDescription = "Scan Disk",
                                    tint = SilkscreenWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "SCAN DISK",
                                    color = SilkscreenWhite,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            // Import Local Phone Audio File Button
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ChassisSurfaceHighest)
                                    .clickable { filePickerLauncher.launch(arrayOf("audio/*")) }
                                    .padding(horizontal = 8.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FileUpload,
                                    contentDescription = "Import",
                                    tint = ValveAmber,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "+ IMPORTAR",
                                    color = ValveAmber,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Scan progress telemetry
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.End
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(NeonPinkHot)
                                    )
                                    Text(
                                        text = scanStatusText,
                                        color = NeonPink,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(3.dp)
                                        .clip(CircleShape)
                                        .background(ChassisSurfaceDim)
                                        .padding(top = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(scanProgress)
                                            .height(3.dp)
                                            .background(NeonPinkHot)
                                    )
                                }
                            }
                        }

                        // Search Bar Input
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(ChassisSurfaceDim)
                                .border(1.dp, ChassisSurfaceHighest, RoundedCornerShape(4.dp))
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SEARCH:// ",
                                color = NeonPink,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = onSearchChange,
                                textStyle = TextStyle(
                                    color = SilkscreenWhite,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                cursorBrush = SolidColor(NeonPinkHot),
                                modifier = Modifier.weight(1f),
                                decorationBox = { innerTextField ->
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "local_path / tag / format...",
                                            color = SilkscreenDim,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                            Text(
                                text = "▋",
                                color = NeonPinkHot,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Codec Filters
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "CODEC:",
                                color = SilkscreenMuted,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            val codecs = listOf("ALL", ".FLAC (96k)", ".WAV (PCM)", ".MP3 (320k)")
                            codecs.forEach { codec ->
                                val isSelected = selectedCodecFilter == codec
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(if (isSelected) NeonPinkHot else ChassisSurfaceHighest)
                                        .clickable { onSelectCodecFilter(codec) }
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = codec,
                                        color = if (isSelected) SilkscreenWhite else NeonPink,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. BROWSER DIRECTORY HEADER STRIP
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ChassisSurfaceHigh)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderSpecial,
                            contentDescription = "Folder",
                            tint = ValveAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "[DIR] Synthwave & Darksynth",
                            color = SilkscreenWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(ChassisSurfaceDim, RoundedCornerShape(3.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${filteredTracks.size} TRKS",
                            color = ValveAmber,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 4. HIGH FIDELITY TERMINAL TRACK CARDS
            itemsIndexed(filteredTracks) { index, track ->
                val isExpanded = expandedTrackId == track.id
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ChassisSurfaceLow)
                        .border(1.dp, ChassisSurfaceHighest, RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Cover Thumbnail with Track Index
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ChassisSurfaceHighest),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (track.coverArtUrl.isNotEmpty()) {
                                        AsyncImage(
                                            model = track.coverArtUrl,
                                            contentDescription = track.title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.AudioFile,
                                            contentDescription = null,
                                            tint = NeonPink,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Text(
                                        text = "%02d".format(index + 1),
                                        color = NeonPinkHot,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .background(ChassisSurfaceDim.copy(alpha = 0.8f))
                                            .padding(horizontal = 2.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = track.title,
                                        color = if (track.id == currentTrack.id) NeonPink else SilkscreenWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(NeonPinkHot, RoundedCornerShape(2.dp))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = track.codec,
                                                color = SilkscreenWhite,
                                                fontSize = 8.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(
                                            text = track.spec,
                                            color = ValveAmber,
                                            fontSize = 8.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(text = "•", color = SilkscreenDim, fontSize = 8.sp)
                                        Text(
                                            text = track.fileSizeStr,
                                            color = SilkscreenMuted,
                                            fontSize = 8.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(text = "•", color = SilkscreenDim, fontSize = 8.sp)
                                        Text(
                                            text = track.durationFormatted,
                                            color = SilkscreenMuted,
                                            fontSize = 8.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }

                            // Action buttons: Load to Deck & Context Menu
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(NeonPinkHot)
                                        .clickable { onLoadTrackToDeck(track) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Load to Deck",
                                        tint = SilkscreenWhite,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ChassisSurfaceHighest)
                                        .clickable {
                                            expandedTrackId = if (isExpanded) null else track.id
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "More",
                                        tint = SilkscreenWhite,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Collapsible contextual action drawer
                        AnimatedVisibility(visible = isExpanded) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ChassisSurfaceDim)
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(ChassisSurfaceContainer)
                                        .clickable { /* inspect */ }
                                        .padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(Icons.Default.DataObject, contentDescription = null, tint = NeonPink, modifier = Modifier.size(13.dp))
                                    Text("INSPECT HEX", color = NeonPink, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                }
                                Spacer(modifier = Modifier.size(4.dp))
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(ChassisSurfaceContainer)
                                        .clickable { /* id3 */ }
                                        .padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(Icons.Default.Label, contentDescription = null, tint = ElectricViolet, modifier = Modifier.size(13.dp))
                                    Text("ID3 TAGS", color = ElectricViolet, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                }
                                Spacer(modifier = Modifier.size(4.dp))
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(ChassisSurfaceContainer)
                                        .clickable { /* add */ }
                                        .padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(Icons.Default.PlaylistAdd, contentDescription = null, tint = ValveAmber, modifier = Modifier.size(13.dp))
                                    Text("ADD MIXTAPE", color = ValveAmber, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(140.dp)) }
        }

        // Floating Sticky Mini Player Dock
        MiniPlayerDock(
            currentTrack = currentTrack,
            isPlaying = isPlaying,
            progress = progress,
            currentPosFormatted = currentPosFormatted,
            totalDurFormatted = totalDurFormatted,
            onTogglePlay = onTogglePlay,
            onRewind = onRewind,
            onFastForward = onFastForward,
            onSeek = onSeek,
            onOpenDeck = onOpenDeck,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 12.dp, vertical = 68.dp)
        )
    }
}
