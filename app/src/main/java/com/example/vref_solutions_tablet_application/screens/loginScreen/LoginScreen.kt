package com.example.vref_solutions_tablet_application.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import com.example.vref_solutions_tablet_application.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vref_solutions_tablet_application.screens.loginScreen.ActivateAccountPopUp
import com.example.vref_solutions_tablet_application.screens.loginScreen.LeftPartLoginScreen
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*
import com.example.vref_solutions_tablet_application.viewModels.LoginScreenViewModel

@Composable
fun LoginScreen(viewModel: LoginScreenViewModel = viewModel(), navController: NavController) {
    Scaffold {
        Row() {
            Row(
                horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = MaterialTheme.padding.large, end = MaterialTheme.padding.large)
            ) {

                LeftPartLoginScreen(viewModel, navController)
            }


            //Right Part Login Screen aka Image Display:
            Row(
                horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colors.onBackground)
                    .padding(start = MaterialTheme.padding.large, end = MaterialTheme.padding.large)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.mainlogo_shadow_v2),
                    contentDescription = stringResource(R.string.cd_main_logo),
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
                ) { viewModel.toggleActivationAccountPopUp() }) //doesn't work for some reason for now
        }
    }
}





