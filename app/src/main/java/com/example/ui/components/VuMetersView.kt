package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChassisSurfaceDim
import com.example.ui.theme.ChassisSurfaceHighest
import com.example.ui.theme.ChassisSurfaceLow
import com.example.ui.theme.ClipRed
import com.example.ui.theme.ClipRedContainer
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkHot
import com.example.ui.theme.SilkscreenDim
import com.example.ui.theme.SilkscreenMuted
import com.example.ui.theme.ValveAmber
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VuMetersView(
    leftDb: Float,
    rightDb: Float,
    isPeakClip: Boolean,
    modifier: Modifier = Modifier
) {
    // Map -20dB..+3dB to angle (-35 deg .. +35 deg)
    fun dbToAngle(db: Float): Float {
        val clamped = db.coerceIn(-20f, 3f)
        val fraction = (clamped + 20f) / 23f
        return -35f + fraction * 70f
    }

    val animatedAngleL by animateFloatAsState(
        targetValue = dbToAngle(leftDb),
        animationSpec = tween(durationMillis = 80),
        label = "angleL"
    )
    val animatedAngleR by animateFloatAsState(
        targetValue = dbToAngle(rightDb),
        animationSpec = tween(durationMillis = 80),
        label = "angleR"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ChassisSurfaceLow)
            .border(1.dp, ChassisSurfaceHighest, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "DUAL VU MONITOR // HIGH ACCURACY",
                    color = NeonPink,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "PEAK HOLD",
                        color = SilkscreenMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(if (isPeakClip) ClipRed else ClipRedContainer)
                    )
                }
            }

            // Dual Meters Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VuChannelBox(
                    channelName = "CH-L",
                    dbValue = leftDb,
                    needleAngle = animatedAngleL,
                    modifier = Modifier.weight(1f)
                )
                VuChannelBox(
                    channelName = "CH-R",
                    dbValue = rightDb,
                    needleAngle = animatedAngleR,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun VuChannelBox(
    channelName: String,
    dbValue: Float,
    needleAngle: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(84.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(ChassisSurfaceDim)
            .border(1.dp, ChassisSurfaceHighest, RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 5.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Scale Tick Marks
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "-20", color = SilkscreenDim, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                Text(text = "-10", color = SilkscreenDim, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                Text(text = "-5", color = SilkscreenDim, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                Text(text = "0", color = ValveAmber, fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Text(text = "+3", color = ClipRed, fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            }

            // Dial Needle Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val pivot = Offset(size.width / 2, size.height + 4.dp.toPx())
                    val needleLen = size.height * 1.15f
                    // angle: 0 is straight up (-90 deg from horizontal)
                    val rad = Math.toRadians((needleAngle - 90.0).toDouble())
                    val endX = (pivot.x + needleLen * cos(rad)).toFloat()
                    val endY = (pivot.y + needleLen * sin(rad)).toFloat()

                    // Glow line
                    drawLine(
                        color = NeonPinkHot.copy(alpha = 0.3f),
                        start = pivot,
                        end = Offset(endX, endY),
                        strokeWidth = 3.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    // Core needle
                    drawLine(
                        color = NeonPink,
                        start = pivot,
                        end = Offset(endX, endY),
                        strokeWidth = 1.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    // Pivot base hub
                    drawCircle(
                        color = ChassisSurfaceHighest,
                        radius = 6.dp.toPx(),
                        center = pivot
                    )
                }
            }

            // Channel Readout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = channelName,
                    color = NeonPink,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${if (dbValue > 0) "+" else ""}${String.format("%.1f", dbValue)} dB",
                    color = SilkscreenMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
