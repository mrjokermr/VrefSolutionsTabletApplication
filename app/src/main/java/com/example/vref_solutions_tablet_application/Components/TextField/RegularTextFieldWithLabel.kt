package com.example.vref_solutions_tablet_application.components.textField

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun RegularTextFieldWithLabel(labelText: String,modifier: Modifier, value: State<String>, onValueChangeFun: (String)->Unit, isPasswordDisplay: Boolean, enabled: Boolean) {
    TextField(
        modifier = modifier,
        singleLine = true,
        value = value.value,
        onValueChange = { onValueChangeFun(it) },
        enabled = enabled,
        label = {
            Text(
                text = labelText,
                color = MaterialTheme.colors.primary
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.onBackground,
            textColor = Color.White
        ),
        visualTransformation = if(isPasswordDisplay) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if(isPasswordDisplay) KeyboardType.Password else KeyboardType.Text
        )
    )
}