package com.example.vref_solutions_tablet_application.Components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.vref_solutions_tablet_application.Handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.Models.PopUpModels.AreYouSureInfo
import com.example.vref_solutions_tablet_application.Models.PopUpModels.ExitTraining
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.StylingClasses.FontSizeStatic
import com.example.vref_solutions_tablet_application.StylingClasses.IconSizeStatic
import com.example.vref_solutions_tablet_application.StylingClasses.PaddingStatic
import com.example.vref_solutions_tablet_application.StylingClasses.RoundedSizeStatic
import com.example.vref_solutions_tablet_application.ViewModels.LiveTrainingViewModel
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
                        withStyle(style = SpanStyle(fontSize = FontSizeStatic.Small)) {
                            append("Save this training as:\n\n")
                        }

                        withStyle(style = SpanStyle(fontSize = FontSizeStatic.Normal,fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)) {
                            append("${currentTrainingHandler.GetCurrentTrainingCreationDate(topBarFormat = false)} | " +
                                    "${currentTrainingHandler.GetCurrentTrainingDateTime()} - ${sdf.format(Date())}\n\n")
                        }

                        withStyle(style = SpanStyle(fontSize = FontSizeStatic.Tiny,fontWeight = FontWeight.Bold)) {
                            append("Note: After saving you will not be able to make anymore changes to the marked events. Video streams are not saved")
                        }

                    },
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = FontSizeStatic.Small,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.padding(PaddingStatic.Small))

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MenuButton(
                    backgroundColor = MaterialTheme.colors.primary,
                    textColor = Color.White,
                    buttonText = "Save & Finish training",
                    buttonAction = {


                        exitTrainingHandler.SetAndDisplayAreYouSurePopUp(
                            infoClass = AreYouSureInfo(
                                popUptitle = "Save & finish training",
                                popUpexplanation =
                                buildAnnotatedString {
                                    append("Are you sure you want to ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(
                                            "finish "
                                        )
                                    }
                                    append("this training?\n\n")

                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("All marked event can’t be changed after finishing the training.")
                                    }

                                },
                                confirmActionText = "Finish training",
                                cancelActionText = "Cancel",
                                width = 420.dp,
                                height = 250.dp,
                                isDiscardTraining = false,
                                isFinishTraining = true,
                                isDeleteUser = false,
                            )
                        )
                    }
                )

                Spacer(Modifier.padding(PaddingStatic.Tiny))

                MenuButton(
                    backgroundColor = MaterialTheme.colors.primary,
                    textColor = Color.White,
                    buttonText = "Leave training",
                    buttonAction = { exitTrainingHandler.SetAndDisplayAreYouSurePopUp(
                        infoClass = AreYouSureInfo(
                                popUptitle = "Leave training",
                                popUpexplanation =
                                    buildAnnotatedString {
                                        append("Are you sure you want to ")
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("leave ")}
                                        append("this training?\n")

                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("You can still access this training from the main menu.\n\n")
                                        }

                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("*Be aware that you have to finish the training for it to become accessible for the students and you")
                                        }
                                    },
                                confirmActionText = "Leave training",
                                cancelActionText = "Cancel",
                                width = 420.dp,
                                height = 280.dp,
                                isDiscardTraining = false,
                                isFinishTraining = false,
                                isDeleteUser = false,
                            )
                        )
                    }
                )

                Spacer(Modifier.padding(PaddingStatic.Tiny))

                MenuButton(
                    backgroundColor = Color.White,
                    textColor = MaterialTheme.colors.primary,
                    buttonText = "Cancel",
                    buttonAction = { viewModel.ClosePopUpScreen() })

                //functionality remove because no API support for this call

//                Spacer(Modifier.padding(PaddingStatic.Small))
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
                            cancelAction = { exitTrainingHandler.CloseAreYouSurePopUp() },
                            {
                                exitTrainingHandler.Confirm()
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
            .shadow(2.dp, RoundedCornerShape(RoundedSizeStatic.Small))
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

