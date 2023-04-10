package com.example.vref_solutions_tablet_application.components

import android.annotation.SuppressLint
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.vref_solutions_tablet_application.components.buttons.RegularRectangleButton
import com.example.vref_solutions_tablet_application.enums.PopUpType
import com.example.vref_solutions_tablet_application.handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.models.popUpModels.AreYouSureInfo
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.viewModels.AdminsViewModel
import com.example.vref_solutions_tablet_application.models.popUpModels.EditUser
import com.example.vref_solutions_tablet_application.models.popUpModels.NewOrganization
import com.example.vref_solutions_tablet_application.models.popUpModels.NewUser
import com.example.vref_solutions_tablet_application.ui.theme.NegativeActionColor
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBackground
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*



import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.TextShadowStatic

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun DynamicPopUpScreenAdmin(viewModel: AdminsViewModel) {
    val popUpScreenHandler = viewModel.uiPopUpScreenType.value
    val popUpScreenTitleDisplay = stringResource(id = popUpScreenHandler.title)

    val loggedInUserHandler = LoggedInUserHandler(currentContext = LocalContext.current)

    Card(
        elevation = 12.dp, modifier = Modifier
            .width(popUpScreenHandler.width)
            .height(popUpScreenHandler.height)
            .clip(RoundedCornerShape(MaterialTheme.roundedCornerShape.small)),
        backgroundColor = PopUpBackground
    ) {

        Column(modifier = Modifier.padding(MaterialTheme.padding.small)) {
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
                    fontSize = MaterialTheme.typography.h4.fontSize,
                    style = TextShadowStatic.Small()
                )
                Image(
                    painter = painterResource(id = R.drawable.x_circle_fill),
                    contentDescription = stringResource(R.string.cd_cross_icon),
                    modifier = Modifier
                        .size(MaterialTheme.iconSize.medium)
                        .clickable {
                            popUpScreenHandler.cancel()
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

                val deleteTrainingText = stringResource(R.string.delete_training)
                val areYouSureYouWantToDeleteUserText = "${stringResource(R.string.are_you_sure_you_want_to)} ${stringResource(R.string.delete)} ${stringResource(R.string.user)} "
                val actionCantBeReversedText = stringResource(R.string.this_action_cant_reversed)
                val deleteUserText = "${stringResource(R.string.delete)} ${stringResource(R.string.user)}"
                val cancelText = stringResource(R.string.cancel)

                Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.Bottom) {
                    if(deleteButtonIsActive) {
                        Column(modifier = Modifier.weight(2f), verticalArrangement = Arrangement.Bottom) {
                            Button(
                                contentPadding = PaddingValues(4.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(backgroundColor = NegativeActionColor),
                                onClick = {
                                    viewModel.setAndDisplayAreYouSureInfo(
                                        _areYouSureInfo = AreYouSureInfo(
                                            popUptitle = deleteTrainingText,
                                            popUpexplanation = buildAnnotatedString {
                                                append(areYouSureYouWantToDeleteUserText)
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("${viewModel.userToEdit.value.firstName} ${viewModel.userToEdit.value.lastName}?")
                                                }
                                                append("\n $actionCantBeReversedText!")
                                            },
                                            confirmActionText = deleteUserText,
                                            cancelActionText = cancelText,
                                            width = 420.dp,
                                            height = 240.dp,
                                            isDiscardTraining = false,
                                            isFinishTraining = false,
                                            isDeleteUser = true //important!
                                        )
                                    )

                                }) {
                                Text(
                                    text = deleteUserText,
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = MaterialTheme.typography.h5.fontSize
                                )
                            }
                        }


                    }

                    Spacer(Modifier.weight(2f))

                    Column(modifier = Modifier.weight(if(deleteButtonIsActive) 3f else 1f)) {

                        RegularRectangleButton(buttonText = stringResource(id = popUpScreenHandler.confirmText), onClick = { popUpScreenHandler.confirm() }, modifier = Modifier.fillMaxWidth(),
                            fontSize = MaterialTheme.typography.h5.fontSize, invertedColors = false)

                        RegularRectangleButton(buttonText = stringResource(id = popUpScreenHandler.cancelText), onClick = { popUpScreenHandler.cancel() }, modifier = Modifier.fillMaxWidth(),
                            fontSize = MaterialTheme.typography.h5.fontSize, invertedColors = true)

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
                                cancelAction = { viewModel.toggleAreYouSureScreen() },
                                confirmAction = {
                                    //delete user that is being edited
                                    viewModel.launchDeleteUser(authKey = loggedInUserHandler.getAuthKey())
                                }
                            )
                        }
                        else {
                            AreYouSureContainer(areYouSureInfo = areYouSureInfo.value!!,
                                cancelAction = { viewModel.toggleAreYouSureScreen() },
                                confirmAction = {
                                    popUpScreenHandler.confirm()
                                    viewModel.toggleAreYouSureScreen()
                                }
                            )
                        }
                    }

                }
            }
        }
    }
}
