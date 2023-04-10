package com.example.vref_solutions_tablet_application.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.handlers.PrescribedEventsHandler
import com.example.vref_solutions_tablet_application.models.popUpModels.PrescribedEventsDetails
import com.example.vref_solutions_tablet_application.models.PrescribedEvent
import com.example.vref_solutions_tablet_application.viewModels.LiveTrainingViewModel
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBoxDarkBackground
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*


@Composable
fun PrescribedEventsDetailsComponent(viewModel: LiveTrainingViewModel, prescribedEventsDetailsVm: PrescribedEventsDetails) {

    val allPrescribedEvents = PrescribedEventsHandler.allPescribedEvents
    LazyColumn() {
        items(allPrescribedEvents.size) {
            PrescribedEventDetailColumnItem(prescribedEvent = allPrescribedEvents[it])

            Spacer(Modifier.padding(MaterialTheme.padding.tiny))
        }
    }
}

@Composable
fun PrescribedEventDetailColumnItem(prescribedEvent: PrescribedEvent) {
    Row(modifier = Modifier.clip(RoundedCornerShape(MaterialTheme.roundedCornerShape.small)).background(PopUpBoxDarkBackground).padding(
        MaterialTheme.padding.tiny), verticalAlignment = Alignment.CenterVertically) {

        Image(
            painter = painterResource(id = prescribedEvent.drawableId),
            contentDescription = stringResource(R.string.cd_event_icon),
            modifier = Modifier.size(MaterialTheme.iconSize.extraLarge).weight(1f).clip(RoundedCornerShape(
                MaterialTheme.roundedCornerShape.medium)).background(Color.White),
        )

        Spacer(Modifier.padding(MaterialTheme.padding.small))

        Column(modifier = Modifier.weight(7f).fillMaxWidth()) {

            Text(text = prescribedEvent.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.h5.fontSize)
            Text(text = prescribedEvent.message, color = Color.White, fontSize = MaterialTheme.typography.h6.fontSize)

        }
    }

}