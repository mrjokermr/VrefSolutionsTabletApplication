package com.example.vref_solutions_tablet_application.Components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.vref_solutions_tablet_application.Components.Buttons.RegularRectangleButton
import com.example.vref_solutions_tablet_application.Components.TextField.RegularTextFieldWithPlaceholder
import com.example.vref_solutions_tablet_application.Components.`Text-UI`.SmallAditionalInfoText
import com.example.vref_solutions_tablet_application.Enums.UserType
import com.example.vref_solutions_tablet_application.Handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.StylingClasses.*
import com.example.vref_solutions_tablet_application.ViewModels.NewTrainingViewModel
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBackground
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBoxDarkBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SelectStudentsPopUpScreen(viewModel: NewTrainingViewModel, authToken: String) {
    val loggedInUserHandler = LoggedInUserHandler(currentContext = LocalContext.current)
    viewModel.viewModelScope.launch {
        viewModel.LoadSearchQuerySuggestions(loggedInUserHandler)
    }

    Card(
        elevation = 12.dp, modifier = Modifier
            .width(500.dp)
            .height(520.dp)
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
                    text = "Add student(s)",
                    color = Color(0xFFFFFFFF),
                    fontSize = FontSizeStatic.Normal,
                    style = TextShadowStatic.Small()
                )
                Image(
                    painter = painterResource(id = R.drawable.x_circle_fill),
                    contentDescription = "cross_icon",
                    modifier = Modifier
                        .size(IconSizeStatic.Medium)
                        .clickable {
                            viewModel.TogglePopUpScreen()
                        }
                )
            }

            //Content:
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(9f)
            ) {
                Column {

                    val searchQueryText = viewModel.inputSearchStudent.collectAsState()

                    SmallAditionalInfoText(text = "Search student", modifier = Modifier.fillMaxWidth(), textSize = FontSizeStatic.Tiny, textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Normal)

                    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
                        .fillMaxWidth()
                    ) {
                        RegularTextFieldWithPlaceholder(placeholderText = "firstname / lastname / e-mail",modifier = Modifier.fillMaxWidth().height(52.dp).shadow(elevation = 12.dp),
                            value = searchQueryText, onValueChangeFun = { viewModel.SetInputAndUpdateStudentsList(input = it, authToken = authToken) }, isPasswordDisplay = false,
                            enabled = true)

                    }

                    Spacer(Modifier.padding(PaddingStatic.Mini))

                    val allInputSuggenstionsFlow = viewModel.uiSearchQuerySuggestionsList.collectAsState()
                    //Input suggestions display
                    LazyRow() {
                        items(allInputSuggenstionsFlow.value.size) {
                            val currentQuerySuggestion = allInputSuggenstionsFlow.value[it]
                            if(currentQuerySuggestion.amountOfUsage > 3 && currentQuerySuggestion.query.isEmpty() == false) {
                                Button(
                                    modifier = Modifier
                                        .height(22.dp)
                                        .clip(RoundedCornerShape(RoundedSizeStatic.Medium)),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                                    elevation = ButtonDefaults.elevation(10.dp),
                                    contentPadding = PaddingValues(1.dp),
                                    onClick = { viewModel.SetInputAndUpdateStudentsList("${currentQuerySuggestion.query}",authToken = authToken) }) {
                                    Text(
                                        modifier = Modifier.padding(start = PaddingStatic.Mini, end = PaddingStatic.Mini),
                                        text = "${currentQuerySuggestion.query}", color = Color.Black,
                                        fontSize = FontSizeStatic.Tiny
                                    )
                                }

                                Spacer(Modifier.padding(PaddingStatic.Mini))
                            }

                        }
                    }

                    Spacer(Modifier.padding(PaddingStatic.Tiny))


                    SmallAditionalInfoText(text = "Search result", modifier = Modifier.fillMaxWidth(), textSize = FontSizeStatic.Tiny, textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Normal)

                    var selectedStudentOne = viewModel.uiSelectedStudentFirst.collectAsState()
                    var selectedStudentTwo = viewModel.uiSelectedStudentSecond.collectAsState()

                    var studentsSearchResult = viewModel.uiAllStudents.collectAsState()


                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(RoundedSizeStatic.Small))
                            .shadow(elevation = 12.dp)
                            .background(PopUpBoxDarkBackground)
                            .padding(PaddingStatic.Tiny),
                    ) {
                        if(studentsSearchResult.value != null && studentsSearchResult.value!!.size > 0) {
                            items(studentsSearchResult.value!!.size) {
                                //only display if the user is a student,
                                //the API doesn't supply a Get students method or a parameter where I can load in only students
                                val foundStudent = studentsSearchResult.value!![it]
                                if(foundStudent.userType == UserType.Student) {
                                    Text(text = foundStudent.FullName(),
                                        color = if(foundStudent.equals(selectedStudentOne.value) || foundStudent.equals(selectedStudentTwo.value)) MaterialTheme.colors.primary else Color.White,
                                        modifier = Modifier.clickable {
                                            viewModel.SetSelectedStudent(student = foundStudent, loggedInUserHandler = loggedInUserHandler)
                                        })

                                    Spacer(Modifier.padding(PaddingStatic.Mini))
                                }
                            }
                        }
                        else {
                            item() {
                                if(studentsSearchResult.value == null) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                        DynamicLoadingDisplay(
                                            loadingText = "Loading students info",
                                            iconSize = IconSizeStatic.Large,
                                            iconTint = Color.White,
                                            textColor = Color.White,
                                        )
                                    }
                                }
                                else {
                                    Text(text = "No students found for this input", color = Color.White, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                                }

                            }
                        }
                    }

                    //cache user input for input suggestions
                    LaunchedEffect(Unit) {
                        while(true) {
                            var preDelayInput = searchQueryText.value
                            delay(2000)
                            if(preDelayInput == searchQueryText.value && preDelayInput.length >= 3) {
                                //cache and save the input
                                viewModel.CacheAndSaveUserInput(input = preDelayInput.lowercase(),loggedInUserHandler = loggedInUserHandler)
                            }

                        }
                    }

                    Spacer(Modifier.padding(PaddingStatic.Tiny))

                    SmallAditionalInfoText(text = "Currently selected student(s)", modifier = Modifier.fillMaxWidth(), textSize = FontSizeStatic.Tiny, textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Normal)

                    LazyColumn(
                        modifier = Modifier
                            .padding(bottom = PaddingStatic.Small)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(RoundedSizeStatic.Small))
                            .shadow(elevation = 12.dp)
                            .background(PopUpBoxDarkBackground)
                            .padding(PaddingStatic.Tiny)
                        ,
                    ) {
                        if(selectedStudentOne.value != null) {
                            item() {
                                Row(modifier = Modifier.clickable {
                                    if(selectedStudentOne.value != null) viewModel.RemoveSelectedStudent(selectedStudentOne.value!!)
                                }) {
                                    Text(text = "${selectedStudentOne.value!!.FullName()}", color = Color.White)

                                    //Spacer(Modifier.fillMaxWidth())

                                    Image(
                                        painter = painterResource(id = R.drawable.cancel_ico),
                                        contentDescription = "cancel_icon",
                                        modifier = Modifier
                                            .size(IconSizeStatic.Small)
                                    )
                                }
                            }
                        }

                        item() {
                            Spacer(Modifier.padding(PaddingStatic.Mini))
                        }

                        if(selectedStudentTwo.value != null) {
                            item() {
                                Row(modifier = Modifier.clickable {
                                    if(selectedStudentTwo.value != null) viewModel.RemoveSelectedStudent(selectedStudentTwo.value!!)
                                }) {
                                    Text(text = "${selectedStudentTwo.value!!.FullName()}", color = Color.White)

                                    //Spacer(Modifier.fillMaxWidth())

                                    Image(
                                        painter = painterResource(id = R.drawable.cancel_ico),
                                        contentDescription = "cancel_icon",
                                        modifier = Modifier
                                            .size(IconSizeStatic.Small)
                                    )
                                }

                            }
                        }
                    }
                }
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RegularRectangleButton(buttonText = "Cancel", onClick = { viewModel.CancelAddingStudents() }, modifier = Modifier.weight(1f),
                    fontSize = FontSizeStatic.Small, invertedColors = true)

                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(2f)
                )

                RegularRectangleButton(buttonText = "Confirm", onClick = { viewModel.ConfirmAddingStudents() }, modifier = Modifier.weight(1f),
                    fontSize = FontSizeStatic.Small, invertedColors = false)

            } // end row
        }
    }

}