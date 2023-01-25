package com.example.vref_solutions_tablet_application.Components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.StylingClasses.IconSizeStatic

@Composable
fun DynamicLoadingDisplay(loadingText: String, iconSize: Dp, textColor: Color , iconTint: Color) {
    val infiniteTransition = rememberInfiniteTransition()

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2000,easing = LinearEasing), RepeatMode.Restart)
    )

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.loading_dots_spinner),
            contentDescription = "loading_icon",
            colorFilter = ColorFilter.tint(iconTint),
            modifier = Modifier.size(iconSize).rotate(angle)
        )

        Text(text = loadingText, color = textColor, textAlign = TextAlign.Center)
    }


}