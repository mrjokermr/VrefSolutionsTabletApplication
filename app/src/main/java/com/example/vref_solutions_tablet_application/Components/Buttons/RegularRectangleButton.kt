package com.example.vref_solutions_tablet_application.components.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun RegularRectangleButton(buttonText: String,onClick: ()->Unit, modifier: Modifier, fontSize: TextUnit , invertedColors: Boolean) {
    Button(
        contentPadding = PaddingValues(1.dp),
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = if(invertedColors) Color.White else MaterialTheme.colors.primary),) {
        Text(
            text = buttonText,
            color = if(invertedColors) MaterialTheme.colors.primary else Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize
        )
    }
}