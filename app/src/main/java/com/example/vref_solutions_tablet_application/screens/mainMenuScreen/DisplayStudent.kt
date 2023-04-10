package com.example.vref_solutions_tablet_application.screens.mainMenuScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.components.DynamicLoadingDisplay
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*

@Composable
fun DisplayStudent(name: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.person_ico_blue),
            contentDescription = stringResource(R.string.cd_student_icon),
            modifier = Modifier.size(MaterialTheme.iconSize.small)
        )
        Spacer(Modifier.padding(2.dp))
        if(name == "") {
            DynamicLoadingDisplay(
                loadingText = stringResource(R.string.loading_student_name),
                iconSize = MaterialTheme.iconSize.small,
                textColor = MaterialTheme.colors.primaryVariant,
                iconTint = MaterialTheme.colors.primaryVariant
            )
        }
        else {
            Text(
                text = name,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.widthIn(134.dp, 134.dp),
            )
        }
    }
}