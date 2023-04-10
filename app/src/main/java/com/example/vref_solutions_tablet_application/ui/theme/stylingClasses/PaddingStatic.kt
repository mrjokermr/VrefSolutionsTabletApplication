package com.example.vref_solutions_tablet_application.ui.theme.stylingClasses

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Padding (
    val extreme: Dp = 80.dp,
    val extraLarge: Dp = 64.dp,
    val large: Dp = 32.dp,
    val medium: Dp = 22.dp,
    val small: Dp = 12.dp,
    val tiny: Dp = 8.dp,
    val mini: Dp = 4.dp,
)

val LocalPadding = compositionLocalOf { Padding() }

val MaterialTheme.padding: Padding
    @Composable
    @ReadOnlyComposable
    get() = LocalPadding.current