package com.example.vref_solutions_tablet_application.screens.liveTraningScreen

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.models.popUpModels.EditEvent
import com.example.vref_solutions_tablet_application.models.TrainingEvent
import com.example.vref_solutions_tablet_application.viewModels.LiveTrainingViewModel
import com.example.vref_solutions_tablet_application.ui.theme.DarkerMainPurple
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.TextShadowStatic

@Composable
fun EventDisplayListItem(trainingEvent: TrainingEvent, viewModel: LiveTrainingViewModel) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        val cantEditAutomaticFeedbackText = stringResource(R.string.edit_automatic_feedback_restriction)
        Button(
            onClick = {
                if(!trainingEvent.isAutomaticFeedback()) {
                    viewModel.openPopUpScreenEditEvent(trainingEvent = trainingEvent,type = EditEvent())
                }
                else {
                    Toast.makeText(viewModel.getApplication<Application>().baseContext,"$cantEditAutomaticFeedbackText!", Toast.LENGTH_SHORT).show()
                }
            },
            contentPadding = PaddingValues(start = MaterialTheme.padding.tiny, end = MaterialTheme.padding.tiny),
            shape = RoundedCornerShape(38),
            colors = ButtonDefaults.buttonColors(backgroundColor = if(trainingEvent.isAutomaticFeedback()) DarkerMainPurple else MaterialTheme.colors.primary ),
            modifier = Modifier
                .height(24.dp)
                .weight(3f)

        ) {
            Text(
                text = if(trainingEvent.isAutomaticFeedback()) stringResource(R.string.generated) else stringResource(R.string.change),
                color = MaterialTheme.colors.onSurface ,
                fontSize = MaterialTheme.typography.h6.fontSize
            )
        }

        Spacer(Modifier.padding(MaterialTheme.padding.mini))

        Text(modifier = Modifier.weight(2f),text = trainingEvent.timeStamp.getHoursAndMinutesFormat(), color = Color.White, textAlign = TextAlign.Center,
            fontSize = MaterialTheme.typography.h5.fontSize, style = TextShadowStatic.Small())

        Spacer(Modifier.padding(MaterialTheme.padding.mini))

        Row(modifier = Modifier
            .fillMaxHeight()
            .weight(1f)) {
            Spacer(Modifier.weight(1f))

            Image(
                painter = painterResource(id = trainingEvent.getIconPainterId()),
                contentDescription = stringResource(R.string.cd_event_icon),
                modifier = Modifier
                    .size(MaterialTheme.iconSize.small)
                    .clip(
                        RoundedCornerShape(
                            if (trainingEvent.isPrescribedEvent()) MaterialTheme.roundedCornerShape.small else 0.dp
                        )
                    )
                    .background(if (trainingEvent.isPrescribedEvent()) Color.White else Color.Transparent)
                    .weight(2f)
            )

            Spacer(Modifier.weight(1f))
        }


        Spacer(Modifier.padding(MaterialTheme.padding.mini))

        Text(modifier = Modifier.weight(7f),text = trainingEvent.name, color = Color.White, fontSize = MaterialTheme.typography.h5.fontSize, style = TextShadowStatic.Small())
    }
}