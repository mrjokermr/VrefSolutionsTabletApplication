package com.example.vref_solutions_tablet_application.components.textUI

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.TextShadowStatic

@Composable
fun PageTitle(titleText: String, fontSize: TextUnit) {
    Text(text = titleText.uppercase(), color = MaterialTheme.colors.primary, fontSize = fontSize, fontWeight = FontWeight.Bold,
        style = TextShadowStatic.Medium(),
        textAlign = TextAlign.Center,modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth())
}