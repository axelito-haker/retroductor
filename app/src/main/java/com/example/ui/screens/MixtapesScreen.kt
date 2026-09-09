package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Eject
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.AudioTrack
import com.example.model.Mixtape
import com.example.ui.components.MiniPlayerDock
import com.example.ui.theme.ChassisSurfaceBright
import com.example.ui.theme.ChassisSurfaceContainer
import com.example.ui.theme.ChassisSurfaceDim
import com.example.ui.theme.ChassisSurfaceHigh
import com.example.ui.theme.ChassisSurfaceHighest
import com.example.ui.theme.ChassisSurfaceLow
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkHot
import com.example.ui.theme.SilkscreenBorder
import com.example.ui.theme.SilkscreenDim
import com.example.ui.theme.SilkscreenMuted
import com.example.ui.theme.SilkscreenWhite
import com.example.ui.theme.ValveAmber

@Composable
fun MixtapesScreen(
    mixtapes: List<Mixtape>,
    selectedMixtape: Mixtape,
    currentTrack: AudioTrack,
    isPlaying: Boolean,
    progress: Float,
    currentPosFormatted: String,
    totalDurFormatted: String,
    onSelectMixtape: (Mixtape) -> Unit,
    onInsertIntoDeck: (Mixtape) -> Unit,
    onTogglePlay: () -> Unit,
    onRewind: () -> Unit,
    onFastForward: () -> Unit,
    onSeek: (Float) -> Unit,
    onOpenDeck: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAllTracks by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "tape_spool_spin")
    val spoolRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spoolRotation"
    )

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(6.dp)) }

            // 1. RACK ENCLOSURE HEADER STRIP
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ChassisSurfaceDim)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(NeonPinkHot))
                        Text(
                            text = "BAY-04 // TAPE VAULT ARCHIVE",
                            color = NeonPink,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = "Sensors",
                            tint = SilkscreenMuted,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "READY-REC",
                            color = SilkscreenMuted,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 2. HERO CASSETTE VITRINE CAROUSEL
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "VITRINA DE COCCIÓN MAGNÉTICA",
                                color = SilkscreenDim,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Casetes Virtuales",
                                color = SilkscreenWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(ChassisSurfaceHighest, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${mixtapes.indexOfFirst { it.id == selectedMixtape.id } + 1} / 8 EN ESTANTE",
                                color = ElectricViolet,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Horizontal Carousel Cards
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(mixtapes) { index, tape ->
                            val isSelected = tape.id == selectedMixtape.id
                            val accentColor = Color(tape.accentColorHex)

                            Box(
                                modifier = Modifier
                                    .width(260.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) ChassisSurfaceHigh else ChassisSurfaceLow)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) accentColor else ChassisSurfaceHighest,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onSelectMixtape(tape) }
                                    .padding(8.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // Tape J-Card Art Cover
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(ChassisSurfaceDim)
                                    ) {
                                        AsyncImage(
                                            model = tape.coverArtUrl,
                                            contentDescription = tape.title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )

                                        // Badge
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(6.dp)
                                                .background(ChassisSurfaceDim.copy(alpha = 0.9f), RoundedCornerShape(2.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = tape.biasBadge,
                                                color = accentColor,
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(6.dp)
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(accentColor)
                                        )
                                    }

                                    // Tape Reel Window
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(ChassisSurfaceContainer)
                                            .padding(vertical = 4.dp, horizontal = 12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceAround,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Left Spool
                                            Box(
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(CircleShape)
                                                    .background(ChassisSurfaceDim),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Adjust,
                                                    contentDescription = null,
                                                    tint = accentColor,
                                                    modifier = Modifier
                                                        .size(22.dp)
                                                        .rotate(if (isPlaying && isSelected) spoolRotation else 0f)
                                                )
                                            }

                                            // Center Tape Ribbon
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(horizontal = 8.dp)
                                                    .height(8.dp)
                                                    .clip(RoundedCornerShape(2.dp))
                                                    .background(ChassisSurfaceDim)
                                                    .padding(1.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth(0.6f)
                                                        .height(6.dp)
                                                        .background(accentColor.copy(alpha = 0.8f))
                                                )
                                            }

                                            // Right Spool
                                            Box(
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(CircleShape)
                                                    .background(ChassisSurfaceDim),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Adjust,
                                                    contentDescription = null,
                                                    tint = SilkscreenDim,
                                                    modifier = Modifier
                                                        .size(22.dp)
                                                        .rotate(if (isPlaying && isSelected) spoolRotation else 0f)
                                                )
                                            }
                                        }
                                    }

                                    // Typography Plate
                                    Column(modifier = Modifier.padding(horizontal = 2.dp)) {
                                        Text(
                                            text = tape.subtitle,
                                            color = accentColor,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )
                                        Text(
                                            text = tape.title,
                                            color = SilkscreenWhite,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "${tape.tracks.size} PISTAS • ${tape.durationLabel} • ${tape.tapeType}",
                                            color = SilkscreenMuted,
                                            fontSize = 8.sp,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Card: Record New Tape Blank Slot
                        item {
                            Box(
                                modifier = Modifier
                                    .width(220.dp)
                                    .height(240.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ChassisSurfaceLow)
                                    .border(1.dp, ChassisSurfaceHighest, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .background(ChassisSurfaceHighest),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FiberManualRecord,
                                            contentDescription = "New Mixtape",
                                            tint = NeonPinkHot,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    Text(
                                        text = "GRABAR NUEVO MIXTAPE",
                                        color = SilkscreenWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "RANURA #04 DISPONIBLE",
                                        color = SilkscreenMuted,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Row(
                                        modifier = Modifier
                                            .background(ChassisSurfaceDim, RoundedCornerShape(3.dp))
                                            .padding(horizontal = 6.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.LockOpen, contentDescription = null, tint = ValveAmber, modifier = Modifier.size(12.dp))
                                        Text("PESTAÑAS DE GRABACIÓN OK", color = ValveAmber, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. SELECTED CASSETTE TERMINAL BAY
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ChassisSurfaceLow)
                        .border(1.dp, ChassisSurfaceHighest, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Header info & Holo Seal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(ChassisSurfaceHighest, RoundedCornerShape(3.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "SLOT A-LOADED",
                                        color = NeonPink,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "SERIAL: CKD-9602-MX",
                                    color = SilkscreenDim,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            // Holo seal
                            Row(
                                modifier = Modifier
                                    .background(ChassisSurfaceHighest, RoundedCornerShape(3.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = ElectricViolet, modifier = Modifier.size(13.dp))
                                Text(
                                    text = "HOLO-SEAL AUTH",
                                    color = ElectricViolet,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Index Label Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ChassisSurfaceDim)
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "CASSETTE INDEX LABEL", color = SilkscreenDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                    Text(text = selectedMixtape.durationLabel, color = NeonPink, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    text = selectedMixtape.title,
                                    color = SilkscreenWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = selectedMixtape.curator,
                                    color = SilkscreenMuted,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Big Primary Action Button: INSERT INTO DECK PLAYER
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonPinkHot)
                                .clickable { onInsertIntoDeck(selectedMixtape) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Eject, contentDescription = null, tint = SilkscreenWhite, modifier = Modifier.size(20.dp))
                                Text(
                                    text = "INSERT INTO DECK PLAYER",
                                    color = SilkscreenWhite,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = SilkscreenWhite, modifier = Modifier.size(18.dp))
                            }
                        }

                        // Tape Tracklist
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.FormatListNumbered, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "CONTENIDO DE LA CINTA",
                                        color = SilkscreenWhite,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Text(
                                    text = "${selectedMixtape.tracks.size} TEMAS INDEXADOS",
                                    color = SilkscreenDim,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            val visibleTracks = if (showAllTracks) selectedMixtape.tracks else selectedMixtape.tracks.take(5)
                            visibleTracks.forEach { trackItem ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ChassisSurfaceContainer)
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = trackItem.index, color = NeonPink, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                        Box(
                                            modifier = Modifier
                                                .background(ChassisSurfaceDim, RoundedCornerShape(2.dp))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(text = trackItem.marker, color = SilkscreenDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = trackItem.title, color = SilkscreenWhite, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                                            Text(text = trackItem.soundchip, color = SilkscreenMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                        }
                                    }
                                    Text(text = trackItem.duration, color = SilkscreenMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                }
                            }

                            // View all tracks toggle
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ChassisSurfaceHighest)
                                    .clickable { showAllTracks = !showAllTracks }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = if (showAllTracks) "COLAPSAR LISTA" else "VER TODAS LAS PISTAS",
                                        color = SilkscreenMuted,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(Icons.Default.ExpandMore, contentDescription = null, tint = SilkscreenMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            // 4. ANALOG DECK HARDWARE TELEMETRY (3-GRID BENTO)
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "HARDWARE TELEMETRÍA // ESTADÍSTICAS", color = SilkscreenDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ValveAmber))
                                Text(text = "RACK ONLINE", color = ValveAmber, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Bento 1
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ChassisSurfaceDim)
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text("TOTAL CINTAS", color = SilkscreenDim, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                    Text("8", color = NeonPink, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    Text("5 Ranuras Libres", color = SilkscreenMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                }
                            }

                            // Bento 2
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ChassisSurfaceDim)
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text("ALMACENADO", color = SilkscreenDim, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                    Text("14.8 GB", color = ElectricViolet, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    Text("SSD NVMe R/W", color = SilkscreenMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                }
                            }

                            // Bento 3
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ChassisSurfaceDim)
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text("FIDELIDAD", color = SilkscreenDim, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                    Text("24-bit", color = ValveAmber, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    Text("Lossless 96kHz", color = SilkscreenMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
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
