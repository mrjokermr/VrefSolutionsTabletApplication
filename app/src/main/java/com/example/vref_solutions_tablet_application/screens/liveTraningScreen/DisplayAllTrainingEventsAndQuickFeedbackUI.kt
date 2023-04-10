package com.example.vref_solutions_tablet_application.screens.liveTraningScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.components.DynamicLoadingDisplay
import com.example.vref_solutions_tablet_application.handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.TextShadowStatic
import com.example.vref_solutions_tablet_application.viewModels.LiveTrainingViewModel

@Composable
fun DisplayAllTrainingEventsAndQuickFeedbackUI(viewModel: LiveTrainingViewModel, currentTrainingInfoHandler: CurrentTrainingHandler) {
    var allTrainingEvents = viewModel.uiAllTrainingEvents.collectAsState()

    //row with quick feedback text and add text
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(modifier = Modifier.weight(5f),text = stringResource(R.string.quick_feedback), textDecoration = TextDecoration.Underline, color = Color.Gray,
            fontSize = MaterialTheme.typography.h5.fontSize, style = TextShadowStatic.Small())
        Text(modifier = Modifier
            .weight(1f)
            .fillMaxWidth(), textAlign = TextAlign.Center,text = stringResource(R.string.add), textDecoration = TextDecoration.Underline, color = Color.Gray,
            fontSize = MaterialTheme.typography.h5.fontSize, style = TextShadowStatic.Small())
    }

    Spacer(Modifier.padding(MaterialTheme.padding.mini))

    var inputFieldQuickFeedback = viewModel.inputQuickFeedback.collectAsState()

    //row with quick feedback input field and add quick feedback button
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(54.dp), verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.SpaceBetween) {
        TextField(
            modifier = Modifier
                .weight(5f)
                .height(54.dp)
                .clip(RoundedCornerShape(MaterialTheme.roundedCornerShape.medium))
                .padding(0.dp),
            value = inputFieldQuickFeedback.value,
            singleLine = true,
            onValueChange = { viewModel.setQuickFeedback(input = it) },
            placeholder = { Text("${stringResource(R.string.write_quick_feedback_here)}...") },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                disabledTextColor = Color.White,
                placeholderColor = Color.Gray,
                textColor = Color.Black,
            ),
            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.h5.fontSize,
            ),
        )

        var menuIsOpen = viewModel.uiOptionMenuIsOpen.collectAsState()
        Column(modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally ,
            verticalArrangement = Arrangement.Center) {
            //add quick feedback
            Button(contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(100),
                modifier = Modifier.size(40.dp),
                colors = ButtonDefaults.buttonColors(
                    disabledBackgroundColor = Color(0xFF856699),
                ),
                enabled = !menuIsOpen.value,
                onClick = {
                    //create quick feedback
                    viewModel.launchNewQuickTrainingEvent(currentTrainingId = currentTrainingInfoHandler.getCurrentTrainingId(),
                        message = inputFieldQuickFeedback.value,
                        authKey = currentTrainingInfoHandler.getAuthKey())
                }) {
                Image(
                    modifier = Modifier.size(MaterialTheme.iconSize.small),
                    painter = painterResource(id = R.drawable.plus),
                    contentDescription = stringResource(R.string.cd_plus_icon)
                )
            }
        }
    }

    Spacer(Modifier.padding(MaterialTheme.padding.tiny))

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        if(allTrainingEvents.value != null) {
            if(allTrainingEvents.value!!.size > 0) {
                items(allTrainingEvents.value!!.size) {
                    EventDisplayListItem(trainingEvent = allTrainingEvents.value!![it], viewModel = viewModel)

                    Spacer(Modifier.padding(MaterialTheme.padding.mini))
                }
            }
            else {
                item() {
                    Text(modifier = Modifier.fillMaxWidth(),text = "${stringResource(R.string.no_event_made)} \n${stringResource(R.string.for_this_training_yet)}.", color = Color.White,
                        fontSize = MaterialTheme.typography.h5.fontSize,
                        textAlign = TextAlign.Center)
                }
            }
        }
        else {
            item() {
                Column() {
                    DynamicLoadingDisplay(loadingText = stringResource(R.string.loading_events), iconSize = MaterialTheme.iconSize.medium, iconTint = Color.White, textColor = Color.White)

                    Spacer(Modifier.padding(MaterialTheme.padding.mini))

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.padding.medium), horizontalArrangement = Arrangement.Center) {
                        Button(
                            onClick = {
                                viewModel.launchGetAllTrainingEvents(trainingId = currentTrainingInfoHandler.getCurrentTrainingId(), authKey = currentTrainingInfoHandler.getAuthKey())
                            },
                            contentPadding = PaddingValues(start = MaterialTheme.padding.tiny, end = MaterialTheme.padding.tiny),
                            shape = RoundedCornerShape(38),
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary ),
                            modifier = Modifier
                                .height(24.dp)
                                .width(204.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.manual_reload_events),
                                color = MaterialTheme.colors.onSurface ,
                                fontSize = MaterialTheme.typography.h6.fontSize
                            )
                        }
                    }

                }

            }
        }
    }
}