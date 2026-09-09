package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.CyberAudioEngine
import com.example.model.AudioTrack
import com.example.ui.components.CassetteDeckView
import com.example.ui.components.VuMetersView
import com.example.ui.theme.ChassisSurfaceBright
import com.example.ui.theme.ChassisSurfaceContainer
import com.example.ui.theme.ChassisSurfaceDim
import com.example.ui.theme.ChassisSurfaceHigh
import com.example.ui.theme.ChassisSurfaceHighest
import com.example.ui.theme.ChassisSurfaceLow
import com.example.ui.theme.ClipRed
import com.example.ui.theme.ClipRedContainer
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkDark
import com.example.ui.theme.NeonPinkDeep
import com.example.ui.theme.NeonPinkHot
import com.example.ui.theme.SilkscreenBorder
import com.example.ui.theme.SilkscreenDim
import com.example.ui.theme.SilkscreenMuted
import com.example.ui.theme.SilkscreenWhite
import com.example.ui.theme.ValveAmber
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DeckPlayerScreen(
    currentTrack: AudioTrack,
    isPlaying: Boolean,
    isRecording: Boolean,
    currentPosMs: Long,
    totalDurMs: Long,
    progress: Float,
    vuLeftDb: Float,
    vuRightDb: Float,
    isPeakClip: Boolean,
    outputLevelDb: Float,
    isBassBoost: Boolean,
    isDolbyB: Boolean,
    isLoopEnabled: Boolean,
    onTogglePlay: () -> Unit,
    onStop: () -> Unit,
    onRewind: () -> Unit,
    onFastForward: () -> Unit,
    onToggleRecord: () -> Unit,
    onSeek: (Float) -> Unit,
    onToggleFavorite: () -> Unit,
    onSetLoopA: () -> Unit,
    onSetLoopB: () -> Unit,
    onToggleLoop: () -> Unit,
    onOutputLevelChange: (Float) -> Unit,
    onBassBoostToggle: (Boolean) -> Unit,
    onDolbyBToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "deck_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    fun formatMs(ms: Long): String {
        val totalSec = (ms / 1000).coerceAtLeast(0)
        val m = totalSec / 60
        val s = totalSec % 60
        return "%02d:%02d".format(m, s)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. TOP STATUS STRIP
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ChassisSurfaceLow)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(NeonPinkHot.copy(alpha = pulseAlpha))
                )
                Text(
                    text = "TAPE BAY 01 // TRANSPORT ENGAGED",
                    color = NeonPink,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "BIAS: HIGH",
                    color = SilkscreenMuted,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "EQ: 70µs",
                    color = ValveAmber,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 2. MAIN SKEUOMORPHIC CASSETTE DECK UNIT
        CassetteDeckView(
            isPlaying = isPlaying,
            trackLabel = currentTrack.title.removeSuffix(".flac").removeSuffix(".wav").removeSuffix(".mp3")
        )

        // 3. DUAL ANALOG VU METERS
        VuMetersView(
            leftDb = vuLeftDb,
            rightDb = vuRightDb,
            isPeakClip = isPeakClip
        )

        // 4. TRACK METADATA & TELEMETRY CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ChassisSurfaceContainer)
                .border(1.dp, ChassisSurfaceHighest, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PROGRAM 04 // ACTIVE TRACK",
                            color = ElectricViolet,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = currentTrack.title,
                            color = SilkscreenWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = currentTrack.artist,
                                color = NeonPink,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(text = "•", color = SilkscreenDim, fontSize = 10.sp)
                            Text(
                                text = currentTrack.album,
                                color = SilkscreenMuted,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }

                    // Heart Favorite Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ChassisSurfaceHigh)
                            .clickable { onToggleFavorite() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (currentTrack.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (currentTrack.isFavorite) NeonPinkHot else SilkscreenDim,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Tech Badges Strip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(ChassisSurfaceDim, RoundedCornerShape(3.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(NeonPinkHot))
                            Text(
                                text = "${currentTrack.codec} ${currentTrack.spec}",
                                color = NeonPink,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(ChassisSurfaceDim, RoundedCornerShape(3.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = currentTrack.bitrate,
                            color = SilkscreenMuted,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(ChassisSurfaceDim, RoundedCornerShape(3.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "SIZE: ${currentTrack.fileSizeStr}",
                            color = ValveAmber,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(ChassisSurfaceDim, RoundedCornerShape(3.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "CRC32: ${currentTrack.crc32}",
                            color = ElectricViolet,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // 5. CONVEYOR TAPE PROGRESS & VFD COUNTER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ChassisSurfaceLow)
                .border(1.dp, ChassisSurfaceHighest, RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Counter & Loop buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // VFD Counter
                    Row(
                        modifier = Modifier
                            .background(ChassisSurfaceDim, RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AvTimer,
                            contentDescription = "Timer",
                            tint = NeonPink,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = formatMs(currentPosMs),
                            color = NeonPink,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(text = "/", color = SilkscreenDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Text(
                            text = formatMs(totalDurMs),
                            color = SilkscreenMuted,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Loop Marker buttons
                    Row(
                        modifier = Modifier
                            .background(ChassisSurfaceDim, RoundedCornerShape(6.dp))
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(NeonPinkHot)
                                .clickable { onSetLoopA() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "SET A",
                                color = SilkscreenWhite,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(ChassisSurfaceHigh)
                                .clickable { onSetLoopB() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "SET B",
                                color = SilkscreenMuted,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isLoopEnabled) NeonPinkHot else ChassisSurfaceHigh)
                                .clickable { onToggleLoop() }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AllInclusive,
                                contentDescription = "Loop",
                                tint = if (isLoopEnabled) SilkscreenWhite else NeonPink,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // Conveyor Track Progress Strip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(ChassisSurfaceDim)
                        .border(1.dp, ChassisSurfaceHighest, RoundedCornerShape(3.dp))
                        .pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                val frac = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                                onSeek(frac)
                            }
                        }
                        .clickable { onSeek(progress) }
                ) {
                    // Elapsed Pink Phosphor Ribbon
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(18.dp)
                            .background(NeonPinkHot)
                    ) {
                        // Magnetic playhead marker
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .width(6.dp)
                                .height(18.dp)
                                .background(Color.White)
                        )
                    }
                }

                // Subtext Labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "START // 00:00", color = SilkscreenDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Text(
                        text = "REEL CONSUMPTION: ${(progress * 100).toInt()}%",
                        color = NeonPink,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "LEADER // ${formatMs(totalDurMs)}", color = SilkscreenDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }

        // 6. SKEUOMORPHIC MECHANICAL BUTTON BANK
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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "MECHANICAL TRANSPORT CHASSIS",
                        color = SilkscreenDim,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "SOLENOID: ARMED",
                        color = NeonPink,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                // 6 Tactile Stomp Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // REW
                    DeckStompButton(
                        icon = Icons.Default.FastRewind,
                        label = "REW",
                        isActive = false,
                        onClick = onRewind,
                        modifier = Modifier.weight(1f)
                    )
                    // FFWD
                    DeckStompButton(
                        icon = Icons.Default.FastForward,
                        label = "FFWD",
                        isActive = false,
                        onClick = onFastForward,
                        modifier = Modifier.weight(1f)
                    )
                    // STOP
                    DeckStompButton(
                        icon = Icons.Default.Stop,
                        label = "STOP",
                        isActive = false,
                        onClick = onStop,
                        modifier = Modifier.weight(1f)
                    )
                    // PAUSE
                    DeckStompButton(
                        icon = Icons.Default.Pause,
                        label = "PAUSE",
                        isActive = !isPlaying,
                        onClick = onTogglePlay,
                        modifier = Modifier.weight(1f)
                    )
                    // PLAY (Primary Engaged)
                    DeckStompButton(
                        icon = Icons.Default.PlayArrow,
                        label = "PLAY",
                        isActive = isPlaying,
                        isPrimary = true,
                        onClick = onTogglePlay,
                        modifier = Modifier.weight(1f)
                    )
                    // REC
                    DeckStompButton(
                        icon = Icons.Default.FiberManualRecord,
                        label = "REC",
                        isActive = isRecording,
                        isRecord = true,
                        onClick = onToggleRecord,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 7. HARDWARE CONSOLE: POTENTIOMETERS & ROCKER TOGGLES
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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ANALOG DISCRETE STAGE",
                        color = NeonPink,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "OP-AMP: BURR-BROWN OPA2134",
                        color = SilkscreenMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Left: Rotary Output Level Potentiometer
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ChassisSurfaceDim)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "OUTPUT LEVEL",
                                color = SilkscreenMuted,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )

                            // Dial Knob Canvas
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .pointerInput(Unit) {
                                        detectDragGestures { change, dragAmount ->
                                            val delta = -dragAmount.y * 0.2f
                                            onOutputLevelChange(outputLevelDb + delta)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val center = Offset(size.width / 2, size.height / 2)
                                    val radius = size.width / 2 - 4.dp.toPx()

                                    // Outer ring
                                    drawCircle(
                                        color = ChassisSurfaceHighest,
                                        radius = radius,
                                        center = center,
                                        style = Stroke(width = 3.dp.toPx())
                                    )

                                    // Active Pink Arc (fraction based on -24 to +12)
                                    val frac = ((outputLevelDb + 24f) / 36f).coerceIn(0.05f, 0.95f)
                                    drawArc(
                                        color = NeonPinkHot,
                                        startAngle = 135f,
                                        sweepAngle = 270f * frac,
                                        useCenter = false,
                                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                                    )

                                    // Inner knob core
                                    drawCircle(
                                        color = ChassisSurfaceHigh,
                                        radius = radius - 7.dp.toPx(),
                                        center = center
                                    )

                                    // Indicator needle notch
                                    val currentAngleDeg = 135f + 270f * frac
                                    val rad = Math.toRadians(currentAngleDeg.toDouble())
                                    val needleLen = radius - 10.dp.toPx()
                                    val endX = center.x + (needleLen * cos(rad)).toFloat()
                                    val endY = center.y + (needleLen * sin(rad)).toFloat()
                                    drawLine(
                                        color = NeonPink,
                                        start = center,
                                        end = Offset(endX, endY),
                                        strokeWidth = 3.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "-INF", color = SilkscreenDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                Text(
                                    text = "${if (outputLevelDb > 0) "+" else ""}${String.format("%.1f", outputLevelDb)} dB",
                                    color = NeonPink,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "+12", color = SilkscreenDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }

                    // Right: Bass Boost & Dolby B Switches
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ChassisSurfaceDim)
                            .padding(8.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Bass Boost
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "BASS BOOST",
                                        color = SilkscreenWhite,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "+6dB @ 60Hz",
                                        color = SilkscreenDim,
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .width(44.dp)
                                        .height(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isBassBoost) NeonPinkHot else ChassisSurfaceHigh)
                                        .clickable { onBassBoostToggle(!isBassBoost) }
                                        .padding(2.dp),
                                    contentAlignment = if (isBassBoost) Alignment.CenterEnd else Alignment.CenterStart
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(ChassisSurfaceDim)
                                    )
                                }
                            }

                            // Dolby B
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "DOLBY B // NR",
                                        color = SilkscreenWhite,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "TAPE WARMTH",
                                        color = SilkscreenDim,
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .width(44.dp)
                                        .height(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isDolbyB) NeonPinkHot else ChassisSurfaceHigh)
                                        .clickable { onDolbyBToggle(!isDolbyB) }
                                        .padding(2.dp),
                                    contentAlignment = if (isDolbyB) Alignment.CenterEnd else Alignment.CenterStart
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(ChassisSurfaceDim)
                                    )
                                }
                            }

                            // Bias Indicator Strip
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(ChassisSurfaceLow, RoundedCornerShape(2.dp))
                                    .padding(horizontal = 6.dp, vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "BIAS CAL",
                                    color = SilkscreenMuted,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "+1.2 dB HIGH",
                                    color = ValveAmber,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 8. DECK FOOTER SILKSCREEN SYSTEM INFO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "CIRCUIT REF: NT-884-REV3", color = SilkscreenDim, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
            Text(text = "HEAD GAP: 1.2µm AMORPHOUS", color = SilkscreenDim, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
            Text(text = "SYS: OK", color = SilkscreenDim, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
        }

        Spacer(modifier = Modifier.height(72.dp))
    }
}

@Composable
fun DeckStompButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    isPrimary: Boolean = false,
    isRecord: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isPrimary && isActive -> NeonPinkHot
        isPrimary -> NeonPinkHot.copy(alpha = 0.85f)
        isActive -> ChassisSurfaceHighest
        else -> ChassisSurfaceHigh
    }
    val contentColor = when {
        isPrimary -> SilkscreenWhite
        isRecord -> ClipRed
        else -> SilkscreenWhite
    }

    Box(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(if (isPrimary) 22.dp else 18.dp)
            )
            Text(
                text = label,
                color = contentColor,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
