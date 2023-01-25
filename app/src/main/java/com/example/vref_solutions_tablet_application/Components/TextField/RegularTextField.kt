package com.example.vref_solutions_tablet_application.Components.TextField

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBoxDarkBackground

@Composable
fun RegularTextField(modifier: Modifier,value: State<String>, onValueChangeFun: (String)->Unit, isPasswordDisplay: Boolean, enabled: Boolean) {


    TextField(
        modifier = modifier,
        singleLine = true,
        value = value.value,
        onValueChange = { onValueChangeFun(it) },
        visualTransformation = if(isPasswordDisplay) PasswordVisualTransformation() else VisualTransformation.None,
        enabled = enabled,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = PopUpBoxDarkBackground,
            textColor = Color.White
        )
    )
}