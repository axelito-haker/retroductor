package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DspPreset
import com.example.ui.theme.ChassisSurfaceBright
import com.example.ui.theme.ChassisSurfaceContainer
import com.example.ui.theme.ChassisSurfaceDim
import com.example.ui.theme.ChassisSurfaceHigh
import com.example.ui.theme.ChassisSurfaceHighest
import com.example.ui.theme.ChassisSurfaceLow
import com.example.ui.theme.ClipRed
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkHot
import com.example.ui.theme.PhosphorGreen
import com.example.ui.theme.SilkscreenBorder
import com.example.ui.theme.SilkscreenDim
import com.example.ui.theme.SilkscreenMuted
import com.example.ui.theme.SilkscreenWhite
import com.example.ui.theme.ValveAmber
import com.example.ui.theme.ValveAmberBright

@Composable
fun EqualizerDspScreen(
    presets: List<DspPreset>,
    selectedPresetName: String,
    eqBandsDb: List<Float>,
    isDspEnabled: Boolean,
    fftBars: List<Float>,
    tubeDrive: Float,
    stereoWidth: Float,
    isSubsonicFilter: Boolean,
    onToggleDsp: (Boolean) -> Unit,
    onSelectPreset: (String) -> Unit,
    onBandChange: (Int, Float) -> Unit,
    onTubeDriveChange: (Float) -> Unit,
    onStereoWidthChange: (Float) -> Unit,
    onSubsonicFilterToggle: (Boolean) -> Unit,
    onResetEq: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val bandFreqs = listOf("32", "64", "125", "250", "500", "1k", "2k", "4k", "8k", "16k")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. DSP ENGINE POWER STRIP
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ChassisSurfaceDim)
                .border(1.dp, ChassisSurfaceHighest, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "DSP",
                        tint = if (isDspEnabled) NeonPink else SilkscreenDim,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = "DSP-CORE 04 // 10-BAND PARAMETRIC EQ",
                            color = if (isDspEnabled) NeonPink else SilkscreenDim,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (isDspEnabled) "32-BIT FLOATING POINT ENGINE" else "DSP BYPASSED",
                            color = SilkscreenMuted,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isDspEnabled) "ENGAGED" else "BYPASS",
                        color = if (isDspEnabled) NeonPinkHot else SilkscreenDim,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Switch(
                        checked = isDspEnabled,
                        onCheckedChange = onToggleDsp,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SilkscreenWhite,
                            checkedTrackColor = NeonPinkHot,
                            uncheckedThumbColor = SilkscreenDim,
                            uncheckedTrackColor = ChassisSurfaceHigh
                        )
                    )
                }
            }
        }

        // 2. PRESETS HORIZONTAL SELECTOR
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "CURVA DE EQUALIZACIÓN // PRESETS",
                color = SilkscreenDim,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(presets) { preset ->
                    val isSelected = preset.name == selectedPresetName
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) NeonPinkHot else ChassisSurfaceLow)
                            .border(1.dp, if (isSelected) NeonPink else ChassisSurfaceHighest, RoundedCornerShape(6.dp))
                            .clickable { onSelectPreset(preset.name) }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = preset.name.uppercase(),
                            color = if (isSelected) SilkscreenWhite else NeonPink,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 3. CRT PHOSPHOR REAL-TIME FFT SPECTRUM ANALYZER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ChassisSurfaceDim)
                .border(1.5.dp, ChassisSurfaceHighest, RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
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
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(PhosphorGreen))
                        Text(
                            text = "CRT SPECTRUM ANALYZER // 16-CH FFT",
                            color = PhosphorGreen,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "PEAK RMS: -4.2 dB",
                        color = ValveAmber,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // CRT Screen Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF070B09))
                        .border(1.dp, Color(0xFF14241B), RoundedCornerShape(6.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Scanlines & CRT Grid
                        val gridCols = 8
                        val gridRows = 4
                        for (i in 1..gridCols) {
                            val x = size.width * (i.toFloat() / gridCols)
                            drawLine(
                                color = Color(0xFF14241B).copy(alpha = 0.5f),
                                start = Offset(x, 0f),
                                end = Offset(x, size.height),
                                strokeWidth = 1f
                            )
                        }
                        for (j in 1..gridRows) {
                            val y = size.height * (j.toFloat() / gridRows)
                            drawLine(
                                color = Color(0xFF14241B).copy(alpha = 0.5f),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1f
                            )
                        }

                        // FFT Bars
                        val barCount = fftBars.size
                        val totalGap = (barCount - 1) * 3.dp.toPx()
                        val barWidth = (size.width - totalGap) / barCount

                        for (k in 0 until barCount) {
                            val barFrac = fftBars[k].coerceIn(0.05f, 1f)
                            val barH = size.height * barFrac
                            val x = k * (barWidth + 3.dp.toPx())
                            val y = size.height - barH

                            // Gradient from Green to Neon Pink at peak
                            drawRoundRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(NeonPinkHot, PhosphorGreen),
                                    startY = y,
                                    endY = size.height
                                ),
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barH),
                                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("20Hz", color = SilkscreenDim, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    Text("250Hz", color = SilkscreenDim, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    Text("1kHz", color = SilkscreenDim, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    Text("4kHz", color = SilkscreenDim, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    Text("20kHz", color = SilkscreenDim, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }

        // 4. 10-BAND GRAPHIC EQUALIZER FADER RACK
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
                    Text(
                        text = "10-BAND TACTILE SLIDER ARRAY (-12dB .. +12dB)",
                        color = SilkscreenWhite,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(ChassisSurfaceHighest)
                            .clickable { onResetEq() }
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = "Reset", tint = SilkscreenMuted, modifier = Modifier.size(12.dp))
                        Text("RESET", color = SilkscreenMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    }
                }

                // 10 Faders Grid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 0 until 10) {
                        val db = eqBandsDb.getOrElse(i) { 0f }
                        val freq = bandFreqs[i]

                        VerticalFader(
                            gainDb = db,
                            freqLabel = freq,
                            onGainChange = { newDb -> onBandChange(i, newDb) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 5. MASTER DISCRETE TUBE DRIVE & STEREO SPATIAL EXPANDER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ChassisSurfaceLow)
                .border(1.dp, ChassisSurfaceHighest, RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ANALOG SATURATION & SPATIAL MATRIX",
                        color = ValveAmber,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "TRIODE 12AX7 STAGE",
                        color = SilkscreenDim,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Tube Drive Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Whatshot, contentDescription = null, tint = ValveAmberBright, modifier = Modifier.size(15.dp))
                            Text("TUBE DRIVE (VALVE SATURATION)", color = SilkscreenWhite, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                        Text("${(tubeDrive * 100).toInt()}%", color = ValveAmber, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }

                    Slider(
                        value = tubeDrive,
                        onValueChange = onTubeDriveChange,
                        colors = SliderDefaults.colors(
                            thumbColor = ValveAmberBright,
                            activeTrackColor = ValveAmber,
                            inactiveTrackColor = ChassisSurfaceContainer
                        )
                    )
                }

                // Stereo Width Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("STEREO SPATIAL EXPANDER", color = SilkscreenWhite, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        Text("${(stereoWidth * 100).toInt()}%", color = NeonPinkHot, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }

                    Slider(
                        value = stereoWidth,
                        onValueChange = onStereoWidthChange,
                        valueRange = 0.5f..2.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonPinkHot,
                            activeTrackColor = NeonPink,
                            inactiveTrackColor = ChassisSurfaceContainer
                        )
                    )
                }

                // Filter toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSubsonicFilter) ChassisSurfaceHighest else ChassisSurfaceDim)
                            .border(1.dp, if (isSubsonicFilter) NeonPink else ChassisSurfaceHighest, RoundedCornerShape(6.dp))
                            .clickable { onSubsonicFilterToggle(!isSubsonicFilter) }
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("SUBSONIC FILTER", color = if (isSubsonicFilter) NeonPink else SilkscreenDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            Text("18Hz 24dB/oct CUT", color = SilkscreenMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(ChassisSurfaceDim)
                            .border(1.dp, ChassisSurfaceHighest, RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("HIGH-AIR SHELF", color = ElectricViolet, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            Text("+2.0dB @ 22kHz", color = SilkscreenMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(72.dp))
    }
}

@Composable
fun VerticalFader(
    gainDb: Float,
    freqLabel: String,
    onGainChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Gain Readout
        Text(
            text = "${if (gainDb > 0) "+" else ""}${gainDb.toInt()}",
            color = if (gainDb != 0f) NeonPink else SilkscreenDim,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (gainDb != 0f) FontWeight.Bold else FontWeight.Normal
        )

        // Fader Track Canvas
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(130.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val trackHeight = size.height
                        val y = change.position.y.coerceIn(0f, trackHeight.toFloat())
                        // y = 0 -> +12dB, y = trackHeight -> -12dB
                        val frac = 1f - (y / trackHeight.toFloat())
                        val newDb = -12f + frac * 24f
                        onGainChange(newDb.coerceIn(-12f, 12f))
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2
                // Center Line
                drawLine(
                    color = ChassisSurfaceHighest,
                    start = Offset(cx, 4.dp.toPx()),
                    end = Offset(cx, size.height - 4.dp.toPx()),
                    strokeWidth = 3.dp.toPx()
                )

                // 0dB Center Notch
                val zeroY = size.height / 2
                drawLine(
                    color = ValveAmber.copy(alpha = 0.7f),
                    start = Offset(cx - 8.dp.toPx(), zeroY),
                    end = Offset(cx + 8.dp.toPx(), zeroY),
                    strokeWidth = 1.5.dp.toPx()
                )

                // Thumb Position: -12..+12 mapped to bottom..top
                val frac = ((gainDb + 12f) / 24f).coerceIn(0f, 1f)
                val thumbY = (1f - frac) * (size.height - 18.dp.toPx()) + 9.dp.toPx()

                // Slider Thumb
                drawRoundRect(
                    color = NeonPinkHot,
                    topLeft = Offset(cx - 10.dp.toPx(), thumbY - 6.dp.toPx()),
                    size = Size(20.dp.toPx(), 12.dp.toPx()),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
                // Center stripe on thumb
                drawLine(
                    color = Color.White,
                    start = Offset(cx - 8.dp.toPx(), thumbY),
                    end = Offset(cx + 8.dp.toPx(), thumbY),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
        }

        // Freq Label
        Text(
            text = freqLabel,
            color = SilkscreenWhite,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}
