package com.example.vref_solutions_tablet_application.screens.liveTraningScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.vref_solutions_tablet_application.components.textUI.SmallAditionalInfoText
import com.example.vref_solutions_tablet_application.enums.FeedbackTarget
import com.example.vref_solutions_tablet_application.handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.handlers.PrescribedEventsHandler
import com.example.vref_solutions_tablet_application.models.popUpModels.PrescribedEventsDetails
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.viewModels.LiveTrainingViewModel
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBoxDarkBackground
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*



import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.TextShadowStatic

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PrescribedEventsAndTimelineUI(viewModel: LiveTrainingViewModel, currentTrainingHandler: CurrentTrainingHandler) {
    //hier
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(MaterialTheme.roundedCornerShape.medium))
        .background(MaterialTheme.colors.onBackground)
        .padding(MaterialTheme.padding.small)) {

        item() {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(R.string.prescribed_events), color = Color.Gray, fontSize = MaterialTheme.typography.h6.fontSize, style = TextShadowStatic.Small())

                    Spacer(Modifier.padding(MaterialTheme.padding.mini))

                    Box(
                        modifier = Modifier
                            .size(MaterialTheme.iconSize.small)
                            .clip(RoundedCornerShape(100))
                            .background(Color.White)
                            .clickable {
                                viewModel.openPopUpScreen(PrescribedEventsDetails())
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.info_circle_fill),
                            colorFilter = ColorFilter.tint(Color(0xFF990FEE)),
                            contentDescription = stringResource(R.string.cd_event_icon),
                            modifier = Modifier.size(MaterialTheme.iconSize.small),
                        )
                    }
                }

                Spacer(Modifier.padding(MaterialTheme.padding.mini))

                var studentOne = viewModel.uiStudentOne.collectAsState()
                var studentTwo = viewModel.uiStudentTwo.collectAsState()
                var feedbackTarget = viewModel.uiFeedbackTarget.collectAsState()

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

                    SmallAditionalInfoText(text = "${stringResource(R.string.for_event)}:", modifier = Modifier.weight(2f), textSize = MaterialTheme.typography.h6.fontSize, textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Normal)

                    //list with feedback-target description info
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

                            AnimatedVisibility(
                                modifier = Modifier.zIndex(18f),
                                visible = feedbackTarget.value == FeedbackTarget.Everyone,
                                enter = scaleIn(
                                    tween(
                                        180,
                                        easing = FastOutSlowInEasing
                                    )
                                )
                            ) {
                                if (feedbackTarget.value == FeedbackTarget.Everyone) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(2.dp)
                                            .background(MaterialTheme.colors.primary)
                                    )
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
                                text = if (studentOne.value != null) studentOne.value!!.getFirstNameAndInitialLastName() else stringResource(R.string.unknown),
                                textAlign = TextAlign.Center,
                                fontSize = MaterialTheme.typography.h6.fontSize,
                                color = Color.White,
                                style = TextShadowStatic.Small()
                            )

                            AnimatedVisibility(modifier = Modifier.zIndex(18f), visible = feedbackTarget.value == FeedbackTarget.StudentOne,
                                enter = scaleIn(
                                    tween(
                                        180,
                                        easing = FastOutSlowInEasing
                                    )
                                )
                            ) {
                                if (feedbackTarget.value == FeedbackTarget.StudentOne) {
                                    Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(
                                        MaterialTheme.colors.primary))
                                }
                            }

                        }

                        Column(modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.setFeedbackTarget(FeedbackTarget.StudentTwo)
                            }) {
                            Text(
                                modifier = Modifier.fillMaxWidth(), text = if (studentTwo.value != null) studentTwo.value!!.getFirstNameAndInitialLastName() else stringResource(R.string.unknown),
                                textAlign = TextAlign.Center, fontSize = MaterialTheme.typography.h6.fontSize, color = Color.White, style = TextShadowStatic.Small()
                            )

                            AnimatedVisibility(
                                modifier = Modifier.zIndex(18f),
                                visible = feedbackTarget.value == FeedbackTarget.StudentTwo,
                                enter = scaleIn(
                                    tween(
                                        180,
                                        easing = FastOutSlowInEasing
                                    )
                                )
                            ) {
                                if (feedbackTarget.value == FeedbackTarget.StudentTwo) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().height(2.dp).background(
                                            MaterialTheme.colors.primary)
                                    )
                                }
                            }
                        }
                    }
                } //end row with the feedback target selection

                Spacer(Modifier.padding(MaterialTheme.padding.mini))

                LazyRow(modifier = Modifier.fillMaxWidth().height(56.dp).clip(
                    RoundedCornerShape(
                        MaterialTheme.roundedCornerShape.small)
                ).background(PopUpBoxDarkBackground)
                    .padding(MaterialTheme.padding.mini), verticalAlignment = Alignment.CenterVertically) {
                    val allPrescribedEvents = PrescribedEventsHandler.allPescribedEvents

                    items(allPrescribedEvents.size) {
                        var prescribedEvent = allPrescribedEvents[it]

                        Box(modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(MaterialTheme.roundedCornerShape.small))
                            .background(Color.White)
                            .clickable {
                                viewModel.newPrescribedEvent(
                                    prescribedEvent = prescribedEvent,
                                    currentTrainingId = currentTrainingHandler.getCurrentTrainingId(),
                                    authKey = currentTrainingHandler.getAuthKey()
                                )
                            }) {
                            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Image(
                                    modifier = Modifier.fillMaxSize(),
                                    painter = painterResource(id = prescribedEvent.drawableId),
                                    contentDescription = "${stringResource(R.string.prescribed_event_icon)} ${prescribedEvent.title}"
                                )


                            }
                        }

                        Spacer(Modifier.padding(MaterialTheme.padding.small))
                    }
                }

                Spacer(Modifier.padding(MaterialTheme.padding.mini))

                TimelineUiDisplay(viewModel = viewModel, currentTrainingHandler = currentTrainingHandler)
            }
        }
    }
}