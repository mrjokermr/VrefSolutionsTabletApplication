package com.example.vref_solutions_tablet_application.Screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import com.example.vref_solutions_tablet_application.R
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.vref_solutions_tablet_application.Components.Buttons.RegularRectangleButton
import com.example.vref_solutions_tablet_application.Components.SelectStudentsPopUpScreen
import com.example.vref_solutions_tablet_application.Components.TopBarComp
import com.example.vref_solutions_tablet_application.Handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.Models.User
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.StylingClasses.*
import com.example.vref_solutions_tablet_application.ViewModels.NewTrainingViewModel
import com.example.vref_solutions_tablet_application.ui.theme.DarkerGray
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBoxDarkBackground
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NewTrainingScreen(viewModel: NewTrainingViewModel = viewModel(), navController: NavController) {
    viewModel.navController = navController
    viewModel.context = LocalContext.current
    val loggedInUserHanlder = LoggedInUserHandler(currentContext = LocalContext.current)
    val authToken = loggedInUserHanlder.GetAuthKey()


    viewModel.viewModelScope.launch {
        viewModel.GetAllStudents(authKey = authToken)
    }


    Scaffold(
        topBar = { TopBarComp(ScreenNavName.NewTraining, navController = navController,hideLogOutButton = false) },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            StartTrainingButton(viewModel)
        } ,
//        modifier = Modifier.padding(PaddingStatic.Small)
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
                    PageTitle(titleText = "NEW TRAINING", fontSize = FontSizeStatic.Large)

                    Spacer(Modifier.padding(PaddingStatic.Small))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.weight(1f))

                        Column(modifier = Modifier.weight(4f).clip(RoundedCornerShape(RoundedSizeStatic.Medium)).background(PopUpBoxDarkBackground)
                                            .padding(PaddingStatic.Tiny)) {
                            SelectedStudentAndInstructorDisplay(viewModel = viewModel, loggedInUserHandler = loggedInUserHanlder)
                        }

                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Spacer(Modifier.padding(PaddingStatic.Small))

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

@Composable
fun NewTrainingActionButtons(viewModel: NewTrainingViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row() {
            Spacer(Modifier.weight(2f))

            RegularRectangleButton(buttonText = "ADD STUDENT", onClick = { viewModel.TogglePopUpScreen() }, modifier = Modifier.weight(3f),
                fontSize = FontSizeStatic.Small, invertedColors = false)

            Spacer(Modifier.weight(2f))
        }

        Spacer(Modifier.padding(PaddingStatic.Mini))

        Row() {
            Spacer(Modifier.weight(2f))

            RegularRectangleButton(buttonText = "Cancel", onClick = { viewModel.NavigateToPage(ScreenNavName.MainMenu) }, modifier = Modifier.weight(3f),
                fontSize = FontSizeStatic.Small, invertedColors = true)

            Spacer(Modifier.weight(2f))
        }
    }
}

@Composable
fun SelectedStudentAndInstructorDisplay(viewModel: NewTrainingViewModel, loggedInUserHandler: LoggedInUserHandler) {
    val firstStudent = viewModel.uiSelectedStudentFirst.collectAsState()
    val secondStudent = viewModel.uiSelectedStudentSecond.collectAsState()

    if(firstStudent.value != null) {
        ShowcaseStudent(student = firstStudent.value!!, viewModel = viewModel)
    }
    else ShowcaseStudentPlaceholder(viewModel = viewModel)

    Spacer(Modifier.padding(PaddingStatic.Tiny))

    if(secondStudent.value != null) {
        ShowcaseStudent(student = secondStudent.value!!, viewModel = viewModel)
    }
    else ShowcaseStudentPlaceholder(viewModel = viewModel)

    Spacer(Modifier.padding(PaddingStatic.Small))

    ShowcaseInstructor(loggedInUserHandler = loggedInUserHandler)
}

@Composable
fun ShowcaseStudent(student: User, viewModel: NewTrainingViewModel) {
    val fullName = student.FullName()
    val email = student.email

    Row(horizontalArrangement = Arrangement.Center) {
        Column(modifier = Modifier
            .weight(3f)
        ) {

            Text(text = fullName, fontSize = FontSizeStatic.Small, color = Color.White)
            Text(text = email, fontSize = FontSizeStatic.Tiny, color = Color.White)
        }

        Column(modifier = Modifier
            .weight(1f)
            .clickable {
                viewModel.TogglePopUpScreen()
            }, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Image(
                painter = painterResource(id = R.drawable.person_ico_outline_white),
                contentDescription = "student_icon",
                modifier = Modifier.size(IconSizeStatic.Medium)
            )

            Text(text = "Student", fontSize = FontSizeStatic.Mini, color = Color.White)
        }
    }
}

@Composable
fun ShowcaseInstructor(loggedInUserHandler: LoggedInUserHandler) {
    val fullName = loggedInUserHandler.GetFullNameCurrentUser()
    val email = loggedInUserHandler.GetEmailCurrentUser()

    Row(horizontalArrangement = Arrangement.Center) {
        Column(modifier = Modifier
            .weight(3f)
        ) {

            Text(text = fullName, fontSize = FontSizeStatic.Small, color = Color.White)
            Text(text = email, fontSize = FontSizeStatic.Tiny, color = Color.White)
        }

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Image(
                painter = painterResource(id = R.drawable.person_ico_outline_white),
                contentDescription = "student_icon",
                modifier = Modifier.size(IconSizeStatic.Medium)
            )

            Text(text = "Instructor", fontSize = FontSizeStatic.Mini, color = Color.White)
        }
    }
}

@Composable
fun ShowcaseStudentPlaceholder(viewModel: NewTrainingViewModel) {
    Row(horizontalArrangement = Arrangement.Center) {

        Column(modifier = Modifier
            .weight(3f)
        ) {

            Text(text = "No student has been assigned \nto this slot", fontSize = FontSizeStatic.Tiny, color = Color.Gray)
        }

        Column(modifier = Modifier
            .weight(1f)
            .clickable {
                viewModel.TogglePopUpScreen()
            }, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Image(
                painter = painterResource(id = R.drawable.person_ico_add_outline),
                contentDescription = "student_icon",
                modifier = Modifier.size(IconSizeStatic.Medium)
            )

            Text(text = "Student", fontSize = FontSizeStatic.Mini, color = Color.White)
        }
    }
}

@Composable
fun StartTrainingButton(viewModel: NewTrainingViewModel) {
    var localContext = LocalContext.current //to be able to create a toast message inside startnewtraining

    FloatingActionButton(
        backgroundColor = MaterialTheme.colors.primary,
        onClick = {
            viewModel.viewModelScope.launch {
                viewModel.StartNewTraining(localContext)
            }
        }) {
        Row(modifier = Modifier.padding(top = PaddingStatic.Tiny, bottom = PaddingStatic.Tiny ,
            start = PaddingStatic.Small, end = PaddingStatic.Small),
            horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Start Training", color = Color.White, fontWeight = FontWeight.Bold, fontSize = FontSizeStatic.Small)
            Image(
                modifier = Modifier.size(IconSizeStatic.Medium),
                painter = painterResource(id = R.drawable.plus),
                contentDescription = "plus icon"
            )
        }
    }
}

@Composable
fun PageTitle(titleText: String, fontSize: TextUnit) {
    Text(text = titleText.uppercase(), color = MaterialTheme.colors.primary, fontSize = fontSize, fontWeight = FontWeight.Bold,
        style = TextShadowStatic.Medium(),
        textAlign = TextAlign.Center,modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth())
}