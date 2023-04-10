package com.example.vref_solutions_tablet_application.components

import android.annotation.SuppressLint
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.components.buttons.RegularRectangleButton
import com.example.vref_solutions_tablet_application.components.textField.RegularTextFieldWithPlaceholder
import com.example.vref_solutions_tablet_application.components.textUI.SmallAditionalInfoText
import com.example.vref_solutions_tablet_application.enums.UserType
import com.example.vref_solutions_tablet_application.handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.viewModels.NewTrainingViewModel
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBackground
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBoxDarkBackground
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*



import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.TextShadowStatic
import kotlinx.coroutines.delay

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SelectStudentsPopUpScreen(viewModel: NewTrainingViewModel, authToken: String) {
    val loggedInUserHandler = LoggedInUserHandler(currentContext = LocalContext.current)

    viewModel.launchLoadSearchQuerySuggestions(loggedInUserHandler)

    Card(
        elevation = 12.dp, modifier = Modifier
            .width(500.dp)
            .height(520.dp)
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
                    text = stringResource(R.string.add_students),
                    color = Color(0xFFFFFFFF),
                    fontSize = MaterialTheme.typography.h4.fontSize,
                    style = TextShadowStatic.Small()
                )
                Image(
                    painter = painterResource(id = R.drawable.x_circle_fill),
                    contentDescription = stringResource(R.string.cd_cross_icon),
                    modifier = Modifier
                        .size(MaterialTheme.iconSize.medium)
                        .clickable {
                            viewModel.togglePopUpScreen()
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

                    SmallAditionalInfoText(text = stringResource(R.string.search_student), modifier = Modifier.fillMaxWidth(), textSize = MaterialTheme.typography.h6.fontSize, textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Normal)

                    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
                        .fillMaxWidth()
                    ) {
                        RegularTextFieldWithPlaceholder(placeholderText = stringResource(R.string.search_query_explanation),modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(elevation = 12.dp),
                            value = searchQueryText, onValueChangeFun = { viewModel.setInputAndUpdateStudentsList(input = it, authToken = authToken) }, isPasswordDisplay = false,
                            enabled = true)

                    }

                    Spacer(Modifier.padding(MaterialTheme.padding.mini))

                    val allInputSuggenstionsFlow = viewModel.uiSearchQuerySuggestionsList.collectAsState()
                    //Input suggestions display
                    LazyRow() {
                        items(allInputSuggenstionsFlow.value.size) {
                            val currentQuerySuggestion = allInputSuggenstionsFlow.value[it]
                            if(currentQuerySuggestion.amountOfUsage > 3 && currentQuerySuggestion.query.isEmpty() == false) {
                                Button(
                                    modifier = Modifier
                                        .height(22.dp)
                                        .clip(RoundedCornerShape(MaterialTheme.roundedCornerShape.medium)),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                                    elevation = ButtonDefaults.elevation(10.dp),
                                    contentPadding = PaddingValues(1.dp),
                                    onClick = { viewModel.setInputAndUpdateStudentsList("${currentQuerySuggestion.query}",authToken = authToken) }) {
                                    Text(
                                        modifier = Modifier.padding(start = MaterialTheme.padding.mini, end = MaterialTheme.padding.mini),
                                        text = "${currentQuerySuggestion.query}", color = Color.Black,
                                        fontSize = MaterialTheme.typography.h6.fontSize
                                    )
                                }

                                Spacer(Modifier.padding(MaterialTheme.padding.mini))
                            }

                        }
                    }

                    Spacer(Modifier.padding(MaterialTheme.padding.tiny))


                    SmallAditionalInfoText(text = stringResource(R.string.search_result), modifier = Modifier.fillMaxWidth(), textSize = MaterialTheme.typography.h6.fontSize, textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Normal)

                    var selectedStudentOne = viewModel.uiSelectedStudentFirst.collectAsState()
                    var selectedStudentTwo = viewModel.uiSelectedStudentSecond.collectAsState()

                    var studentsSearchResult = viewModel.uiAllStudents.collectAsState()


                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(MaterialTheme.roundedCornerShape.small))
                            .shadow(elevation = 12.dp)
                            .background(PopUpBoxDarkBackground)
                            .padding(MaterialTheme.padding.tiny),
                    ) {
                        if(studentsSearchResult.value != null && studentsSearchResult.value!!.size > 0) {
                            items(studentsSearchResult.value!!.size) {
                                //only display if the user is a student,
                                //the API doesn't supply a Get students method or a parameter where I can load in only students
                                val foundStudent = studentsSearchResult.value!![it]
                                if(foundStudent.userType == UserType.Student) {
                                    Text(text = foundStudent.fullName(),
                                        color = if(foundStudent.equals(selectedStudentOne.value) || foundStudent.equals(selectedStudentTwo.value)) MaterialTheme.colors.primary else Color.White,
                                        modifier = Modifier.clickable {
                                            viewModel.setSelectedStudent(student = foundStudent, loggedInUserHandler = loggedInUserHandler)
                                        })

                                    Spacer(Modifier.padding(MaterialTheme.padding.mini))
                                }
                            }
                        }
                        else {
                            item() {
                                if(studentsSearchResult.value == null) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                        DynamicLoadingDisplay(
                                            loadingText = stringResource(R.string.loading_student_info),
                                            iconSize = MaterialTheme.iconSize.large,
                                            iconTint = Color.White,
                                            textColor = Color.White,
                                        )
                                    }
                                }
                                else {
                                    Text(text = stringResource(R.string.no_students_found_input), color = Color.White, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
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
                                viewModel.cacheAndSaveUserInput(input = preDelayInput.lowercase(),loggedInUserHandler = loggedInUserHandler)
                            }

                        }
                    }

                    Spacer(Modifier.padding(MaterialTheme.padding.tiny))

                    SmallAditionalInfoText(text = stringResource(R.string.currently_selected_students), modifier = Modifier.fillMaxWidth(), textSize = MaterialTheme.typography.h6.fontSize, textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Normal)

                    LazyColumn(
                        modifier = Modifier
                            .padding(bottom = MaterialTheme.padding.small)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(MaterialTheme.roundedCornerShape.small))
                            .shadow(elevation = 12.dp)
                            .background(PopUpBoxDarkBackground)
                            .padding(MaterialTheme.padding.tiny)
                        ,
                    ) {
                        if(selectedStudentOne.value != null) {
                            item() {
                                Row(modifier = Modifier.clickable {
                                    if(selectedStudentOne.value != null) viewModel.removeSelectedStudent(selectedStudentOne.value!!)
                                }) {
                                    Text(text = "${selectedStudentOne.value!!.fullName()}", color = Color.White)

                                    //Spacer(Modifier.fillMaxWidth())

                                    Image(
                                        painter = painterResource(id = R.drawable.cancel_ico),
                                        contentDescription = "cancel_icon",
                                        modifier = Modifier
                                            .size(MaterialTheme.iconSize.small)
                                    )
                                }
                            }
                        }

                        item() {
                            Spacer(Modifier.padding(MaterialTheme.padding.mini))
                        }

                        if(selectedStudentTwo.value != null) {
                            item() {
                                Row(modifier = Modifier.clickable {
                                    if(selectedStudentTwo.value != null) viewModel.removeSelectedStudent(selectedStudentTwo.value!!)
                                }) {
                                    Text(text = "${selectedStudentTwo.value!!.fullName()}", color = Color.White)

                                    //Spacer(Modifier.fillMaxWidth())

                                    Image(
                                        painter = painterResource(id = R.drawable.cancel_ico),
                                        contentDescription = stringResource(R.string.cd_cancel_icon),
                                        modifier = Modifier
                                            .size(MaterialTheme.iconSize.small)
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
                RegularRectangleButton(buttonText = stringResource(R.string.cancel), onClick = { viewModel.cancelAddingStudents() }, modifier = Modifier.weight(1f),
                    fontSize = MaterialTheme.typography.h5.fontSize, invertedColors = true)

                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(2f)
                )

                RegularRectangleButton(buttonText = stringResource(R.string.confirm), onClick = { viewModel.confirmAddingStudents() }, modifier = Modifier.weight(1f),
                    fontSize = MaterialTheme.typography.h5.fontSize, invertedColors = false)

            } // end row
        }
    }

}