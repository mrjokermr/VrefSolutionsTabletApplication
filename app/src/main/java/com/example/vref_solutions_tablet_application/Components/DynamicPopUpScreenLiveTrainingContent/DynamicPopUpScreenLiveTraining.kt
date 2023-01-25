package com.example.vref_solutions_tablet_application.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.Components.Buttons.RegularRectangleButton
import com.example.vref_solutions_tablet_application.Enums.PopUpType
import com.example.vref_solutions_tablet_application.Models.PopUpModels.*
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.StylingClasses.*
import com.example.vref_solutions_tablet_application.ViewModels.LiveTrainingViewModel
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBackground


@Composable
fun DyanmicPopUpScreenLiveTraining(popUpScreenHandler: BasePopUpScreen, viewModel: LiveTrainingViewModel) {

    var popUpScreenTitleDisplay: String = popUpScreenHandler.title

    if (popUpScreenHandler.type.equals(PopUpType.SWITCH_CAMERA_1)) popUpScreenTitleDisplay += " 1"
    else if (popUpScreenHandler.type.equals(PopUpType.SWITCH_CAMERA_2)) popUpScreenTitleDisplay += " 2"

    Card(
        elevation = 12.dp, modifier = Modifier
            .width(popUpScreenHandler.width)
            .height(popUpScreenHandler.height)
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
                    text = popUpScreenTitleDisplay,
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
                            popUpScreenHandler.Cancel()
                        }
                )
            }

            //Content:
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(
                        if (popUpScreenHandler.type.equals(PopUpType.EXIT_TRAINING)) 7f
                        else if (popUpScreenHandler.type.equals(PopUpType.EDIT_EVENT)) 10f
                        else 9f
                    )
            ) {
                //Based on the pop up screen type we decide what has to be put inside content part of this dynamic screen
                if (popUpScreenHandler.type.equals(PopUpType.ADD_EVENT) || popUpScreenHandler.type.equals(PopUpType.EDIT_EVENT) ) {
                    var isAddNewEvent = false

                    if(popUpScreenHandler.type.equals(PopUpType.ADD_EVENT)) {
                        val popUpHandler = popUpScreenHandler as AddEvent
                        popUpHandler.viewModel = viewModel //must be declared otherwise the cancel and confirm won't work
                        isAddNewEvent = true
                    }
                    else {
                        val popUpHandler = popUpScreenHandler as EditEvent
                        popUpHandler.viewModel = viewModel //must be declared otherwise the cancel and confirm won't work
                    }

                    //uses the same screen because editing and creating a user uses the same fields
                    AddOrEditEventComponent(
                        viewModel = viewModel,
                        isAddNewEvent = isAddNewEvent,
                        popUpScreenHandler = popUpScreenHandler
                    )
                } else if (popUpScreenHandler.type.equals(PopUpType.SWITCH_CAMERA_1)) {
                    val switchCameraHandler: SwitchCamera = popUpScreenHandler as SwitchCamera
                    switchCameraHandler.SetSelectedCameraObject(viewModel.GetLargeVideoPlayerActiveCameraLinkObject())

                    SwitchCameraPopUpComponent(
                        targetCamera = 1,
                        cameraSwitchHandler = switchCameraHandler
                    )
                } else if (popUpScreenHandler.type.equals(PopUpType.SWITCH_CAMERA_2)) {
                    val switchCameraHandler: SwitchCamera = popUpScreenHandler as SwitchCamera
                    switchCameraHandler.SetSelectedCameraObject(viewModel.GetSmallVideoPlayerActiveCameraLinkObject())

                    SwitchCameraPopUpComponent(
                        targetCamera = 2,
                        cameraSwitchHandler = switchCameraHandler
                    )
                }
                else if( popUpScreenHandler.type.equals(PopUpType.PRESCRIBED_EVENT_INFO)) {
                    val prescribedEventHandler: PrescribedEventsDetails = popUpScreenHandler as PrescribedEventsDetails
                    prescribedEventHandler.viewModel = viewModel

                    PrescribedEventsDetailsComponent(viewModel = viewModel, prescribedEventsDetailsVm = prescribedEventHandler)
                }
                else if (popUpScreenHandler.type.equals(PopUpType.EXIT_TRAINING)) {
                    ExitTraingPopUpcomponent(exitTrainingHandler = popUpScreenHandler as ExitTraining,viewModel = viewModel)
                }
            }

            //Cancel and confirm button(s)
            //exit training, edit event have their own implementation for the cancel exit buttons
            //and prescribed event info doesn't have confirm or cancel buttons only the X button
            if(!popUpScreenHandler.type.equals(PopUpType.EXIT_TRAINING) && !popUpScreenHandler.type.equals(PopUpType.EDIT_EVENT) &&
                    !popUpScreenHandler.type.equals(PopUpType.PRESCRIBED_EVENT_INFO)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(
                            if (popUpScreenHandler.type.equals(PopUpType.EXIT_TRAINING) || popUpScreenHandler.type.equals(PopUpType.EDIT_EVENT)) 0.0001f //for some reason 0f without the or statement would work but with the extra or statement it wouldn't  so used 0.0001f
                            else 1f
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    RegularRectangleButton(buttonText = popUpScreenHandler.cancelText, onClick = { popUpScreenHandler.Cancel() }, modifier = Modifier.weight(1f),
                        fontSize = FontSizeStatic.Small, invertedColors = true)

                    Spacer(Modifier.fillMaxWidth().weight(if (popUpScreenHandler.type.equals(PopUpType.EXIT_TRAINING)) 1f else 2f))

                    RegularRectangleButton(buttonText = popUpScreenHandler.confirmText, onClick = { popUpScreenHandler.Confirm() }, modifier = Modifier.weight(1f),
                        fontSize = FontSizeStatic.Small, invertedColors = false)

                } // end row
            }

        } // end column

    } // end card

}