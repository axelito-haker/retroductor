package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChassisSurfaceBright
import com.example.ui.theme.ChassisSurfaceContainer
import com.example.ui.theme.ChassisSurfaceDim
import com.example.ui.theme.ChassisSurfaceHigh
import com.example.ui.theme.ChassisSurfaceHighest
import com.example.ui.theme.ChassisSurfaceLow
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkDeep
import com.example.ui.theme.NeonPinkHot
import com.example.ui.theme.SilkscreenBorder
import com.example.ui.theme.SilkscreenDim
import com.example.ui.theme.SilkscreenMuted
import com.example.ui.theme.ValveAmber

@Composable
fun CassetteDeckView(
    isPlaying: Boolean,
    trackLabel: String = "NEO-TOKYO SYNTH RUN",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "tape_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val effectiveRotation = if (isPlaying) rotation else 0f

    // Outer Skeuomorphic Unit
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ChassisSurfaceDim)
            .border(1.dp, ChassisSurfaceHigh, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ChassisSurfaceHigh)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: Type II & Track Label & Side A
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(ChassisSurfaceHighest)
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(NeonPinkHot, RoundedCornerShape(2.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "TYPE II",
                            color = NeonPinkDeep,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Text(
                        text = trackLabel.uppercase(),
                        color = NeonPink,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = "SIDE A // CRO2",
                    color = ValveAmber,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            // Acrylic Viewing Window & Tape Spool Hubs
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ChassisSurfaceDim)
                    .border(1.dp, ChassisSurfaceHighest, RoundedCornerShape(4.dp))
            ) {
                // Scanline drawing overlay
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val step = 4.dp.toPx()
                    var y = 0f
                    while (y < size.height) {
                        drawLine(
                            color = Color.Black.copy(alpha = 0.35f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                        y += step
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Spool Assembly (REEL-L)
                    TapeSpoolHub(
                        reelLabel = "REEL-L",
                        rotationDegrees = effectiveRotation,
                        tapeThickness = 68.dp
                    )

                    // Center Level Guide & Bridge
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(82.dp)
                                .height(32.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(ChassisSurfaceLow.copy(alpha = 0.85f))
                                .border(1.dp, ChassisSurfaceHighest, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(modifier = Modifier.size(3.dp, 8.dp).background(NeonPinkHot, CircleShape))
                                    Text(
                                        text = "TAPE",
                                        color = NeonPink,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 2.sp
                                    )
                                    Box(modifier = Modifier.size(3.dp, 8.dp).background(NeonPinkHot, CircleShape))
                                }
                                Box(
                                    modifier = Modifier
                                        .width(60.dp)
                                        .height(4.dp)
                                        .clip(CircleShape)
                                        .background(ChassisSurfaceHighest)
                                )
                            }
                        }

                        Text(
                            text = if (isPlaying) "RUNNING 4.76 cm/s" else "PAUSED 0.00 cm/s",
                            color = if (isPlaying) NeonPink else SilkscreenDim,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    // Right Spool Assembly (REEL-R)
                    TapeSpoolHub(
                        reelLabel = "REEL-R",
                        rotationDegrees = effectiveRotation,
                        tapeThickness = 56.dp
                    )
                }
            }

            // Bottom Plate: Screws & Japan Spec
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ChassisSurfaceDim))
                    Text(
                        text = "NR: DOLBY B",
                        color = SilkscreenDim,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Center Guide Slot
                Box(
                    modifier = Modifier
                        .width(56.dp)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(ChassisSurfaceDim)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "JAPAN SPEC",
                        color = SilkscreenDim,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ChassisSurfaceDim))
                }
            }
        }
    }
}

@Composable
fun TapeSpoolHub(
    reelLabel: String,
    rotationDegrees: Float,
    tapeThickness: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        // Outer Roll Mass
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(ChassisSurfaceHighest),
            contentAlignment = Alignment.Center
        ) {
            // Magnetic Tape Layer
            Box(
                modifier = Modifier
                    .size(tapeThickness)
                    .clip(CircleShape)
                    .background(ChassisSurfaceDim),
                contentAlignment = Alignment.Center
            ) {
                // Rotating Cog Body
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .rotate(rotationDegrees)
                        .clip(CircleShape)
                        .background(ChassisSurfaceBright),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(ChassisSurfaceDim),
                        contentAlignment = Alignment.Center
                    ) {
                        // Cog Teeth
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2, size.height / 2)
                            val toothRadius = 8.dp.toPx()
                            for (angle in 0 until 360 step 60) {
                                val rad = Math.toRadians(angle.toDouble())
                                val x = center.x + (toothRadius * Math.cos(rad)).toFloat()
                                val y = center.y + (toothRadius * Math.sin(rad)).toFloat()
                                drawCircle(
                                    color = NeonPinkHot,
                                    radius = 2.dp.toPx(),
                                    center = Offset(x, y)
                                )
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = reelLabel,
            color = SilkscreenMuted,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}
