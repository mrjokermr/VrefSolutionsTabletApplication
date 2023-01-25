package com.example.vref_solutions_tablet_application.Components

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import com.example.vref_solutions_tablet_application.ViewModels.LiveTrainingViewModel
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewModelScope
import com.example.vref_solutions_tablet_application.Components.Buttons.RegularRectangleButton
import com.example.vref_solutions_tablet_application.Components.TextField.RegularTextFieldWithPlaceholder
import com.example.vref_solutions_tablet_application.Components.`Text-UI`.SmallAditionalInfoText
import com.example.vref_solutions_tablet_application.Enums.FeedbackTarget
import com.example.vref_solutions_tablet_application.Enums.PopUpType
import com.example.vref_solutions_tablet_application.Handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.Handlers.IconDisplayHandler
import com.example.vref_solutions_tablet_application.Models.PopUpModels.AreYouSureInfo
import com.example.vref_solutions_tablet_application.Models.PopUpModels.BasePopUpScreen
import com.example.vref_solutions_tablet_application.Models.PopUpModels.EditEvent
import com.example.vref_solutions_tablet_application.Models.TrainingEvent
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.StylingClasses.*
import com.example.vref_solutions_tablet_application.ui.theme.*
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddOrEditEventComponent(popUpScreenHandler: BasePopUpScreen, viewModel: LiveTrainingViewModel, isAddNewEvent: Boolean) {
    val currentTrainingHandler = CurrentTrainingHandler(currentContext = LocalContext.current)
    val currentlySelectedIcon = viewModel.uiCurrentlySelectedIconForEvent.collectAsState()

    val eventToEdit: TrainingEvent? = viewModel.uiEventToEdit.value


    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(PaddingStatic.Small)) {

            //Top part with icon select and event title set
            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {

                Row(verticalAlignment = Alignment.Bottom) {
                    //big icon display
                    Column( modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(PaddingStatic.Mini)
                        .clickable {
                            if (popUpScreenHandler.type.equals(PopUpType.ADD_EVENT)) {
                                viewModel.ToggleIconPickerPopUp(onCloseReset = false)
                            } else if (eventToEdit != null && !eventToEdit.IsPrescribedEvent()) {
                                viewModel.ToggleIconPickerPopUp(onCloseReset = false)
                            } else {
                                Toast.makeText(viewModel.context, "Can't change the icon of a prescribed event", Toast.LENGTH_LONG).show()
                            }
                        }
                        , horizontalAlignment = Alignment.CenterHorizontally) {

                        SmallAditionalInfoText(text = "Icon", modifier = Modifier.fillMaxWidth().weight(1f), textSize = FontSizeStatic.Tiny, textAlign = TextAlign.Left,
                            fontWeight = FontWeight.Normal)

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(3f)
                                .clip(RoundedCornerShape(RoundedSizeStatic.Large))
                                .background(PopUpBoxBackground)
                            ,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                modifier = Modifier
                                    .size(IconSizeStatic.Large)
                                    .clip(
                                        RoundedCornerShape(if (eventToEdit != null && eventToEdit.IsPrescribedEvent()) RoundedSizeStatic.Small else 0.dp)
                                    )
                                    .background(if (eventToEdit != null && eventToEdit.IsPrescribedEvent()) Color.White else Color.Transparent)
                                ,
                                painter = painterResource(id =
                                if(currentlySelectedIcon.value == "") R.drawable.plus
                                else if(eventToEdit != null && eventToEdit.IsPrescribedEvent()) eventToEdit.GetIconPainterId()
                                else IconDisplayHandler.GetPainterIdForSymbolName(currentlySelectedIcon.value)),
                                contentDescription = "selected icon example"
                            )
                        }

                    }

                    //event name input display
                    val eventTitleText = viewModel.inputEventTitle.collectAsState()

                    Column(modifier = Modifier
                        .weight(4f)
                        .padding(
                            start = PaddingStatic.Medium,
                            end = PaddingStatic.Small,
                            bottom = PaddingStatic.Tiny
                        ), verticalArrangement = Arrangement.Center) {

                        SmallAditionalInfoText(text = if(eventToEdit != null && eventToEdit.IsPrescribedEvent()) "Event name of prescribed event *Can't be edited" else "Event name ",
                            modifier = Modifier.fillMaxWidth().padding(bottom = PaddingStatic.Tiny), textSize = FontSizeStatic.Tiny, textAlign = TextAlign.Left, fontWeight = FontWeight.Normal)


                        RegularTextFieldWithPlaceholder(placeholderText = "Set title of the event", modifier = Modifier.fillMaxWidth().shadow(elevation = 12.dp),
                            value = eventTitleText, onValueChangeFun = { viewModel.SetEventTitle(it) }, isPasswordDisplay = false,
                            enabled = if(eventToEdit != null && eventToEdit.IsPrescribedEvent()) false else true
                        )

                    }
                }

            }

            var studentOne = viewModel.uiStudentOne.collectAsState()
            var studentTwo = viewModel.uiStudentTwo.collectAsState()
            var feedbackTarget = viewModel.uiFeedbackTarget.collectAsState()

            //Second part with large event notes input field
            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
                .padding(top = PaddingStatic.Small)) {

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = PaddingStatic.Tiny)) {

                    SmallAditionalInfoText(text = "Event description   for:", modifier = Modifier.weight(2f), textSize = FontSizeStatic.Tiny, textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Normal)

                    //list with target description info
                    Row(modifier = Modifier.weight(5f), horizontalArrangement = Arrangement.SpaceAround) {

                        Column(modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.SetFeedbackTarget(FeedbackTarget.Everyone)
                            }) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Everyone",
                                textAlign = TextAlign.Center,
                                fontSize = FontSizeStatic.Tiny,
                                color = Color.White,
                                style = TextShadowStatic.Small()
                            )

                            AnimatedVisibility(modifier = Modifier.zIndex(18f),visible = feedbackTarget.value == FeedbackTarget.Everyone, enter = scaleIn(
                                tween(180,
                                    easing = FastOutSlowInEasing
                                )
                            )) {
                                if(feedbackTarget.value == FeedbackTarget.Everyone) {
                                    Box(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(MaterialTheme.colors.primary))
                                }
                            }
                        }

                        Column(modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.SetFeedbackTarget(FeedbackTarget.StudentOne)
                            }) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = if(studentOne.value != null) studentOne.value!!.GetFirstNameAndInitialLastName() else "unknown",
                                textAlign = TextAlign.Center,
                                fontSize = FontSizeStatic.Tiny,
                                color = Color.White,
                                style = TextShadowStatic.Small()
                            )

                            AnimatedVisibility(modifier = Modifier.zIndex(18f),visible = feedbackTarget.value == FeedbackTarget.StudentOne, enter = scaleIn(
                                tween(180,
                                    easing = FastOutSlowInEasing
                                )
                            )) {
                                if (feedbackTarget.value == FeedbackTarget.StudentOne) {
                                    Box(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(MaterialTheme.colors.primary))
                                }
                            }

                        }

                        Column(modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.SetFeedbackTarget(FeedbackTarget.StudentTwo)
                            }) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = if(studentTwo.value != null) studentTwo.value!!.GetFirstNameAndInitialLastName() else "unknown",
                                textAlign = TextAlign.Center,
                                fontSize = FontSizeStatic.Tiny,
                                color = Color.White,
                                style = TextShadowStatic.Small()
                            )

                            AnimatedVisibility(modifier = Modifier.zIndex(18f),visible = feedbackTarget.value == FeedbackTarget.StudentTwo, enter = scaleIn(
                                tween(180,
                                    easing = FastOutSlowInEasing
                                )
                            )) {
                                if (feedbackTarget.value == FeedbackTarget.StudentTwo) {
                                    Box(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(MaterialTheme.colors.primary))
                                }
                            }
                        }

                    }
                }


                val eventDescriptionText = viewModel.inputEventDescription.collectAsState()

                var writeDescriptionForName = "description"

                if(studentOne.value != null && studentTwo.value != null) {
                    if (feedbackTarget.value == FeedbackTarget.Everyone) writeDescriptionForName = "for everyone"
                    else if (feedbackTarget.value == FeedbackTarget.StudentOne) writeDescriptionForName = "for " + studentOne.value!!.FullName()
                    else writeDescriptionForName = "for " + studentTwo.value!!.FullName()
                }


                RegularTextFieldWithPlaceholder(placeholderText = "Write the feedback event $writeDescriptionForName here", modifier = if(isAddNewEvent) Modifier.fillMaxSize().shadow(elevation = 12.dp) else Modifier
                    .fillMaxWidth().height(80.dp).shadow(elevation = 12.dp), value = eventDescriptionText, onValueChangeFun = { viewModel.SetEventDescription(it) },
                    isPasswordDisplay = false, enabled = true
                )

                if(isAddNewEvent == false) {
                    EditEventButtons(editEventHandler = popUpScreenHandler as EditEvent)
                }
            }
        }

        val iconPickerIsOpen = viewModel.uiIconPickerIsOpen.collectAsState()
        //icon picker pop up
        if(iconPickerIsOpen.value) {
            IconPickerPopUpComponent(viewModel = viewModel)
        }
        
        //are you sure you want to delete the current edit event
        if(isAddNewEvent == false) {
            var editEventHandler = popUpScreenHandler as EditEvent

            val currentTrainingHandler = CurrentTrainingHandler(currentContext = viewModel.context)
            val areYouSureInfo = editEventHandler.areYouSureInfo.collectAsState()
            val areYouSureScreenIsOpen = true

            if(areYouSureInfo.value != null) {
                AnimatedVisibility(modifier = Modifier.zIndex(18f),visible = areYouSureScreenIsOpen, enter = fadeIn(
                    tween(200,
                    easing = FastOutSlowInEasing
                    )
                )
                ) {
                    if (areYouSureScreenIsOpen) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .zIndex(18f), horizontalArrangement = Arrangement.Center
                        ) {
                            Column(
                                modifier = Modifier.fillMaxHeight(),
                                verticalArrangement = Arrangement.Center
                            ) {
                                AreYouSureContainer(areYouSureInfo = areYouSureInfo.value!!,
                                    cancelAction = { editEventHandler.ClearAreYouSureInfo() },
                                    confirmAction = {
                                        viewModel.viewModelScope.launch {
                                            viewModel.DeleteTrainingEvent(
                                                currentTrainingId = currentTrainingHandler.GetCurrentTrainingId(),
                                                authKey = currentTrainingHandler.GetAuthKey()
                                            )

                                            viewModel.TogglePopUpScreen()
                                        }
                                    }
                                )
                            }

                        }
                    }
                }
            }
        } // end if statement delete current event are you sure container functionalities

    } //end box


} //end function

@Composable
fun EditEventButtons(editEventHandler: EditEvent) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        RegularRectangleButton(buttonText = editEventHandler.confirmText, onClick = { editEventHandler.Confirm() }, modifier = Modifier.width(220.dp),
            fontSize = FontSizeStatic.Small, invertedColors = false)

        Spacer(Modifier.padding(PaddingStatic.Mini))

        RegularRectangleButton(buttonText = editEventHandler.cancelText, onClick = { editEventHandler.Cancel() }, modifier = Modifier.width(220.dp),
            fontSize = FontSizeStatic.Small, invertedColors = true)

        Spacer(Modifier.padding(PaddingStatic.Mini))


        Button(
            contentPadding = PaddingValues(1.dp),
            modifier = Modifier.width(220.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = NegativeActionColor),
            onClick = {
                //confirm delete event
                editEventHandler.areYouSureInfo.value = AreYouSureInfo(

                    popUptitle = "Delete feedback",
                    popUpexplanation =
                    buildAnnotatedString {
                        append("Are you sure you want to discard this feedback?\n")

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("This action can't be undone.")
                        }
                    },
                    confirmActionText = "Delete feedback",
                    cancelActionText = "Cancel",
                    width = 420.dp,
                    height = 220.dp,
                    isDiscardTraining = false,
                    isFinishTraining = false,
                    isDeleteUser = false,
                )
            })
        {
            Text(
                text = "Delete event",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = FontSizeStatic.Small
            )
        }
    }
}