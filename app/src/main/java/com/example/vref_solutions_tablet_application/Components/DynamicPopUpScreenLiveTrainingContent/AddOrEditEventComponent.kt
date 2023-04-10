package com.example.vref_solutions_tablet_application.components

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import com.example.vref_solutions_tablet_application.viewModels.LiveTrainingViewModel
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.vref_solutions_tablet_application.components.buttons.RegularRectangleButton
import com.example.vref_solutions_tablet_application.components.textField.RegularTextFieldWithPlaceholder
import com.example.vref_solutions_tablet_application.components.textUI.SmallAditionalInfoText
import com.example.vref_solutions_tablet_application.enums.FeedbackTarget
import com.example.vref_solutions_tablet_application.enums.PopUpType
import com.example.vref_solutions_tablet_application.handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.handlers.IconDisplayHandler
import com.example.vref_solutions_tablet_application.models.popUpModels.AreYouSureInfo
import com.example.vref_solutions_tablet_application.models.popUpModels.BasePopUpScreen
import com.example.vref_solutions_tablet_application.models.popUpModels.EditEvent
import com.example.vref_solutions_tablet_application.models.TrainingEvent
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ui.theme.*
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*

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
            .padding(MaterialTheme.padding.small)) {

            //Top part with icon select and event title set
            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {

                val changePrescribedEventIconRestrictionText = stringResource(R.string.change_icon_prescribed_event_restriction)
                Row(verticalAlignment = Alignment.Bottom) {
                    //big icon display
                    Column( modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(MaterialTheme.padding.mini)
                        .clickable {
                            if (popUpScreenHandler.type.equals(PopUpType.ADD_EVENT)) {
                                viewModel.toggleIconPickerPopUp(onCloseReset = false)
                            } else if (eventToEdit != null && !eventToEdit.isPrescribedEvent()) {
                                viewModel.toggleIconPickerPopUp(onCloseReset = false)
                            } else {
                                Toast
                                    .makeText(
                                        viewModel.getApplication<Application>().baseContext,
                                        changePrescribedEventIconRestrictionText,
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                            }
                        }
                        , horizontalAlignment = Alignment.CenterHorizontally) {

                        SmallAditionalInfoText(text = stringResource(R.string.icon), modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f), textSize = MaterialTheme.typography.h6.fontSize, textAlign = TextAlign.Left,
                            fontWeight = FontWeight.Normal)

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(3f)
                                .clip(RoundedCornerShape(MaterialTheme.roundedCornerShape.large))
                                .background(PopUpBoxBackground)
                            ,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                modifier = Modifier
                                    .size(MaterialTheme.iconSize.large)
                                    .clip(
                                        RoundedCornerShape(if (eventToEdit != null && eventToEdit.isPrescribedEvent()) MaterialTheme.roundedCornerShape.small else 0.dp)
                                    )
                                    .background(if (eventToEdit != null && eventToEdit.isPrescribedEvent()) Color.White else Color.Transparent)
                                ,
                                painter = painterResource(id =
                                if(currentlySelectedIcon.value == "") R.drawable.plus
                                else if(eventToEdit != null && eventToEdit.isPrescribedEvent()) eventToEdit.getIconPainterId()
                                else IconDisplayHandler.getPainterIdForSymbolName(currentlySelectedIcon.value)),
                                contentDescription = stringResource(R.string.selected_icon_example)
                            )
                        }

                    }

                    //event name input display
                    val eventTitleText = viewModel.inputEventTitle.collectAsState()

                    Column(modifier = Modifier
                        .weight(4f)
                        .padding(
                            start = MaterialTheme.padding.medium,
                            end = MaterialTheme.padding.small,
                            bottom = MaterialTheme.padding.tiny
                        ), verticalArrangement = Arrangement.Center) {

                        SmallAditionalInfoText(text = if(eventToEdit != null && eventToEdit.isPrescribedEvent()) stringResource(R.string.event_name_cant_be_changed) else stringResource(R.string.event_name),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = MaterialTheme.padding.tiny), textSize = MaterialTheme.typography.h6.fontSize, textAlign = TextAlign.Left, fontWeight = FontWeight.Normal)


                        RegularTextFieldWithPlaceholder(placeholderText = stringResource(R.string.event_set_title), modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 12.dp),
                            value = eventTitleText, onValueChangeFun = { viewModel.setEventTitle(it) }, isPasswordDisplay = false,
                            enabled = if(eventToEdit != null && eventToEdit.isPrescribedEvent()) false else true
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
                .padding(top = MaterialTheme.padding.small)) {

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.padding.tiny)) {

                    SmallAditionalInfoText(text = stringResource(R.string.event_description) + "   " + stringResource(R.string.for_event) + ":", modifier = Modifier.weight(2f), textSize = MaterialTheme.typography.h6.fontSize, textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Normal)

                    //list with target description info
                    Row(modifier = Modifier.weight(5f), horizontalArrangement = Arrangement.SpaceAround) {

                        Column(modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.setFeedbackTarget(FeedbackTarget.Everyone)
                            }) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(R.string.everyone),
                                textAlign = TextAlign.Center,
                                fontSize = MaterialTheme.typography.h6.fontSize,
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
                                viewModel.setFeedbackTarget(FeedbackTarget.StudentOne)
                            }) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = if(studentOne.value != null) studentOne.value!!.getFirstNameAndInitialLastName() else stringResource(R.string.unknown),
                                textAlign = TextAlign.Center,
                                fontSize = MaterialTheme.typography.h6.fontSize,
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
                                viewModel.setFeedbackTarget(FeedbackTarget.StudentTwo)
                            }) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = if(studentTwo.value != null) studentTwo.value!!.getFirstNameAndInitialLastName() else stringResource(R.string.unknown),
                                textAlign = TextAlign.Center,
                                fontSize = MaterialTheme.typography.h6.fontSize,
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

                var writeDescriptionForName = stringResource(R.string.description)

                if(studentOne.value != null && studentTwo.value != null) {
                    if (feedbackTarget.value == FeedbackTarget.Everyone) writeDescriptionForName = "${stringResource(R.string.for_event)} ${stringResource(R.string.everyone)}"
                    else if (feedbackTarget.value == FeedbackTarget.StudentOne) writeDescriptionForName = "${stringResource(R.string.for_event)} " + studentOne.value!!.fullName()
                    else writeDescriptionForName = "${stringResource(R.string.for_event)} " + studentTwo.value!!.fullName()
                }


                RegularTextFieldWithPlaceholder(placeholderText = stringResource(R.string.write_feedback_event) + " " + writeDescriptionForName + " " + stringResource(R.string.here), modifier = if(isAddNewEvent) Modifier
                    .fillMaxSize()
                    .shadow(elevation = 12.dp) else Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .shadow(elevation = 12.dp), value = eventDescriptionText, onValueChangeFun = { viewModel.setEventDescription(it) },
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

            val currentTrainingHandler = CurrentTrainingHandler(currentContext = viewModel.getApplication<Application>().baseContext)
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
                                    cancelAction = { editEventHandler.clearAreYouSureInfo() },
                                    confirmAction = {
                                        viewModel.launchDeleteTrainingEvent(currentTrainingHandler = currentTrainingHandler)
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

        RegularRectangleButton(buttonText = stringResource(id = editEventHandler.confirmText), onClick = { editEventHandler.confirm() }, modifier = Modifier.width(220.dp),
            fontSize = MaterialTheme.typography.h5.fontSize, invertedColors = false)

        Spacer(Modifier.padding(MaterialTheme.padding.mini))

        RegularRectangleButton(buttonText = stringResource(id = editEventHandler.cancelText), onClick = { editEventHandler.cancel() }, modifier = Modifier.width(220.dp),
            fontSize = MaterialTheme.typography.h5.fontSize, invertedColors = true)

        Spacer(Modifier.padding(MaterialTheme.padding.mini))

        val confirmActionText = stringResource(R.string.delete_feedback)
        val deleteEventText = stringResource(R.string.delete_event)
        val cancelText = stringResource(R.string.cancel)
        val areYouSureDiscardFeedbackText = stringResource(R.string.are_you_sure_discard_feedback)
        val actionCantBeUndoneText = stringResource(R.string.action_cant_be_undone)
        Button(
            contentPadding = PaddingValues(1.dp),
            modifier = Modifier.width(220.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = NegativeActionColor),
            onClick = {
                //confirm delete event
                editEventHandler.areYouSureInfo.value = AreYouSureInfo(

                    popUptitle = confirmActionText,
                    popUpexplanation =
                    buildAnnotatedString {
                        append(areYouSureDiscardFeedbackText + "\n")

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("$actionCantBeUndoneText.")
                        }
                    },
                    confirmActionText = confirmActionText,
                    cancelActionText = cancelText,
                    width = 420.dp,
                    height = 220.dp,
                    isDiscardTraining = false,
                    isFinishTraining = false,
                    isDeleteUser = false,
                )
            })
        {
            Text(
                text = deleteEventText,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.h5.fontSize
            )
        }
    }
}