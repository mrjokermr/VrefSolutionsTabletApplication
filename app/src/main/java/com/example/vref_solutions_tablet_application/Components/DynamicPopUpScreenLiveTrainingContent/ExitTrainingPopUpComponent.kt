package com.example.vref_solutions_tablet_application.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.models.popUpModels.AreYouSureInfo
import com.example.vref_solutions_tablet_application.models.popUpModels.ExitTraining
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*
import com.example.vref_solutions_tablet_application.viewModels.LiveTrainingViewModel
import com.example.vref_solutions_tablet_application.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ExitTraingPopUpcomponent(exitTrainingHandler: ExitTraining, viewModel: LiveTrainingViewModel) {
    exitTrainingHandler.viewModel = viewModel

    var areYouSureScreen = exitTrainingHandler.areYouSureScreenOpen.collectAsState()
    var areYouSureInfo = remember { exitTrainingHandler.areYouSureInfo }

    var currentTrainingHandler: CurrentTrainingHandler = CurrentTrainingHandler(currentContext = LocalContext.current)
    val sdf = SimpleDateFormat("HH:mm") //for formatting the current date time

    Box() {

        Column(Modifier.zIndex(12f)) {

            Column() {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontSize = MaterialTheme.typography.h5.fontSize)) {
                            append("${stringResource(R.string.save_training_as)}:\n\n")
                        }

                        withStyle(style = SpanStyle(fontSize = MaterialTheme.typography.h4.fontSize,fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)) {
                            append("${currentTrainingHandler.getCurrentTrainingCreationDate(topBarFormat = false)} | " +
                                    "${currentTrainingHandler.getCurrentTrainingDateTime()} - ${sdf.format(Date())}\n\n")
                        }

                        withStyle(style = SpanStyle(fontSize = MaterialTheme.typography.h6.fontSize,fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.closing_training_note))
                        }

                    },
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = MaterialTheme.typography.h5.fontSize,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.padding(MaterialTheme.padding.small))

            val saveAndFinishTrainingText = stringResource(R.string.save_and_finish_training)
            val areYouSureYouWantToText = stringResource(R.string.are_you_sure_you_want_to)
            val finishText = stringResource(R.string.finish)
            val thisTrainingText = stringResource(R.string.this_training)
            val markedTrainingEventsWarningText = stringResource(R.string.marked_training_events_warning)
            val cancelText = stringResource(R.string.cancel)
            val finishTrainingText = stringResource(R.string.finish_training)
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MenuButton(
                    backgroundColor = MaterialTheme.colors.primary,
                    textColor = Color.White,
                    buttonText = saveAndFinishTrainingText,
                    buttonAction = {
                        exitTrainingHandler.setAndDisplayAreYouSurePopUp(
                            infoClass = AreYouSureInfo(
                                popUptitle = saveAndFinishTrainingText,
                                popUpexplanation =
                                buildAnnotatedString {
                                    append(areYouSureYouWantToText)
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(
                                            "$finishText "
                                        )
                                    }
                                    append("$thisTrainingText?\n\n")

                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("$markedTrainingEventsWarningText.")
                                    }

                                },
                                confirmActionText = finishTrainingText,
                                cancelActionText = cancelText,
                                width = 420.dp,
                                height = 250.dp,
                                isDiscardTraining = false,
                                isFinishTraining = true,
                                isDeleteUser = false,
                            )
                        )
                    }
                )

                Spacer(Modifier.padding(MaterialTheme.padding.tiny))

                val leaveTrainingText = stringResource(R.string.leave_training)
                val stillAccessTrainingTipText = stringResource(R.string.still_access_training_tip)
                val beAwareFinishTrainingTipText = stringResource(R.string.be_aware_finish_training_tip)

                MenuButton(
                    backgroundColor = MaterialTheme.colors.primary,
                    textColor = Color.White,
                    buttonText = leaveTrainingText,
                    buttonAction = { exitTrainingHandler.setAndDisplayAreYouSurePopUp(
                        infoClass = AreYouSureInfo(
                                popUptitle = leaveTrainingText,
                                popUpexplanation =
                                    buildAnnotatedString {
                                        append(areYouSureYouWantToText)
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("leave ")}
                                        append("$thisTrainingText?\n")

                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("$stillAccessTrainingTipText.\n\n")
                                        }

                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("*$beAwareFinishTrainingTipText")
                                        }
                                    },
                                confirmActionText = leaveTrainingText,
                                cancelActionText = cancelText,
                                width = 420.dp,
                                height = 280.dp,
                                isDiscardTraining = false,
                                isFinishTraining = false,
                                isDeleteUser = false,
                            )
                        )
                    }
                )

                Spacer(Modifier.padding(MaterialTheme.padding.tiny))

                MenuButton(
                    backgroundColor = Color.White,
                    textColor = MaterialTheme.colors.primary,
                    buttonText = cancelText,
                    buttonAction = { viewModel.closePopUpScreen() })

                //functionality remove because no API support for this call

//                Spacer(Modifier.padding(MaterialTheme.padding.small))
//
//                MenuButton(
//                    backgroundColor = NegativeActionColor,
//                    textColor = Color.White,
//                    buttonText = "Discard training",
//                    buttonAction = {
//                        exitTrainingHandler.SetAndDisplayAreYouSurePopUp(
//                            infoClass = AreYouSureInfo(
//                                popUptitle = "Discard training",
//                                popUpexplanation =
//                                buildAnnotatedString {
//                                    append("Are you sure you want to discard this training? ")
//
//                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
//                                        append("All marked events will be deleted permanently.\n\n")
//                                    }
//                                },
//                                confirmActionText = "Discard training",
//                                cancelActionText = "Cancel",
//                                width = 420.dp,
//                                height = 220.dp,
//                                isDiscardTraining = true,
//                                isFinishTraining = false,
//                                isDeleteUser = false,
//                            )
//                        )
//                    }
//                )

            } //end column buttons list

        } // end main wrapper column
        AnimatedVisibility(modifier = Modifier.zIndex(14f),visible = areYouSureScreen.value, enter = fadeIn(tween(200,
            easing = FastOutSlowInEasing))
        ) {
            if (areYouSureScreen.value) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(14f), horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        AreYouSureContainer(areYouSureInfo = areYouSureInfo.value,
                            cancelAction = { exitTrainingHandler.closeAreYouSurePopUp() },
                            {
                                exitTrainingHandler.confirm()
                            }
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun MenuButton(backgroundColor: Color, textColor: Color, buttonText: String, buttonAction: ()->Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(Modifier.weight(1f))

        Button(modifier = Modifier
            .weight(3f)
            .shadow(2.dp, RoundedCornerShape(MaterialTheme.roundedCornerShape.small))
            .height(32.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
            onClick = {
                buttonAction()
            }
        ) {
            Text(text = buttonText , color = textColor, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.weight(1f))
    }

}

