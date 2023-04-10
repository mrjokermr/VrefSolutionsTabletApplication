package com.example.vref_solutions_tablet_application.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.components.buttons.NewTrainingActionButtons
import com.example.vref_solutions_tablet_application.components.buttons.StartTrainingButton
import com.example.vref_solutions_tablet_application.components.SelectStudentsPopUpScreen
import com.example.vref_solutions_tablet_application.components.TopBarComp
import com.example.vref_solutions_tablet_application.components.textUI.PageTitle
import com.example.vref_solutions_tablet_application.handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.screens.newTrainingScreen.SelectedStudentAndInstructorDisplay
import com.example.vref_solutions_tablet_application.viewModels.NewTrainingViewModel
import com.example.vref_solutions_tablet_application.ui.theme.DarkerGray
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBoxDarkBackground


import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NewTrainingScreen(viewModel: NewTrainingViewModel = viewModel(), navController: NavController) {
    viewModel.navController = navController

    val loggedInUserHanlder = LoggedInUserHandler(currentContext = LocalContext.current)
    val authToken = loggedInUserHanlder.getAuthKey()

    viewModel.launchGetAllStudents(authKey = authToken)

    Scaffold(
        topBar = { TopBarComp(ScreenNavName.NewTraining, navController = navController,hideLogOutButton = false) },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            StartTrainingButton(viewModel)
        } ,
//        modifier = Modifier.padding(MaterialTheme.padding.small)
    ) {
        var selectStudentsPopUpisOpen = viewModel.uiPopUpScreenIsOpen.collectAsState()

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
            ) {
                Spacer(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(DarkerGray))

                Column(modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()) {
                    PageTitle(titleText = "${stringResource(R.string.new_training).toUpperCase()}", fontSize = MaterialTheme.typography.h3.fontSize)

                    Spacer(Modifier.padding(MaterialTheme.padding.small))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.weight(1f))

                        Column(modifier = Modifier.weight(4f).clip(RoundedCornerShape(
                            MaterialTheme.roundedCornerShape.medium)).background(PopUpBoxDarkBackground)
                                            .padding(MaterialTheme.padding.tiny)) {
                            SelectedStudentAndInstructorDisplay(viewModel = viewModel, loggedInUserHandler = loggedInUserHanlder)
                        }

                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Spacer(Modifier.padding(MaterialTheme.padding.small))

                    NewTrainingActionButtons(viewModel = viewModel)

                }

                Spacer(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(DarkerGray))
            }
            AnimatedVisibility(modifier = Modifier.zIndex(2f),visible = selectStudentsPopUpisOpen.value, enter = slideIn(
                tween(200, easing = LinearEasing), initialOffset = { IntOffset(x = 0, y = 300) })
            ) {
                if (selectStudentsPopUpisOpen.value) {
                    Box(modifier = Modifier.zIndex(2f)) {
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

                                SelectStudentsPopUpScreen(viewModel = viewModel, authToken = authToken)

                            } // pop up main column
                        } // end pop up screen row

                    }

                }
            }
        }

    }
}