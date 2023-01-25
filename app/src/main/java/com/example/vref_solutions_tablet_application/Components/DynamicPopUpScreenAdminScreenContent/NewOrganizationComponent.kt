package com.example.vref_solutions_tablet_application.Components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.Components.TextField.RegularTextFieldWithPlaceholder
import com.example.vref_solutions_tablet_application.Components.`Text-UI`.SmallAditionalInfoText
import com.example.vref_solutions_tablet_application.Handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.Models.PopUpModels.NewOrganization
import com.example.vref_solutions_tablet_application.StylingClasses.FontSizeStatic
import com.example.vref_solutions_tablet_application.StylingClasses.PaddingStatic
import com.example.vref_solutions_tablet_application.ViewModels.AdminsViewModel
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBoxDarkBackground

@Composable
fun NewOrganizationComponent(newOrganizationHandler: NewOrganization,viewModel: AdminsViewModel) {
    var loggedInUserHandler: LoggedInUserHandler = LoggedInUserHandler(currentContext = LocalContext.current)
    newOrganizationHandler.viewModel = viewModel
    newOrganizationHandler.authToken = loggedInUserHandler.GetAuthKey()

    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.padding(PaddingStatic.Small))

        Column(modifier = Modifier.weight(1f)) {

            SmallAditionalInfoText(text = "Organization name", modifier = Modifier.fillMaxWidth(), textSize = FontSizeStatic.Tiny, textAlign = TextAlign.Left,
                fontWeight = FontWeight.Normal)

            Spacer(Modifier.padding(PaddingStatic.Mini))

            var organizationNameState = newOrganizationHandler.inputNewOrganizationName.collectAsState()

            RegularTextFieldWithPlaceholder(placeholderText = "Organization name here...",modifier = Modifier.fillMaxWidth().shadow(elevation = 12.dp), value = organizationNameState,
                onValueChangeFun = { newOrganizationHandler.SetOrganizationValue(it) }, isPasswordDisplay = false, enabled = true)

        }
    }
}