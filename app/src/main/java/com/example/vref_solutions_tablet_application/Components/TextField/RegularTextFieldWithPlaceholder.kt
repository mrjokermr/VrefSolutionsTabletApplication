package com.example.vref_solutions_tablet_application.components.textField

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBoxDarkBackground

@Composable
fun RegularTextFieldWithPlaceholder(placeholderText: String,modifier: Modifier, value: State<String>, onValueChangeFun: (String)->Unit, isPasswordDisplay: Boolean, enabled: Boolean) {


    TextField(
        modifier = modifier,
        singleLine = true,
        value = value.value,
        onValueChange = { onValueChangeFun(it) },
        visualTransformation = if(isPasswordDisplay) PasswordVisualTransformation() else VisualTransformation.None,
        placeholder = { Text(text = placeholderText) },
        enabled = enabled,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = PopUpBoxDarkBackground,
            textColor = Color.White
        )
    )

}