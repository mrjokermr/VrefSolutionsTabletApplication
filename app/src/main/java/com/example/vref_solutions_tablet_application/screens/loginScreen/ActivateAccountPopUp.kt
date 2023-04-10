package com.example.vref_solutions_tablet_application.screens.loginScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.components.buttons.RegularRectangleButton
import com.example.vref_solutions_tablet_application.components.textField.RegularTextField
import com.example.vref_solutions_tablet_application.components.textField.RegularTextFieldWithPlaceholder
import com.example.vref_solutions_tablet_application.components.textUI.SmallAditionalInfoText
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*
import com.example.vref_solutions_tablet_application.viewModels.LoginScreenViewModel
import com.example.vref_solutions_tablet_application.ui.theme.NegativeActionColor
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBackground
import com.example.vref_solutions_tablet_application.ui.theme.PositiveActionColor

@Composable
fun ActivateAccountPopUp(viewModel: LoginScreenViewModel) {
    Card(elevation = 12.dp, modifier = Modifier
        .width(450.dp)
        .height(442.dp)
        .clip(RoundedCornerShape(MaterialTheme.roundedCornerShape.small)),
        backgroundColor = PopUpBackground
    ) {

        Column(modifier = Modifier.padding(MaterialTheme.padding.small)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "${stringResource(R.string.activate)} ${stringResource(R.string.account)}",
                    color = Color(0xFFFFFFFF),
                    fontSize = MaterialTheme.typography.h4.fontSize
                )
                Image(
                    painter = painterResource(id = R.drawable.x_circle_fill),
                    contentDescription = stringResource(R.string.cd_cross_icon),
                    modifier = Modifier
                        .size(MaterialTheme.iconSize.medium)
                        .clickable {
                            viewModel.toggleActivationAccountPopUp()
                        }
                )
            }


            //Content:
            Row(modifier = Modifier
                .fillMaxWidth()
                .weight(7f)) {
                Spacer(Modifier.weight(2f))

                Column(modifier = Modifier
                    .weight(5f)
                    .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    Column(modifier = Modifier.weight(3f)) {
                        SmallAditionalInfoText(text = stringResource(R.string.activation_code), modifier = Modifier.fillMaxWidth(), textSize = MaterialTheme.typography.h6.fontSize, textAlign = TextAlign.Left,
                            fontWeight = FontWeight.Normal)

                        Spacer(Modifier.padding(MaterialTheme.padding.mini))

                        var activationCodeState = viewModel.inputActivationCode.collectAsState()

                        RegularTextFieldWithPlaceholder(placeholderText = "${stringResource(R.string.activation_code)} ${stringResource(R.string.here)}",modifier = Modifier.fillMaxWidth().shadow(elevation = 12.dp), value = activationCodeState,
                            onValueChangeFun = { viewModel.setInputActivationCode(it) }, isPasswordDisplay = false, enabled = true)
                    }

                    Spacer(Modifier.padding(MaterialTheme.padding.tiny))

                    Column(modifier = Modifier.weight(3f)) {

                        SmallAditionalInfoText(text = stringResource(R.string.your_new_password), modifier = Modifier.fillMaxWidth(), textSize = MaterialTheme.typography.h6.fontSize, textAlign = TextAlign.Left,
                            fontWeight = FontWeight.Normal)

                        Spacer(Modifier.padding(MaterialTheme.padding.mini))

                        val newPasswordFirstState = viewModel.inputFirstNewPassword.collectAsState()

                        RegularTextField(modifier = Modifier.fillMaxWidth().shadow(elevation = 12.dp), value = newPasswordFirstState,
                            onValueChangeFun = { viewModel.setFirstNewPassword(it) }, isPasswordDisplay = true, enabled = true)

                    }

                    Column(modifier = Modifier.weight(2f)) {
                        val uiPasswordContainsRightCharacters = viewModel.uiPasswordContainsRightCharacters.collectAsState()
                        val uiPasswordIsLongEnough = viewModel.uiPasswordIsLongEnough.collectAsState()

                        Column(modifier = Modifier.fillMaxWidth().height(50.dp)) {
                            Spacer(Modifier.padding(MaterialTheme.padding.mini))

                            Text(text = stringResource(R.string.password_contains_lowercase_uppercase_special), fontSize = MaterialTheme.typography.subtitle1.fontSize,
                                color = if(uiPasswordContainsRightCharacters.value) PositiveActionColor else NegativeActionColor
                            )

                            Text(text = stringResource(R.string.password_long_enough), fontSize = MaterialTheme.typography.subtitle1.fontSize,
                                color = if(uiPasswordIsLongEnough.value) PositiveActionColor else NegativeActionColor
                            )
                        }
                    }

                    Spacer(Modifier.padding(MaterialTheme.padding.mini))

                    Column(modifier = Modifier.weight(3f)) {
                        SmallAditionalInfoText(text = stringResource(R.string.confirm_password), modifier = Modifier.fillMaxWidth(), textSize = MaterialTheme.typography.h6.fontSize, textAlign = TextAlign.Left,
                            fontWeight = FontWeight.Normal)

                        Spacer(Modifier.padding(MaterialTheme.padding.mini))

                        var newPasswordSecondState = viewModel.inputSecondNewPassword.collectAsState()

                        RegularTextField(modifier = Modifier.fillMaxWidth().shadow(elevation = 12.dp), value = newPasswordSecondState,
                            onValueChangeFun = { viewModel.setSecondNewPassword(it) }, isPasswordDisplay = true, enabled = true)
                    }

                } // end content column wrapper

                Spacer(Modifier.weight(2f))
            } // end row wrapper

            Spacer(Modifier.padding(MaterialTheme.padding.small))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                RegularRectangleButton(buttonText = stringResource(R.string.cancel), onClick = { viewModel.toggleActivationAccountPopUp() }, modifier = Modifier.weight(1f),
                    fontSize = MaterialTheme.typography.h5.fontSize, invertedColors = true)

                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(2f)
                )

                RegularRectangleButton(buttonText = stringResource(R.string.confirm),
                    onClick = {
                        viewModel.launchActivateAccountAttempt()
                    }, modifier = Modifier.weight(1f), fontSize = MaterialTheme.typography.h5.fontSize, invertedColors = false)


            } // end row
        }
    }
}