package com.example.vref_solutions_tablet_application.screens

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.components.buttons.CurrentEventsListFilterButton
import com.example.vref_solutions_tablet_application.components.buttons.FilterDateButton
import com.example.vref_solutions_tablet_application.components.buttons.NewTrainingButton
import com.example.vref_solutions_tablet_application.components.DynamicLoadingDisplay
import com.example.vref_solutions_tablet_application.components.TopBarComp
import com.example.vref_solutions_tablet_application.components.textUI.SmallAditionalInfoText
import com.example.vref_solutions_tablet_application.handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.screens.mainMenuScreen.DisplayListItemTrainingSummary
import com.example.vref_solutions_tablet_application.screens.mainMenuScreen.ExpandableTrainingEventListItem
import com.example.vref_solutions_tablet_application.screens.mainMenuScreen.WelcomeMessageAndAdminButton
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.TextShadowStatic
import com.example.vref_solutions_tablet_application.viewModels.MainMenuViewModel
import java.util.*

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MainMenu(viewModel: MainMenuViewModel = viewModel(), navController: NavController) {

    val currentContext = LocalContext.current
    viewModel.navController = navController

    //currenttraining handler created at top level otherwise for each item a individual training handler would be created
    val currentTrainingHandler = CurrentTrainingHandler(currentContext = currentContext)
    val loggedInUserHandler: LoggedInUserHandler = LoggedInUserHandler(currentContext = currentContext)

    viewModel.launchGetAllTrainingSummaries()

    val selectedTrainingSum = viewModel.uiCurrentlySelectedTraining.collectAsState()
    Scaffold(
        topBar = { TopBarComp(ScreenNavName.MainMenu, navController = navController, hideLogOutButton = false) },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            if(selectedTrainingSum.value == null) NewTrainingButton(viewModel)
        } ,
    ) {
        Row() {

            //LEFT SIDE
            Column(modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(topEnd = MaterialTheme.roundedCornerShape.medium))
                .fillMaxHeight()
                .shadow(elevation = 20.dp)
                .background(MaterialTheme.colors.onBackground),
            ) {
                Spacer(Modifier.padding(MaterialTheme.padding.small))

                Text(
                    text =
                    if(loggedInUserHandler.userIsSuperAdmin() || loggedInUserHandler.userIsAdmin()) stringResource(R.string.training_sessions_company)
                    else stringResource(R.string.your_training_session),

                    fontSize = MaterialTheme.typography.h3.fontSize, color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(start = MaterialTheme.padding.tiny),style = TextShadowStatic.Medium()
                )

                Spacer(Modifier.padding(MaterialTheme.padding.tiny))

                //Column with the date filters
                Column(modifier = Modifier
                    .padding(start = MaterialTheme.padding.small)
                    .fillMaxWidth()) {

                    SmallAditionalInfoText(text = stringResource(R.string.filter_by_date), modifier = Modifier.fillMaxWidth(), textSize = MaterialTheme.typography.h6.fontSize, textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Normal)

                    Spacer(Modifier.padding(MaterialTheme.padding.mini))

                    Row() {
                        FilterDateButton(dateDisplay = viewModel.uiFromDateDisplay, viewModel = viewModel, context = currentContext)

                        Spacer(Modifier.padding(MaterialTheme.padding.tiny))

                        FilterDateButton(dateDisplay = viewModel.uiToDateDisplay, viewModel = viewModel, context = currentContext) //to: button
                    }
                }

                Spacer(Modifier.padding(MaterialTheme.padding.mini))

                val allTrainingSummaries = viewModel.uiAllTrainingSummaries.collectAsState()

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    if(allTrainingSummaries.value != null) {

                        if(allTrainingSummaries.value!!.isNotEmpty()) {
                            items(allTrainingSummaries.value!!.size) {
                                val currTrainingSum = allTrainingSummaries.value!![it]
                                //als de current training niet viewable is dan mag de user niet een student zijn
                                if(currTrainingSum.isViewable() == false && loggedInUserHandler.userIsInstructor()) {
                                    DisplayListItemTrainingSummary(trainingSummary = currTrainingSum,viewModel = viewModel, currentTrainingHandler = currentTrainingHandler)
                                    Spacer(Modifier.padding(MaterialTheme.padding.mini))
                                }
                                else if(currTrainingSum.isViewable()) {
                                    DisplayListItemTrainingSummary(trainingSummary = currTrainingSum,viewModel = viewModel, currentTrainingHandler = currentTrainingHandler)
                                    Spacer(Modifier.padding(MaterialTheme.padding.mini))
                                }

                            }
                        }
                        else {
                            item() {
                                Column(Modifier.padding(MaterialTheme.padding.medium)) {
                                    Text(modifier = Modifier.fillMaxWidth(),text = "${stringResource(R.string.no_training_participation)}.",
                                        color = MaterialTheme.colors.primaryVariant, fontSize = MaterialTheme.typography.h4.fontSize, textAlign = TextAlign.Center)
                                }
                            }

                        }

                    }
                    else {

                        item() {
                            Column(Modifier.padding(MaterialTheme.padding.medium)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                    DynamicLoadingDisplay(
                                        loadingText = stringResource(R.string.trainings_are_loading),
                                        iconSize = MaterialTheme.iconSize.large,
                                        iconTint = MaterialTheme.colors.primaryVariant,
                                        textColor = MaterialTheme.colors.primaryVariant,
                                    )
                                }

                                Spacer(Modifier.padding(MaterialTheme.padding.large))

                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = MaterialTheme.padding.medium), horizontalArrangement = Arrangement.Center) {
                                    Button(
                                        onClick = {
                                            viewModel.launchGetAllTrainingSummaries()
                                        },
                                        contentPadding = PaddingValues(start = MaterialTheme.padding.tiny, end = MaterialTheme.padding.tiny),
                                        shape = RoundedCornerShape(38),
                                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary ),
                                        modifier = Modifier
                                            .height(24.dp)
                                            .width(304.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.click_to_reload_trainings),
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

            //RIGHT SIDE
            Box(modifier = Modifier
                .weight(1f)
                .fillMaxHeight()) {

                val currentlySelectedTrainingToView = viewModel.uiCurrentlySelectedTraining.collectAsState()
                val allCurrentTrainingEvents = viewModel.uiAllTrainingEventsForSelectedTraining.collectAsState()

                if(currentlySelectedTrainingToView.value == null && allCurrentTrainingEvents.value == null ) {
                    WelcomeMessageAndAdminButton(viewModel = viewModel, loggedInUserHandler = loggedInUserHandler)
                }
                else { //currently selected training is not null
                    //display all info from the training
                    if(currentlySelectedTrainingToView.value != null) {
                        Column() {

                            //filters for the training events should be added here
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .height(108.dp), verticalArrangement = Arrangement.Bottom) {

                                Row() {
                                    Spacer(Modifier.padding(MaterialTheme.padding.tiny))

                                    SmallAditionalInfoText(text = stringResource(R.string.filter_events), modifier = Modifier.fillMaxWidth(), textSize = MaterialTheme.typography.h6.fontSize, textAlign = TextAlign.Left,
                                        fontWeight = FontWeight.Normal)

                                }

                                Spacer(Modifier.padding(MaterialTheme.padding.mini))

                                Row() {
                                    Spacer(Modifier.padding(MaterialTheme.padding.tiny))

                                    CurrentEventsListFilterButton(
                                        initialFilterValue = true, //filter values are by default true
                                        buttonText = stringResource(R.string.instructors_feedback),
                                        onClickAction = {
                                            viewModel.toggleInstructorFeedbackEventFilter()
                                            viewModel.updateCurrentEventsWithFilter(trainingId = currentlySelectedTrainingToView.value!!.id, authKey = currentTrainingHandler.getAuthKey())
                                        }
                                    )

                                    Spacer(Modifier.padding(MaterialTheme.padding.tiny))

                                    CurrentEventsListFilterButton(
                                        initialFilterValue = true, //filter values are by default true
                                        buttonText = stringResource(R.string.automatic_feedback),
                                        onClickAction = {
                                            viewModel.toggleAutomaticFeedbackEventFilter()
                                            viewModel.updateCurrentEventsWithFilter(trainingId = currentlySelectedTrainingToView.value!!.id, authKey = currentTrainingHandler.getAuthKey())
                                        }
                                    )
                                }

                                Spacer(Modifier.padding(MaterialTheme.padding.tiny))

                                val instructorFeedbackFilterFlow = viewModel.getInstructorFeedbackFilterFlow().collectAsState()

                                //false means that the instructor feedback is not being removed from the feedback list
                                //so show the deeper functionalities of the filter option for instructor feedback
                                Row(modifier = Modifier.fillMaxWidth().height(24.dp)) {

                                    if(instructorFeedbackFilterFlow.value == false) {
                                        Spacer(Modifier.padding(MaterialTheme.padding.tiny))

                                        CurrentEventsListFilterButton(
                                            initialFilterValue = true, //filter values are by default true
                                            buttonText = stringResource(R.string.manual),
                                            onClickAction = {
                                                viewModel.toggleManualInstructorFeedbackFilter()
                                                viewModel.updateCurrentEventsWithFilter(trainingId = currentlySelectedTrainingToView.value!!.id, authKey = currentTrainingHandler.getAuthKey())
                                            }
                                        )

                                        Spacer(Modifier.padding(MaterialTheme.padding.tiny))

                                        CurrentEventsListFilterButton(
                                            initialFilterValue = true, //filter values are by default true
                                            buttonText = stringResource(R.string.quick),
                                            onClickAction = {
                                                viewModel.toggleQuickInstructorFeedbackFilter()
                                                viewModel.updateCurrentEventsWithFilter(trainingId = currentlySelectedTrainingToView.value!!.id, authKey = currentTrainingHandler.getAuthKey())
                                            }
                                        )

                                        Spacer(Modifier.padding(MaterialTheme.padding.tiny))

                                        CurrentEventsListFilterButton(
                                            initialFilterValue = true, //filter values are by default true
                                            buttonText = stringResource(R.string.prescribed),
                                            onClickAction = {
                                                viewModel.togglePrescribedEventsFeedbackFilter()
                                                viewModel.updateCurrentEventsWithFilter(trainingId = currentlySelectedTrainingToView.value!!.id, authKey = currentTrainingHandler.getAuthKey())
                                            }
                                        )
                                    }
                                }

                                Spacer(Modifier.padding(MaterialTheme.padding.tiny))
                            }

                            if(allCurrentTrainingEvents.value != null && currentlySelectedTrainingToView.value != null) {
                                if(allCurrentTrainingEvents.value!!.size > 0) {
                                    LazyColumn(modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colors.onBackground)) {

                                        items(allCurrentTrainingEvents.value!!.size) {
                                            if(it == 0) Spacer(Modifier.padding(MaterialTheme.padding.small)) //because padding top = MaterialTheme.padding.small wasn't working

                                            if(currentlySelectedTrainingToView.value != null && allCurrentTrainingEvents.value != null) { //previous checks would still be ignored
                                                ExpandableTrainingEventListItem(trainingEvent = allCurrentTrainingEvents.value!![it], trainingSummary = currentlySelectedTrainingToView.value!!)
                                            }

                                            Spacer(Modifier.padding(MaterialTheme.padding.tiny))
                                        }
                                    }
                                }
                                else {
                                    Spacer(Modifier.padding(MaterialTheme.padding.medium))

                                    Text(modifier = Modifier.fillMaxWidth(),
                                        text = "${stringResource(R.string.no_event_for_this_training)} \n${stringResource(R.string.for_this_training_yet)}.",
                                        color = Color.White,
                                        fontSize = MaterialTheme.typography.h4.fontSize,
                                        textAlign = TextAlign.Center)
                                }
                            }
                            else {
                                DynamicLoadingDisplay(
                                    loadingText = stringResource(R.string.the_events_loading),
                                    iconSize = MaterialTheme.iconSize.large,
                                    textColor = Color.White,
                                    iconTint = Color.White
                                )
                            }

                        }
                    }
                    else {
                        WelcomeMessageAndAdminButton(viewModel = viewModel, loggedInUserHandler = loggedInUserHandler)
                    }

                } //end first if else null check

            }

        }
    }
}