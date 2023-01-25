package com.example.vref_solutions_tablet_application.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.vref_solutions_tablet_application.Handlers.PrescribedEventsHandler
import com.example.vref_solutions_tablet_application.Models.PopUpModels.PrescribedEventsDetails
import com.example.vref_solutions_tablet_application.Models.PrescribedEvent
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.StylingClasses.FontSizeStatic
import com.example.vref_solutions_tablet_application.StylingClasses.IconSizeStatic
import com.example.vref_solutions_tablet_application.StylingClasses.PaddingStatic
import com.example.vref_solutions_tablet_application.StylingClasses.RoundedSizeStatic
import com.example.vref_solutions_tablet_application.ViewModels.LiveTrainingViewModel
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBoxDarkBackground


@Composable
fun PrescribedEventsDetailsComponent(viewModel: LiveTrainingViewModel, prescribedEventsDetailsVm: PrescribedEventsDetails) {

    val allPrescribedEvents = PrescribedEventsHandler.allPescribedEvents
    LazyColumn() {
        items(allPrescribedEvents.size) {
            PrescribedEventDetailColumnItem(prescribedEvent = allPrescribedEvents[it])

            Spacer(Modifier.padding(PaddingStatic.Tiny))
        }
    }
}

@Composable
fun PrescribedEventDetailColumnItem(prescribedEvent: PrescribedEvent) {
    Row(modifier = Modifier.clip(RoundedCornerShape(RoundedSizeStatic.Small)).background(PopUpBoxDarkBackground).padding(PaddingStatic.Tiny), verticalAlignment = Alignment.CenterVertically) {

        Image(
            painter = painterResource(id = prescribedEvent.drawableId),
            contentDescription = "event_icon",
            modifier = Modifier.size(IconSizeStatic.ExtraLarge).weight(1f).clip(RoundedCornerShape(RoundedSizeStatic.Medium)).background(Color.White),
        )

        Spacer(Modifier.padding(PaddingStatic.Small))

        Column(modifier = Modifier.weight(7f).fillMaxWidth()) {

            Text(text = prescribedEvent.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = FontSizeStatic.Small)
            Text(text = prescribedEvent.message, color = Color.White, fontSize = FontSizeStatic.Tiny)

        }
    }

}