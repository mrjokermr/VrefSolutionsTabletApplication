package com.example.vref_solutions_tablet_application.Components

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewModelScope
import com.example.vref_solutions_tablet_application.Components.Buttons.RegularRectangleButton
import com.example.vref_solutions_tablet_application.Enums.PopUpType
import com.example.vref_solutions_tablet_application.Handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.Models.PopUpModels.AreYouSureInfo
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ViewModels.AdminsViewModel
import com.example.vref_solutions_tablet_application.Models.PopUpModels.EditUser
import com.example.vref_solutions_tablet_application.Models.PopUpModels.NewOrganization
import com.example.vref_solutions_tablet_application.Models.PopUpModels.NewUser
import com.example.vref_solutions_tablet_application.StylingClasses.*
import com.example.vref_solutions_tablet_application.ui.theme.NegativeActionColor
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBackground
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun DynamicPopUpScreenAdmin(viewModel: AdminsViewModel) {
    val popUpScreenHandler = viewModel.uiPopUpScreenType.value
    val popUpScreenTitleDisplay = popUpScreenHandler.title

    val loggedInUserHandler = LoggedInUserHandler(currentContext = LocalContext.current)

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
                    .weight(2f),
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
                    .weight(7f)
            ) {
                when(popUpScreenHandler.type) {
                    PopUpType.EDIT_USER -> EditUserComponent(editUserHandler = popUpScreenHandler as EditUser, viewModel = viewModel)
                    PopUpType.NEW_USER -> NewUserComponent(newUserHandler = popUpScreenHandler as NewUser, viewModel = viewModel)
                    PopUpType.NEW_ORGANIZATION -> NewOrganizationComponent(newOrganizationHandler = popUpScreenHandler as NewOrganization, viewModel = viewModel)
                }
            }

            //Cancel and confirm button(s)
            if(!popUpScreenHandler.type.equals(PopUpType.EXIT_TRAINING)) {
                val deleteButtonIsActive = popUpScreenHandler.type.equals(PopUpType.EDIT_USER)

                Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.Bottom) {
                    if(deleteButtonIsActive) {
                        Column(modifier = Modifier.weight(2f), verticalArrangement = Arrangement.Bottom) {
                            Button(
                                contentPadding = PaddingValues(4.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(backgroundColor = NegativeActionColor),
                                onClick = {
                                    viewModel.SetAndDisplayAreYouSureInfo(
                                        _areYouSureInfo = AreYouSureInfo(
                                            popUptitle = "Delete training",
                                            popUpexplanation = buildAnnotatedString {
                                                append("Are you sure you want to delete user ")
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("${viewModel.userToEdit.value.firstName} ${viewModel.userToEdit.value.lastName}?")
                                                }
                                                append("\n This action can't be reversed!")
                                            },
                                            confirmActionText = "Delete user",
                                            cancelActionText = "Cancel",
                                            width = 420.dp,
                                            height = 240.dp,
                                            isDiscardTraining = false,
                                            isFinishTraining = false,
                                            isDeleteUser = true //important!
                                        )
                                    )

                                }) {
                                Text(
                                    text = "Delete user",
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = FontSizeStatic.Small
                                )
                            }
                        }


                    }

                    Spacer(Modifier.weight(2f))

                    Column(modifier = Modifier.weight(if(deleteButtonIsActive) 3f else 1f)) {

                        RegularRectangleButton(buttonText = popUpScreenHandler.confirmText, onClick = { popUpScreenHandler.Confirm() }, modifier = Modifier.fillMaxWidth(),
                            fontSize = FontSizeStatic.Small, invertedColors = false)

                        RegularRectangleButton(buttonText = popUpScreenHandler.cancelText, onClick = { popUpScreenHandler.Cancel() }, modifier = Modifier.fillMaxWidth(),
                            fontSize = FontSizeStatic.Small, invertedColors = true)

                    }
                } // end column

            }
        }
        //end content

        var areYouSureScreenIsOpen = viewModel.uiAreYouSureScreenIsOpen.collectAsState()
        var areYouSureInfo = viewModel.uiAreYouSureInfo.collectAsState()
        //are you sure placement
        AnimatedVisibility(modifier = Modifier.zIndex(14f),visible = areYouSureScreenIsOpen.value, enter = fadeIn(
            tween(200,
            easing = FastOutSlowInEasing
            )
        )
        ) {
            if (areYouSureScreenIsOpen.value && areYouSureInfo.value != null) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(14f), horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        if(areYouSureInfo.value!!.isDeleteUser) {
                            AreYouSureContainer(areYouSureInfo = areYouSureInfo.value!!,
                                cancelAction = { viewModel.ToggleAreYouSureScreen() },
                                confirmAction = {
                                    //delete user that is being edited
                                    viewModel.viewModelScope.launch {
                                        viewModel.DeleteUser(
                                            context = viewModel.getApplication<Application>().baseContext,
                                            authKey = loggedInUserHandler.GetAuthKey()
                                        )

                                        viewModel.ToggleAreYouSureScreen()
                                        viewModel.ToggleDynamicPopUpScreen()
                                    }
                                }
                            )
                        }
                        else {
                            AreYouSureContainer(areYouSureInfo = areYouSureInfo.value!!,
                                cancelAction = { viewModel.ToggleAreYouSureScreen() },
                                confirmAction = {
                                    popUpScreenHandler.Confirm()
                                    viewModel.ToggleAreYouSureScreen()
                                }
                            )
                        }
                    }

                }
            }
        }
    }
}
