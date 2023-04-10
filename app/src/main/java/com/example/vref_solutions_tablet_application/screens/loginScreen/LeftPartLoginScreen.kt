package com.example.vref_solutions_tablet_application.screens.loginScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.components.DynamicLoadingDisplay
import com.example.vref_solutions_tablet_application.components.textField.RegularTextFieldWithLabel
import com.example.vref_solutions_tablet_application.components.textUI.PageTitle
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*
import com.example.vref_solutions_tablet_application.viewModels.LoginScreenViewModel
import com.example.vref_solutions_tablet_application.ui.theme.NegativeActionColor

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LeftPartLoginScreen(viewModel: LoginScreenViewModel, navController: NavController) {
    Column() {
        PageTitle(titleText = stringResource(R.string.login), fontSize = MaterialTheme.typography.h1.fontSize)

        Spacer(Modifier.padding(MaterialTheme.padding.large))

        Column {

            val loginUsernameText = viewModel.inputUsername.collectAsState()

            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
                .fillMaxWidth()
                .padding(start = MaterialTheme.padding.extreme, end = MaterialTheme.padding.extreme)) {

                RegularTextFieldWithLabel(labelText = stringResource(R.string.email), modifier = Modifier.fillMaxWidth().shadow(elevation = 12.dp), value = loginUsernameText,
                    onValueChangeFun = { viewModel.setInputUsername(it) },isPasswordDisplay = false, enabled = true)

            }
        }

        Spacer(Modifier.padding(MaterialTheme.padding.small))

        Column {

            val loginPasswordText = viewModel.inputPassword.collectAsState()
            val loginNegativeFeedbackText = viewModel.negativeFeedbackText.collectAsState()

            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
                .fillMaxWidth()
                .padding(start = MaterialTheme.padding.extreme, end = MaterialTheme.padding.extreme)) {

                RegularTextFieldWithLabel(labelText = stringResource(R.string.password), modifier = Modifier.fillMaxWidth().shadow(elevation = 12.dp), value = loginPasswordText,
                    onValueChangeFun = { viewModel.setInputPassword(it) },isPasswordDisplay = true, enabled = true)

            }

            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaterialTheme.padding.small), textAlign = TextAlign.Center , text = loginNegativeFeedbackText.value, color = NegativeActionColor)


        }

        Spacer(Modifier.padding(MaterialTheme.padding.medium))

        Row(modifier = Modifier.padding(start = MaterialTheme.padding.extreme, end = MaterialTheme.padding.extreme)) {

            // RegularRectangleButton() not working for this button, because the height would not properly get set like it should do
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primary),
                onClick = {
                    viewModel.launchLoginAttempt(navController)
                }) {
                Text(text = stringResource(R.string.login), color = Color.White, fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.h4.fontSize)
            }
        }
        val loginAttemptIsActive = viewModel.uiLoginAttemptIsActive.collectAsState()

        if(loginAttemptIsActive.value) {
            Spacer(Modifier.padding(MaterialTheme.padding.tiny))

            DynamicLoadingDisplay(loadingText = "", iconSize = MaterialTheme.iconSize.medium, textColor = Color.White, iconTint = Color.White)
        }

        Spacer(Modifier.padding(MaterialTheme.padding.small))

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {

            Text(text = stringResource(R.string.to_activate_account), textDecoration = TextDecoration.Underline, color = MaterialTheme.colors.primary, fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.h5.fontSize,
                modifier = Modifier.clickable {
                    //open activate account pop up
                    viewModel.toggleActivationAccountPopUp()
                }
            )

        }

    }
}