package com.example.vref_solutions_tablet_application.screens.mainMenuScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.models.TrainingSummary
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.TextShadowStatic
import com.example.vref_solutions_tablet_application.viewModels.MainMenuViewModel
import com.example.vref_solutions_tablet_application.ui.theme.NegativeActionColor
import com.example.vref_solutions_tablet_application.ui.theme.PositiveActionColor

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DisplayListItemTrainingSummary(trainingSummary: TrainingSummary, viewModel: MainMenuViewModel, currentTrainingHandler: CurrentTrainingHandler) {
    val trainingIsNotInProgress = trainingSummary.isViewable()

    var firstStudentName = if(trainingSummary.students.size > 0) trainingSummary.students[0].fullName() else ""
    var secondStudentName = if(trainingSummary.students.size > 1) trainingSummary.students[1].fullName() else ""

    Card(modifier = Modifier.height(136.dp), elevation = 4.dp, shape = RoundedCornerShape(topEnd = 14.dp, bottomEnd = 14.dp), backgroundColor = Color(0xFF262626)) {
        Row() {
            Column(
                Modifier
                    .weight(6f)
                    .padding(start = MaterialTheme.padding.tiny, end = MaterialTheme.padding.tiny)
            ) {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    if(trainingIsNotInProgress) {
                        Text(
                            text = "${trainingSummary.getTrainingSummaryListDate()} | ${trainingSummary.getReadableTime()}",
                            fontSize = MaterialTheme.typography.h4.fontSize,
                            color = MaterialTheme.colors.onSurface,
                            style = TextShadowStatic.Small()
                        )
                    }
                    else {
                        Text(
                            text = "${trainingSummary.getTrainingSummaryListDate()} | ",
                            fontSize = MaterialTheme.typography.h4.fontSize,
                            color = MaterialTheme.colors.onSurface,
                            style = TextShadowStatic.Small()
                        )

                        Spacer(Modifier.padding(MaterialTheme.padding.mini))

                        Image(
                            painter = painterResource(id = R.drawable.clock_history),
                            contentDescription = stringResource(R.string.cd_clock_icon),
                            modifier = Modifier.size(MaterialTheme.iconSize.small)
                        )

                        Spacer(Modifier.padding(MaterialTheme.padding.mini))

                        Text(
                            text = stringResource(R.string.in_progress),
                            fontSize = MaterialTheme.typography.h5.fontSize,
                            color = MaterialTheme.colors.onSurface,
                            style = TextShadowStatic.Small()
                        )
                    }
                }


                Spacer(Modifier.padding(3.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    //instructor
                    Image(
                        painter = painterResource(id = R.drawable.instructor_ico),
                        contentDescription = stringResource(R.string.cd_instructor_icon),
                        modifier = Modifier.size(MaterialTheme.iconSize.small)
                    )
                    Spacer(Modifier.padding(2.dp))
                    Text(
                        text = "${trainingSummary.instructor.fullName()}",
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier.widthIn(134.dp, 134.dp),
                    )
                }

                Spacer(Modifier.padding(MaterialTheme.padding.tiny))

                Row(verticalAlignment = Alignment.CenterVertically) {

                    if(firstStudentName != "") DisplayStudent(name = firstStudentName)

                    Spacer(Modifier.padding(MaterialTheme.padding.mini))

                    if(secondStudentName != "") DisplayStudent(name = secondStudentName)

                }
            }

            Column(
                Modifier
                    .weight(2f)
                    .fillMaxWidth()
                    .fillMaxHeight(), verticalArrangement = Arrangement.Center) {

                if(trainingIsNotInProgress) {
                    //View & Close button
//                    val buttonIsOpen: MutableState<Boolean> = remember { mutableStateOf(false) }
                    var buttonIsActive = false
                    val currentlySelectedTraining = viewModel.uiCurrentlySelectedTraining.collectAsState()

                    if(currentlySelectedTraining.value == trainingSummary) {
                        //viewModel.ClearCurrentlySelectedTraining()
                        buttonIsActive = true
                    }

                    Button(
                        onClick = {
                            if(buttonIsActive) {
                                viewModel.clearCurrentlySelectedTraining()
                            }
                            else {
                                viewModel.launchLoadAndDisplayTrainingEvents(selectedTrainingSum = trainingSummary)
                            }

                        },
                        contentPadding = PaddingValues(4.dp), shape = RoundedCornerShape(38),
                        colors = ButtonDefaults.buttonColors(backgroundColor = if(buttonIsActive) NegativeActionColor else MaterialTheme.colors.primaryVariant ),
                        modifier = Modifier.width(116.dp).height(34.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = if (buttonIsActive) R.drawable.x_circle_fill else R.drawable.eye_fill),
                                contentDescription = stringResource(R.string.cd_eye_icon),
                                modifier = Modifier.size(if (buttonIsActive) MaterialTheme.iconSize.small - 2.dp else MaterialTheme.iconSize.small)
                            )
                            Spacer(Modifier.padding(start = 8.dp, end = 4.dp))
                            Text(
                                text = if (buttonIsActive) stringResource(R.string.close) else stringResource(R.string.view),
                                color = MaterialTheme.colors.onSurface,
                                fontSize = MaterialTheme.typography.h5.fontSize
                            )
                        }
                    }
                }
                else {
                    Button(
                        onClick = {
                            //continue the chosen training
                            viewModel.continueTraining(trainingSumInfo = trainingSummary, currentTrainingHandler = currentTrainingHandler)
                        },
                        contentPadding = PaddingValues(4.dp),
                        shape = RoundedCornerShape(38),
                        colors = ButtonDefaults.buttonColors(backgroundColor = PositiveActionColor ),
                        modifier = Modifier
                            .width(116.dp)
                            .height(34.dp)

                    ) {
                        Text(
                            text = stringResource(R.string.continue_training),
                            color = MaterialTheme.colors.onSurface,
                            fontSize = MaterialTheme.typography.h5.fontSize
                        )
                    }
                }

            }

        } // end row

    } // end card
}