package com.example.vref_solutions_tablet_application.ui.theme.stylingClasses

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class IconSize (
    val extraLarge: Dp = 60.dp,
    val large: Dp = 40.dp,
    val medium: Dp = 30.dp,
    val small: Dp = 20.dp,
    val tiny: Dp = 14.dp,

    val menuCameraIco: Dp = 36.dp,
    val menuExitIco: Dp = 26.dp,
    val menuBurgerIco: Dp = 38.dp,
    val menuPlusIco: Dp = 34.dp,
)

val LocalIconSize = compositionLocalOf { IconSize() }

val MaterialTheme.iconSize: IconSize
    @Composable
    @ReadOnlyComposable
    get() = LocalIconSize.current