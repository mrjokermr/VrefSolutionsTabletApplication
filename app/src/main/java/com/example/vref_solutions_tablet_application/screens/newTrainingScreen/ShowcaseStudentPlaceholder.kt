package com.example.vref_solutions_tablet_application.screens.newTrainingScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*
import com.example.vref_solutions_tablet_application.viewModels.NewTrainingViewModel

@Composable
fun ShowcaseStudentPlaceholder(viewModel: NewTrainingViewModel) {
    Row(horizontalArrangement = Arrangement.Center) {

        Column(modifier = Modifier
            .weight(3f)
        ) {

            Text(text = "${stringResource(R.string.no_student_assigned)} \n${stringResource(R.string.this_slot)}", fontSize = MaterialTheme.typography.h6.fontSize, color = Color.Gray)
        }

        Column(modifier = Modifier
            .weight(1f)
            .clickable {
                viewModel.togglePopUpScreen()
            }, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Image(
                painter = painterResource(id = R.drawable.person_ico_add_outline),
                contentDescription = stringResource(R.string.cd_student_add_icon),
                modifier = Modifier.size(MaterialTheme.iconSize.medium)
            )

            Text(text = stringResource(R.string.student), fontSize = MaterialTheme.typography.subtitle1.fontSize, color = Color.White)
        }
    }
}