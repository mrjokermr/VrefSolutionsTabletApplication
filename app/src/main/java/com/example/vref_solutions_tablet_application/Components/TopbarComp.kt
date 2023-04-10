package com.example.vref_solutions_tablet_application.components

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vref_solutions_tablet_application.handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.screens.mainMenuScreen.DisplayStudent
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*


import com.example.vref_solutions_tablet_application.viewModels.MainMenuViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@SuppressLint("CoroutineCreationDuringComposition", "StateFlowValueCalledInComposition")
@Composable
fun TopBarComp(currentScreen: ScreenNavName, navController: NavController, hideLogOutButton: Boolean) {
    val loggedInUserHandler: LoggedInUserHandler = LoggedInUserHandler(LocalContext.current)
    val currentTrainingHandler: CurrentTrainingHandler = CurrentTrainingHandler(LocalContext.current)

    val fullName = loggedInUserHandler.getFullNameCurrentUser()
    val organisationName = loggedInUserHandler.getUserOrganisationName()

    val firstStudentNameFlow: MutableStateFlow<String> = MutableStateFlow("")
    val secondStudentNameFlow: MutableStateFlow<String> = MutableStateFlow("")

    if(currentScreen == ScreenNavName.LiveTraining) {
        val viewModel = MainMenuViewModel(application = Application())

        viewModel.launchLoadAndSetStudentNames(
            authKey = currentTrainingHandler.getAuthKey(),
            firstStudentId = currentTrainingHandler.getFirstStudentId().toLong(),
            secondStudentId = currentTrainingHandler.getSecondStudentId().toLong(),
            firstStudentNameFlow = firstStudentNameFlow,
            secondStudentNameFlow = secondStudentNameFlow
        )
    }


    Box(
        modifier = Modifier.fillMaxWidth().height(80.dp).background(Color(0xFF292C32))
    ) {
        Row(Modifier.padding(MaterialTheme.padding.tiny)) {
            Image(painter = painterResource(id = R.drawable.mainlogo), contentDescription = stringResource(R.string.cd_main_logo),
                modifier= Modifier.fillMaxHeight().weight(1.2f)
            )

            Row(modifier = Modifier.weight(4f).padding(start = MaterialTheme.padding.medium, end = MaterialTheme.padding.medium, top = MaterialTheme.padding.small),verticalAlignment = Alignment.CenterVertically) {
                if(currentScreen == ScreenNavName.LiveTraining) {
                    Column() {
                        Text(text = stringResource(R.string.training), color = Color.White, fontWeight = FontWeight.Bold)
                        Text(text = "${currentTrainingHandler.getCurrentTrainingCreationDate(topBarFormat = true)}", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.padding(MaterialTheme.padding.small))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if(firstStudentNameFlow.value != stringResource(R.string.unknown) || firstStudentNameFlow.value == "") DisplayStudent(firstStudentNameFlow.value)

                        Spacer(Modifier.padding(MaterialTheme.padding.mini))

                        if(secondStudentNameFlow.value != stringResource(R.string.unknown) || secondStudentNameFlow.value == "") DisplayStudent(secondStudentNameFlow.value)
                    }

                    Spacer(Modifier.padding(MaterialTheme.padding.small))
                }
            }



            Row(modifier = Modifier.weight(2f), horizontalArrangement = Arrangement.Center) {
                Image(painter = painterResource(id = R.drawable.topbar_person), contentDescription = stringResource(R.string.cd_main_logo),
                    modifier= Modifier.height(60.dp).weight(1f))

                Column(modifier = Modifier.weight(3f)) {
                    Text(text ="$fullName", color = MaterialTheme.colors.primaryVariant, fontSize = MaterialTheme.typography.h5.fontSize, style = MaterialTheme.typography.h4.copy(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(2f, 2f),
                            blurRadius = 8f
                        )
                    ))
                    Text(text = "$organisationName", color = MaterialTheme.colors.primaryVariant, fontSize = MaterialTheme.typography.h6.fontSize,style = MaterialTheme.typography.h4.copy(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(2f, 2f),
                            blurRadius = 8f
                        )
                    ))
                }

                if(!hideLogOutButton) {
                    Column(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        //log out button/icon
                        Image(
                            painter = painterResource(id = R.drawable.log_out),
                            contentDescription = stringResource(R.string.cd_log_out_icon),
                            modifier = Modifier.size(MaterialTheme.iconSize.medium).clickable {
                                navController.navigate(ScreenNavName.Login.route) {
                                    popUpTo(0) //makes sure their is nothing else to pop back to
                                }
                            }
                        )
                    }
                }




            }
        }


    }

}