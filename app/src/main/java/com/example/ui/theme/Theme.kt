package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CyberDeckColorScheme = darkColorScheme(
    primary = NeonPink,
    onPrimary = NeonPinkDeep,
    primaryContainer = NeonPinkHot,
    onPrimaryContainer = SilkscreenWhite,
    secondary = ElectricViolet,
    onSecondary = ChassisSurfaceDim,
    secondaryContainer = ElectricVioletDark,
    onSecondaryContainer = SilkscreenWhite,
    tertiary = ValveAmber,
    onTertiary = ChassisSurfaceDim,
    tertiaryContainer = ValveAmberDark,
    onTertiaryContainer = ValveAmberGlow,
    background = ChassisBackground,
    onBackground = SilkscreenWhite,
    surface = ChassisSurface,
    onSurface = SilkscreenWhite,
    surfaceVariant = ChassisSurfaceHighest,
    onSurfaceVariant = SilkscreenMuted,
    surfaceContainer = ChassisSurfaceContainer,
    surfaceContainerLow = ChassisSurfaceLow,
    surfaceContainerLowest = ChassisSurfaceDim,
    surfaceContainerHigh = ChassisSurfaceHigh,
    surfaceContainerHighest = ChassisSurfaceHighest,
    outline = SilkscreenDim,
    outlineVariant = SilkscreenBorder,
    error = ClipRed,
    onError = ChassisSurfaceDim,
    errorContainer = ClipRedContainer,
    onErrorContainer = SilkscreenWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Keep authentic custom cyberdeck aesthetic
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberDeckColorScheme,
        typography = Typography,
        content = content
    )
}
