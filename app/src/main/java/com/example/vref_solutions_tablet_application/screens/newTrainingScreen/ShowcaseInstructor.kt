package com.example.vref_solutions_tablet_application.screens.newTrainingScreen

import androidx.compose.foundation.Image
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
import com.example.vref_solutions_tablet_application.handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*


@Composable
fun ShowcaseInstructor(loggedInUserHandler: LoggedInUserHandler) {
    val fullName = loggedInUserHandler.getFullNameCurrentUser()
    val email = loggedInUserHandler.getEmailCurrentUser()

    Row(horizontalArrangement = Arrangement.Center) {
        Column(modifier = Modifier
            .weight(3f)
        ) {

            Text(text = fullName, fontSize = MaterialTheme.typography.h5.fontSize, color = Color.White)
            Text(text = email, fontSize = MaterialTheme.typography.h6.fontSize, color = Color.White)
        }

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Image(
                painter = painterResource(id = R.drawable.person_ico_outline_white),
                contentDescription = stringResource(R.string.cd_instructor_icon),
                modifier = Modifier.size(MaterialTheme.iconSize.medium)
            )

            Text(text = stringResource(R.string.instructor), fontSize = MaterialTheme.typography.subtitle1.fontSize, color = Color.White)
        }
    }
}