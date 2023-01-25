package com.example.vref_solutions_tablet_application.Screens

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.example.vref_solutions_tablet_application.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.vref_solutions_tablet_application.Components.Buttons.RegularRectangleButton
import com.example.vref_solutions_tablet_application.Components.DyanmicPopUpScreenLiveTraining
import com.example.vref_solutions_tablet_application.Components.DynamicLoadingDisplay
import com.example.vref_solutions_tablet_application.Components.TextField.RegularTextField
import com.example.vref_solutions_tablet_application.Components.TextField.RegularTextFieldWithLabel
import com.example.vref_solutions_tablet_application.Components.TextField.RegularTextFieldWithPlaceholder
import com.example.vref_solutions_tablet_application.Components.`Text-UI`.SmallAditionalInfoText
import com.example.vref_solutions_tablet_application.Enums.PopUpType
import com.example.vref_solutions_tablet_application.Handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.StylingClasses.FontSizeStatic
import com.example.vref_solutions_tablet_application.StylingClasses.IconSizeStatic
import com.example.vref_solutions_tablet_application.StylingClasses.PaddingStatic
import com.example.vref_solutions_tablet_application.StylingClasses.RoundedSizeStatic
import com.example.vref_solutions_tablet_application.ViewModels.LoginScreenViewModel
import com.example.vref_solutions_tablet_application.ui.theme.NegativeActionColor
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBackground
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBoxDarkBackground
import com.example.vref_solutions_tablet_application.ui.theme.PositiveActionColor
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(viewModel: LoginScreenViewModel = viewModel(), navController: NavController) {
    //reset previous info

    Scaffold {
        Row() {
            Row(
                horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = PaddingStatic.Large, end = PaddingStatic.Large)
            ) {

                LeftPartLoginScreen(viewModel, navController)
            }


            //Right Part Login Screen aka Image Display:
            Row(
                horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colors.onBackground)
                    .padding(start = PaddingStatic.Large, end = PaddingStatic.Large)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.mainlogo_shadow_v2),
                    contentDescription = "main_logo",
                    modifier = Modifier.padding(top = 192.dp)
                )
            }
        }
        
        val activateAccountPopUpScreenIsOpen = viewModel.uiActivateAccountPopUpIsOpen.collectAsState()

        AnimatedVisibility(
            modifier = Modifier.zIndex(11f), visible = activateAccountPopUpScreenIsOpen.value, enter = slideIn(
                tween(200, easing = LinearEasing), initialOffset = { IntOffset(x = 0, y = 300) })
        ) {
            //pop up container
            if (activateAccountPopUpScreenIsOpen.value) {
                Box(modifier = Modifier.zIndex(11f)) {
                    //add this background box as a quality of live feature so that clicking on the side of the box will make you also return back to the livetraining
                    //which is a feature also used in many more applications
                    Row(
                        modifier = Modifier
                            .fillMaxSize(), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ActivateAccountPopUp(viewModel = viewModel)

                        } // pop up main column
                    } // end pop up screen row

                }

            } // end if pop up screen
        }

        if(activateAccountPopUpScreenIsOpen.value) {
            val interactionSource = remember { MutableInteractionSource() }
            Box(modifier = Modifier //transparent-black background screen filler
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .zIndex(10f)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { viewModel.ToggleActivationAccountPopUp() }) //doesn't work for some reason for now
        }
    }
}

@Composable
fun ActivateAccountPopUp(viewModel: LoginScreenViewModel) {
    Card(elevation = 12.dp, modifier = Modifier
        .width(450.dp)
        .height(442.dp)
        .clip(RoundedCornerShape(RoundedSizeStatic.Small)),
        backgroundColor = PopUpBackground
    ) {

        Column(modifier = Modifier.padding(PaddingStatic.Small)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Activate account",
                    color = Color(0xFFFFFFFF),
                    fontSize = FontSizeStatic.Normal
                )
                Image(
                    painter = painterResource(id = R.drawable.x_circle_fill),
                    contentDescription = "cross_icon",
                    modifier = Modifier
                        .size(IconSizeStatic.Medium)
                        .clickable {
                            viewModel.ToggleActivationAccountPopUp()
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
                        SmallAditionalInfoText(text = "Activation code", modifier = Modifier.fillMaxWidth(), textSize = FontSizeStatic.Tiny, textAlign = TextAlign.Left,
                            fontWeight = FontWeight.Normal)

                        Spacer(Modifier.padding(PaddingStatic.Mini))

                        var activationCodeState = viewModel.inputActivationCode.collectAsState()

                        RegularTextFieldWithPlaceholder(placeholderText = "Activation code here",modifier = Modifier.fillMaxWidth().shadow(elevation = 12.dp), value = activationCodeState,
                            onValueChangeFun = { viewModel.SetInputActivationCode(it) }, isPasswordDisplay = false, enabled = true)
                    }

                    Spacer(Modifier.padding(PaddingStatic.Tiny))

                    Column(modifier = Modifier.weight(3f)) {

                        SmallAditionalInfoText(text = "Your new password", modifier = Modifier.fillMaxWidth(), textSize = FontSizeStatic.Tiny, textAlign = TextAlign.Left,
                            fontWeight = FontWeight.Normal)

                        Spacer(Modifier.padding(PaddingStatic.Mini))

                        val newPasswordFirstState = viewModel.inputFirstNewPassword.collectAsState()

                        RegularTextField(modifier = Modifier.fillMaxWidth().shadow(elevation = 12.dp), value = newPasswordFirstState,
                            onValueChangeFun = { viewModel.SetFirstNewPassword(it) }, isPasswordDisplay = true, enabled = true)

                    }

                    Column(modifier = Modifier.weight(2f)) {
                        val uiPasswordContainsRightCharacters = viewModel.uiPasswordContainsRightCharacters.collectAsState()
                        val uiPasswordIsLongEnough = viewModel.uiPasswordIsLongEnough.collectAsState()

                        Column(modifier = Modifier.fillMaxWidth().height(50.dp)) {
                            Spacer(Modifier.padding(PaddingStatic.Mini))

                            Text(text = "Contains lowercase, uppercase, a special character and a number", fontSize = FontSizeStatic.Mini,
                                color = if(uiPasswordContainsRightCharacters.value) PositiveActionColor else NegativeActionColor)

                            Text(text = "Password is long enough", fontSize = FontSizeStatic.Mini,
                                color = if(uiPasswordIsLongEnough.value) PositiveActionColor else NegativeActionColor)
                        }
                    }

                    Spacer(Modifier.padding(PaddingStatic.Mini))

                    Column(modifier = Modifier.weight(3f)) {
                        SmallAditionalInfoText(text = "Confirm password", modifier = Modifier.fillMaxWidth(), textSize = FontSizeStatic.Tiny, textAlign = TextAlign.Left,
                            fontWeight = FontWeight.Normal)

                        Spacer(Modifier.padding(PaddingStatic.Mini))

                        var newPasswordSecondState = viewModel.inputSecondNewPassword.collectAsState()

                        RegularTextField(modifier = Modifier.fillMaxWidth().shadow(elevation = 12.dp), value = newPasswordSecondState,
                            onValueChangeFun = { viewModel.SetSecondNewPassword(it) }, isPasswordDisplay = true, enabled = true)
                    }

                } // end content column wrapper

                Spacer(Modifier.weight(2f))
            } // end row wrapper

            Spacer(Modifier.padding(PaddingStatic.Small))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                RegularRectangleButton(buttonText = "Cancel", onClick = { viewModel.ToggleActivationAccountPopUp() }, modifier = Modifier.weight(1f),
                    fontSize = FontSizeStatic.Small, invertedColors = true)

                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(2f)
                )

                RegularRectangleButton(buttonText = "Confirm",
                    onClick = {
                        viewModel.viewModelScope.launch {
                            viewModel.ActivateAccountAttempt(context = viewModel.getApplication<Application>().baseContext)
                        }
                }, modifier = Modifier.weight(1f), fontSize = FontSizeStatic.Small, invertedColors = false)


            } // end row
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LeftPartLoginScreen(viewModel: LoginScreenViewModel, navController: NavController) {
    Column() {
        PageTitle(titleText = "Login", fontSize = FontSizeStatic.LargeTitle)

        Spacer(Modifier.padding(PaddingStatic.Large))

        Column {

            val loginUsernameText = viewModel.inputUsername.collectAsState()

            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
                .fillMaxWidth()
                .padding(start = PaddingStatic.Extreme, end = PaddingStatic.Extreme)) {

                RegularTextFieldWithLabel(labelText = "E-mail", modifier = Modifier.fillMaxWidth().shadow(elevation = 12.dp), value = loginUsernameText,
                    onValueChangeFun = { viewModel.SetInputUsername(it) },isPasswordDisplay = false, enabled = true)

            }
        }

        Spacer(Modifier.padding(PaddingStatic.Small))

        Column {

            val loginPasswordText = viewModel.inputPassword.collectAsState()
            val loginNegativeFeedbackText = viewModel.negativeFeedbackText.collectAsState()

            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
                .fillMaxWidth()
                .padding(start = PaddingStatic.Extreme, end = PaddingStatic.Extreme)) {

                RegularTextFieldWithLabel(labelText = "E-mail", modifier = Modifier.fillMaxWidth().shadow(elevation = 12.dp), value = loginPasswordText,
                    onValueChangeFun = { viewModel.SetInputPassword(it) },isPasswordDisplay = true, enabled = true)

            }

            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(top = PaddingStatic.Small), textAlign = TextAlign.Center , text = loginNegativeFeedbackText.value, color = NegativeActionColor)


        }

        Spacer(Modifier.padding(PaddingStatic.Medium))

        Row(modifier = Modifier.padding(start = PaddingStatic.Extreme, end = PaddingStatic.Extreme)) {

            // RegularRectangleButton() not working for this button, because the height would not properly get set like it should do
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primary),
                onClick = {
                    viewModel.viewModelScope.launch {
                        viewModel.LoginAttempt(navController = navController)
                    }
                }) {
                Text(text = "Login", color = Color.White, fontWeight = FontWeight.Bold, fontSize = FontSizeStatic.Normal)
            }
        }
        val loginAttemptIsActive = viewModel.uiLoginAttemptIsActive.collectAsState()

        if(loginAttemptIsActive.value) {
            Spacer(Modifier.padding(PaddingStatic.Tiny))

            DynamicLoadingDisplay(loadingText = "", iconSize = IconSizeStatic.Medium, textColor = Color.White, iconTint = Color.White)
        }

        Spacer(Modifier.padding(PaddingStatic.Small))

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {

            Text(text = "To activate your account click here", textDecoration = TextDecoration.Underline, color = MaterialTheme.colors.primary, fontWeight = FontWeight.Bold, fontSize = FontSizeStatic.Small,
                modifier = Modifier.clickable {
                    //open activate account pop up
                    viewModel.ToggleActivationAccountPopUp()
                }
            )

        }

    }
}

