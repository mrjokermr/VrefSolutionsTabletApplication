package com.example.vref_solutions_tablet_application.screens.liveTraningScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.handlers.TimelineDisplayHandler
import com.example.vref_solutions_tablet_application.models.TimelineEventsDisplayDataItem
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.TextShadowStatic

@Composable
fun TimelineItemDisplay(item: TimelineEventsDisplayDataItem, isMostRecentTimelineItem: Boolean) {
    BoxWithConstraints(modifier = Modifier
        .width(TimelineDisplayHandler.spacePerDisplayBlock)
        .fillMaxHeight()) {
        Column() {

            Column(
                Modifier.height(40.dp), horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween) {

                for (i in 0..2) {
                    Image(
                        painter = painterResource(id = R.drawable.line_top_down),
                        contentDescription = stringResource(R.string.cd_line_icon),
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier
                            .height(if (isMostRecentTimelineItem) 12.dp else 9.dp)
                            .width(24.dp)
                    )

                    if(!isMostRecentTimelineItem) Spacer(Modifier.padding(1.dp))
                }


            }

            Column(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()) {
                Text(text = if(!isMostRecentTimelineItem) item.fromCustomTimestamp.getHoursMinutesAndSecondsFormat() else stringResource(R.string.now),
                    color = Color.Gray, fontSize = MaterialTheme.typography.subtitle1.fontSize, fontWeight = FontWeight.Bold,
                    textDecoration = if(isMostRecentTimelineItem) TextDecoration.Underline else TextDecoration.None)
            }


        }

        Row(modifier = Modifier
            .padding(top = 3.dp)
            .fillMaxSize(), horizontalArrangement = Arrangement.Center) {

            val thisItemTrainingEventCount = item.trainingEventsInThisBlock.size

            for(i in 0.. item.trainingEventsInThisBlock.size - 1) {
                if(i > 1) {
                    //can't display more events within the timeline space to showcase a number of remaining events within this timeperiod
                    Text(text = "+${thisItemTrainingEventCount - 2}", color = Color.White, style = TextShadowStatic.Small(), fontSize = MaterialTheme.typography.subtitle1.fontSize)
                    break
                }
                else {
                    TrainingEventInTimeLineDisplay(item.trainingEventsInThisBlock[i])
                }
            }

//            for(trainingEvent in item.trainingEventsInThisBlock) {
//
//                TrainingEventInTimeLineDisplay(trainingEvent)
//            }
        }
    }
}