package com.example.vref_solutions_tablet_application.screens.mainMenuScreen

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.models.TrainingEvent
import com.example.vref_solutions_tablet_application.models.TrainingSummary
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*




@SuppressLint("UnrememberedMutableState")
@Composable
fun ExpandableTrainingEventListItem(trainingEvent: TrainingEvent, trainingSummary: TrainingSummary) {
    var isExpanded by remember { mutableStateOf(false) }

    // Opening Animation
    val expandTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(300)
        ) + fadeIn(
            animationSpec = tween(300)
        )
    }

    // Closing Animation
    val collapseTransition = remember {
        shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(300)
        ) + fadeOut(
            animationSpec = tween(300)
        )
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        //Not expanded part
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                isExpanded = !isExpanded
            }, verticalAlignment = Alignment.CenterVertically) {

            Column(Modifier.weight(2f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = trainingEvent.getIconPainterId()),
                    contentDescription = stringResource(R.string.cd_event_icon),
                    modifier = Modifier
                        .size(MaterialTheme.iconSize.medium)
                        .clip(RoundedCornerShape(if(trainingEvent.isPrescribedEvent()) MaterialTheme.roundedCornerShape.small else 0.dp))

                        .background(if(trainingEvent.isPrescribedEvent()) Color.White else Color.Transparent)
                )
            }


            Row(modifier = Modifier.weight(7f), verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = Modifier.widthIn(max = 300.dp) ,text = trainingEvent.name, color = Color.White, fontSize = MaterialTheme.typography.h4.fontSize)

                Image(
                    painter = painterResource(id = R.drawable.dash_lg),
                    contentDescription = stringResource(R.string.cd_line_icon),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    contentScale = ContentScale.FillBounds
                )
            }

            Text(modifier = Modifier.weight(2f), text = trainingEvent.timeStamp.getHoursAndMinutesFormat(), color = Color.White, fontSize = MaterialTheme.typography.h4.fontSize
                , textAlign = TextAlign.Center)

            Image(
                painter = painterResource(id = if(isExpanded) R.drawable.chevron_up else R.drawable.chevron_down),
                contentDescription = stringResource(R.string.cd_chevron_icon),
                modifier = Modifier
                    .size(MaterialTheme.iconSize.small)
                    .weight(1f)
            )
        }


        //Animated expandable part
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandTransition,
            exit = collapseTransition
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = MaterialTheme.padding.extraLarge,
                    top = MaterialTheme.padding.tiny,
                    end = MaterialTheme.padding.large,
                    bottom = MaterialTheme.padding.tiny
                )
            ) {
                //these parameters don't have to be translated they are not displayed in the UI and have a different purpose
                if(trainingEvent.devidedFeedbackContainer.everyone.isNotBlank()) {
                    FeedbackDetail("Everyone",trainingEvent.devidedFeedbackContainer.everyone)
                }

                if(trainingEvent.devidedFeedbackContainer.studentOne.isNotBlank()) {
                    var studentOneName = "StudentOne"
                    if(trainingSummary.students.size > 0) studentOneName = trainingSummary.students[0].fullName()
                    FeedbackDetail(studentOneName,trainingEvent.devidedFeedbackContainer.studentOne)
                }

                if(trainingEvent.devidedFeedbackContainer.studentTwo.isNotBlank()) {
                    var studentTwoName = "StudentTwo"
                    if(trainingSummary.students.size > 1) studentTwoName = trainingSummary.students[1].fullName()
                    FeedbackDetail(studentTwoName,trainingEvent.devidedFeedbackContainer.studentTwo)
                }

            }
        }

    }
}