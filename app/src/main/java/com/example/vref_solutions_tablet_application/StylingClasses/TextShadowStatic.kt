package com.example.vref_solutions_tablet_application.StylingClasses

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle

class TextShadowStatic {
    companion object {

        @Composable
        fun Medium(): TextStyle {
            return MaterialTheme.typography.h4.copy(
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(3f, 3f),
                    blurRadius = 6f
                )
            )
        }

        @Composable
        fun Small(): TextStyle {
            return MaterialTheme.typography.h4.copy(
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(2f, 2f),
                    blurRadius = 1.5f
                )
            )
        }
    }
}