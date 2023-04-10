package com.example.vref_solutions_tablet_application.ui.theme.stylingClasses

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class RoundedCornerShape (
    val large: Dp = 15.dp,
    val medium: Dp = 10.dp,
    val small: Dp = 5.dp,
)

val LocalRoundedCornerShape = compositionLocalOf { RoundedCornerShape() }

val MaterialTheme.roundedCornerShape: RoundedCornerShape
    @Composable
    @ReadOnlyComposable
    get() = LocalRoundedCornerShape.current