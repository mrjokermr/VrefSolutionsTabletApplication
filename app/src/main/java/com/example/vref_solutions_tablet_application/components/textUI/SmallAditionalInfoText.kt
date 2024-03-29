package com.example.vref_solutions_tablet_application.components.textUI

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.TextShadowStatic

@Composable
fun SmallAditionalInfoText(text: String, modifier: Modifier, textSize: TextUnit, textAlign: TextAlign, fontWeight: FontWeight) {
    Text(
        modifier = modifier,
        textAlign = textAlign,
        text = text,
        fontSize = textSize,
        fontWeight = fontWeight,
        color = Color.Gray,
        style = TextShadowStatic.Small()
    )
}