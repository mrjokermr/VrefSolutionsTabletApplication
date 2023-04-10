package com.example.vref_solutions_tablet_application.components.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*


import com.example.vref_solutions_tablet_application.viewModels.NewTrainingViewModel

@Composable
fun StartTrainingButton(viewModel: NewTrainingViewModel) {
    FloatingActionButton(
        backgroundColor = MaterialTheme.colors.primary,
        onClick = {
            viewModel.launchStartNewTraining()
        }) {
        Row(modifier = Modifier.padding(top = MaterialTheme.padding.tiny, bottom = MaterialTheme.padding.tiny ,
            start = MaterialTheme.padding.small, end = MaterialTheme.padding.small),
            horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "${stringResource(R.string.start)} ${stringResource(R.string.training)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.h5.fontSize)
            Image(
                modifier = Modifier.size(MaterialTheme.iconSize.medium),
                painter = painterResource(id = R.drawable.plus),
                contentDescription = stringResource(R.string.cd_plus_icon)
            )
        }
    }
}