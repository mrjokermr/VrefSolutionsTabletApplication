package com.example.vref_solutions_tablet_application.screens.liveTraningScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.handlers.TimelineDisplayHandler
import com.example.vref_solutions_tablet_application.models.TrainingEvent
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.TextShadowStatic

@Composable
fun TrainingEventInTimeLineDisplay(trainingEvent: TrainingEvent) {
    Column(
        Modifier.width(TimelineDisplayHandler.spacePerTimeLineItem)
        , horizontalAlignment = Alignment.CenterHorizontally) {

        Image(
            painter = painterResource(id = trainingEvent.getIconPainterId()),
            contentDescription = stringResource(R.string.cd_event_icon),
            modifier = Modifier
                .size(MaterialTheme.iconSize.tiny)
                .clip(RoundedCornerShape(if (trainingEvent.isPrescribedEvent()) MaterialTheme.roundedCornerShape.small else 0.dp))
                .background(if (trainingEvent.isPrescribedEvent()) Color.White else Color.Transparent)
        )

        Spacer(Modifier.padding(2.dp))

        Text(text = trainingEvent.timeStamp.getMinutesAndSecondsFormat(), color = Color.White, fontSize = 8.sp, style = TextShadowStatic.Small())
    }

}