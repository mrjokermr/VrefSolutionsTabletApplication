package com.example.vref_solutions_tablet_application.screens.mainMenuScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*


@Composable
fun FeedbackDetail(targetTitle: String, content: String) {
    val density: Density = LocalDensity.current
    var textWidth by remember { mutableStateOf(0.dp) }

    Column(modifier = Modifier.fillMaxWidth()) {

        Text(text = targetTitle, modifier = Modifier.onGloballyPositioned { coordinates -> textWidth = with(density) { coordinates.size.width.dp - 32.dp} } , color = Color.White, fontSize = MaterialTheme.typography.h5.fontSize, fontWeight = FontWeight.Bold)
        Box(modifier = Modifier
            .width(textWidth)
            .height(2.dp)
            .background(MaterialTheme.colors.primary))

        Spacer(Modifier.padding(MaterialTheme.padding.mini))

        Text(text = content, modifier = Modifier.fillMaxWidth() , color = Color.White, fontSize = MaterialTheme.typography.h5.fontSize)

        Spacer(Modifier.padding(MaterialTheme.padding.small))
    }
}