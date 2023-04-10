package com.example.vref_solutions_tablet_application.screens.liveTraningScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.handlers.TimelineDisplayHandler
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.TextShadowStatic
import com.example.vref_solutions_tablet_application.viewModels.LiveTrainingViewModel
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBoxDarkBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun TimelineUiDisplay(viewModel: LiveTrainingViewModel, currentTrainingHandler: CurrentTrainingHandler) {

    Row() {
        Text(text = "${stringResource(R.string.timeline)} ", color = Color.Gray, fontSize = MaterialTheme.typography.h6.fontSize, style = TextShadowStatic.Small())

        Text(text = "*${stringResource(R.string.per)} ${TimelineDisplayHandler.secondsPerBlock/60} ${stringResource(R.string.minutes)}", color = Color.Gray,modifier = Modifier.padding(bottom = 4.dp) , fontSize = MaterialTheme.typography.subtitle1.fontSize, style = TextShadowStatic.Small())

    }
    if(viewModel.localDateTimesAreWithinGivenHoursDifference(LocalDateTime.now(),currentTrainingHandler.getCurrentTrainingDateAsDateTimeObject(),12)) {
        var allTrainingTimeLineDisplayItems = viewModel.uiAllTrainingTimelineDisplayItems.collectAsState()
        val listState = rememberLazyListState()

        // a CoroutineScope to be able to launch
        val coroutineScope = rememberCoroutineScope()

        LazyRow(state = listState,modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(MaterialTheme.roundedCornerShape.small))
            .background(PopUpBoxDarkBackground)
            .padding(MaterialTheme.padding.mini),
            verticalAlignment = Alignment.CenterVertically) {

            if(allTrainingTimeLineDisplayItems.value != null) {

                items(allTrainingTimeLineDisplayItems.value!!.size) {

                    TimelineItemDisplay(item = allTrainingTimeLineDisplayItems.value!![it], isMostRecentTimelineItem = it == allTrainingTimeLineDisplayItems.value!!.size - 1)

                }
            }
        }

        Spacer(Modifier.padding(MaterialTheme.padding.mini))

        //row with the buttons to navigate to the start or end of the list
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if(allTrainingTimeLineDisplayItems.value != null) {

                Button(
                    onClick = {
                        coroutineScope.launch {
                            listState.scrollToItem(index = 0)
                        }
                    },
                    contentPadding = PaddingValues(start = MaterialTheme.padding.tiny, end = MaterialTheme.padding.tiny),
                    shape = RoundedCornerShape(38),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary ),
                    modifier = Modifier.height(18.dp)

                ) {
                    Text(
                        text = "${stringResource(R.string.jump_to)} ${stringResource(R.string.start)}",
                        color = MaterialTheme.colors.onSurface ,
                        fontSize = MaterialTheme.typography.subtitle1.fontSize
                    )
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            if(allTrainingTimeLineDisplayItems.value!!.size > 5) listState.scrollToItem(index = allTrainingTimeLineDisplayItems.value!!.lastIndex - 3)
                        }
                    },
                    contentPadding = PaddingValues(start = MaterialTheme.padding.tiny, end = MaterialTheme.padding.tiny),
                    shape = RoundedCornerShape(38),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary ),
                    modifier = Modifier.height(18.dp)

                ) {
                    Text(
                        text = "${stringResource(R.string.jump_to)} ${stringResource(R.string.now)}",
                        color = MaterialTheme.colors.onSurface ,
                        fontSize = MaterialTheme.typography.subtitle1.fontSize
                    )
                }

            }

        }

        //start the auto refresher of the allevents list which will automatically update the timeline
        LaunchedEffect(Unit) {
            while(true) {
                viewModel.getAllTrainingEvents(trainingId = currentTrainingHandler.getCurrentTrainingId(), authKey = currentTrainingHandler.getAuthKey())
                delay(TimelineDisplayHandler.secondsPerBlock.toLong())

            }
        }
    }
    else {
        Row(modifier = Modifier.fillMaxWidth().height(64.dp).clip(
            RoundedCornerShape(
                MaterialTheme.roundedCornerShape.small)
        ).background(PopUpBoxDarkBackground).padding(MaterialTheme.padding.tiny),
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = "${stringResource(R.string.no_timeline_explanation)}.",color = Color.White,
                fontSize = MaterialTheme.typography.h6.fontSize, style = TextShadowStatic.Small(), fontWeight = FontWeight.Bold)
        }

    }




    Spacer(Modifier.padding(MaterialTheme.padding.tiny))
}