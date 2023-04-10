package com.example.vref_solutions_tablet_application.components.buttons

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.toUpperCase
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*
import com.example.vref_solutions_tablet_application.viewModels.NewTrainingViewModel

@Composable
fun NewTrainingActionButtons(viewModel: NewTrainingViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row() {
            Spacer(Modifier.weight(2f))

            RegularRectangleButton(buttonText = "${stringResource(R.string.add).toUpperCase()} ${stringResource(R.string.student).toUpperCase()}", onClick = { viewModel.togglePopUpScreen() }, modifier = Modifier.weight(3f),
                fontSize = MaterialTheme.typography.h5.fontSize, invertedColors = false)

            Spacer(Modifier.weight(2f))
        }

        Spacer(Modifier.padding(MaterialTheme.padding.mini))

        Row() {
            Spacer(Modifier.weight(2f))

            RegularRectangleButton(buttonText = stringResource(R.string.cancel), onClick = { viewModel.navigateToPage(
                ScreenNavName.MainMenu) }, modifier = Modifier.weight(3f),
                fontSize = MaterialTheme.typography.h5.fontSize, invertedColors = true)

            Spacer(Modifier.weight(2f))
        }
    }
}