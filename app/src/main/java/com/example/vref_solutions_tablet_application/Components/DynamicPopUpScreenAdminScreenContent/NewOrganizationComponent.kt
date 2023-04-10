package com.example.vref_solutions_tablet_application.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.components.textField.RegularTextFieldWithPlaceholder
import com.example.vref_solutions_tablet_application.components.textUI.SmallAditionalInfoText
import com.example.vref_solutions_tablet_application.handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.models.popUpModels.NewOrganization
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*
import com.example.vref_solutions_tablet_application.viewModels.AdminsViewModel

@Composable
fun NewOrganizationComponent(newOrganizationHandler: NewOrganization,viewModel: AdminsViewModel) {
    var loggedInUserHandler: LoggedInUserHandler = LoggedInUserHandler(currentContext = LocalContext.current)
    newOrganizationHandler.viewModel = viewModel
    newOrganizationHandler.authToken = loggedInUserHandler.getAuthKey()

    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.padding(MaterialTheme.padding.small))

        Column(modifier = Modifier.weight(1f)) {

            SmallAditionalInfoText(text = stringResource(R.string.organization_name), modifier = Modifier.fillMaxWidth(), textSize = MaterialTheme.typography.h6.fontSize, textAlign = TextAlign.Left,
                fontWeight = FontWeight.Normal)

            Spacer(Modifier.padding(MaterialTheme.padding.mini))

            var organizationNameState = newOrganizationHandler.inputNewOrganizationName.collectAsState()

            RegularTextFieldWithPlaceholder(placeholderText = stringResource(R.string.organization_name_here) + "...",modifier = Modifier.fillMaxWidth().shadow(elevation = 12.dp), value = organizationNameState,
                onValueChangeFun = { newOrganizationHandler.setOrganizationValue(it) }, isPasswordDisplay = false, enabled = true)

        }
    }
}