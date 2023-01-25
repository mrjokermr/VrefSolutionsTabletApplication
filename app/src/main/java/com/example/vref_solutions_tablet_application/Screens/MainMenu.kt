package com.example.vref_solutions_tablet_application.Screens

import android.annotation.SuppressLint
import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vref_solutions_tablet_application.Components.Buttons.CurrentEventsListFilterButton
import com.example.vref_solutions_tablet_application.Components.DynamicLoadingDisplay
import com.example.vref_solutions_tablet_application.Components.TopBarComp
import com.example.vref_solutions_tablet_application.Components.`Text-UI`.SmallAditionalInfoText
import com.example.vref_solutions_tablet_application.Handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.Handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.Models.DateSelectAndDisplayObject
import com.example.vref_solutions_tablet_application.Models.TrainingEvent
import com.example.vref_solutions_tablet_application.Models.TrainingSummary
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.StylingClasses.*
import com.example.vref_solutions_tablet_application.ViewModels.MainMenuViewModel
import com.example.vref_solutions_tablet_application.ui.theme.NegativeActionColor
import com.example.vref_solutions_tablet_application.ui.theme.PositiveActionColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MainMenu(viewModel: MainMenuViewModel = viewModel(), navController: NavController) {

    val currentContext = LocalContext.current
    viewModel.navController = navController


    //currenttraining handler created at top level otherwise for each item a individual training handler would be created
    val currentTrainingHandler = CurrentTrainingHandler(currentContext = currentContext)
    val loggedInUserHandler: LoggedInUserHandler = LoggedInUserHandler(currentContext = currentContext)

    //currentTrainingHandler.ResetCurrentTrainingInfo() //otherwise when continueing training can cause a bug that finishing the training wont do anything

    viewModel.viewModelScope.launch {
        viewModel.GetAllTrainingSummaries(currentContext, filterByDateAfter = true)
    }

    val selectedTrainingSum = viewModel.uiCurrentlySelectedTraining.collectAsState()
    Scaffold(
        topBar = { TopBarComp(ScreenNavName.MainMenu, navController = navController, hideLogOutButton = false) },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            if(selectedTrainingSum.value == null) NewTrainingButton(viewModel)
        } ,
//        modifier = Modifier.padding(PaddingStatic.Small)
    ) {
        Row() {

            //LEFT SIDE
            Column(modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(topEnd = RoundedSizeStatic.Medium))
                .fillMaxHeight()
                .shadow(elevation = 20.dp)
                .background(MaterialTheme.colors.onBackground),
                //.shadow(elevation = 20.dp, ambientColor = Color.Black, spotColor = Color.Transparent)
            ) {
                Spacer(Modifier.padding(PaddingStatic.Small))

                Text(
                    text =
                    if(loggedInUserHandler.UserIsSuperAdmin() || loggedInUserHandler.UserIsAdmin()) "Training sessions within your company"
                    else "Your training session",

                    fontSize = FontSizeStatic.Large, color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(start = PaddingStatic.Tiny),style = TextShadowStatic.Medium()
                )

                Spacer(Modifier.padding(PaddingStatic.Tiny))

                //Column with the date filters
                Column(modifier = Modifier
                    .padding(start = PaddingStatic.Small)
                    .fillMaxWidth()) {

                    SmallAditionalInfoText(text = "Filter by date", modifier = Modifier.fillMaxWidth(), textSize = FontSizeStatic.Tiny, textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Normal)

                    Spacer(Modifier.padding(PaddingStatic.Mini))

                    Row() {
                        FilterDateButton(dateDisplay = viewModel.uiFromDateDisplay, viewModel = viewModel, context = currentContext)

                        Spacer(Modifier.padding(PaddingStatic.Tiny))

                        FilterDateButton(dateDisplay = viewModel.uiToDateDisplay, viewModel = viewModel, context = currentContext) //to: button
                    }
                }

                Spacer(Modifier.padding(PaddingStatic.Mini))

                val allTrainingSummaries = viewModel.uiAllTrainingSummaries.collectAsState()

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    if(allTrainingSummaries.value != null) {

                        if(allTrainingSummaries.value!!.isNotEmpty()) {
                            items(allTrainingSummaries.value!!.size) {
                                val currTrainingSum = allTrainingSummaries.value!![it]
                                //als de current training niet viewable is dan mag de user niet een student zijn
                                if(currTrainingSum.IsViewable() == false && loggedInUserHandler.UserIsInstructor()) {
                                    DisplayListItemTrainingSummary(trainingSummary = currTrainingSum,viewModel = viewModel, currentTrainingHandler = currentTrainingHandler)
                                    Spacer(Modifier.padding(PaddingStatic.Mini))
                                }
                                else if(currTrainingSum.IsViewable()) {
                                    DisplayListItemTrainingSummary(trainingSummary = currTrainingSum,viewModel = viewModel, currentTrainingHandler = currentTrainingHandler)
                                    Spacer(Modifier.padding(PaddingStatic.Mini))
                                }

                            }
                        }
                        else {
                            item() {
                                Column(Modifier.padding(PaddingStatic.Medium)) {
                                    Text(modifier = Modifier.fillMaxWidth(),text = "You have not participated in a training yet.",
                                        color = MaterialTheme.colors.primaryVariant, fontSize = FontSizeStatic.Normal, textAlign = TextAlign.Center)
                                }
                            }

                        }

                    }
                    else {

                        item() {
                            Column(Modifier.padding(PaddingStatic.Medium)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                    DynamicLoadingDisplay(
                                        loadingText = "Your trainings are being loaded",
                                        iconSize = IconSizeStatic.Large,
                                        iconTint = MaterialTheme.colors.primaryVariant,
                                        textColor = MaterialTheme.colors.primaryVariant,
                                    )
                                }

                                Spacer(Modifier.padding(PaddingStatic.Large))

                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = PaddingStatic.Medium), horizontalArrangement = Arrangement.Center) {
                                    Button(
                                        onClick = {
                                            viewModel.viewModelScope.launch {
                                                viewModel.GetAllTrainingSummaries(context = viewModel.getApplication<Application>().baseContext, filterByDateAfter = true)
                                            }
                                        },
                                        contentPadding = PaddingValues(start = PaddingStatic.Tiny, end = PaddingStatic.Tiny),
                                        shape = RoundedCornerShape(38),
                                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary ),
                                        modifier = Modifier
                                            .height(24.dp)
                                            .width(304.dp)
                                    ) {
                                        Text(
                                            text = "Click here to reload the trainings",
                                            color = MaterialTheme.colors.onSurface ,
                                            fontSize = FontSizeStatic.Tiny
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
                                    Spacer(Modifier.padding(PaddingStatic.Tiny))

                                    SmallAditionalInfoText(text = "Filter current events", modifier = Modifier.fillMaxWidth(), textSize = FontSizeStatic.Tiny, textAlign = TextAlign.Left,
                                        fontWeight = FontWeight.Normal)

                                }

                                Spacer(Modifier.padding(PaddingStatic.Mini))

                                Row() {
                                    Spacer(Modifier.padding(PaddingStatic.Tiny))

                                    CurrentEventsListFilterButton(
                                        initialFilterValue = true, //filter values are by default true
                                        buttonText = "Instructors Feedback",
                                        onClickAction = {
                                            viewModel.ToggleInstructorFeedbackEventFilter()
                                            viewModel.UpdateCurrentEventsWithFilter(trainingId = currentlySelectedTrainingToView.value!!.id, authKey = currentTrainingHandler.GetAuthKey())
                                        }
                                    )

                                    Spacer(Modifier.padding(PaddingStatic.Tiny))

                                    CurrentEventsListFilterButton(
                                        initialFilterValue = true, //filter values are by default true
                                        buttonText = "Automatic feedback",
                                        onClickAction = {
                                            viewModel.ToggleAutomaticFeedbackEventFilter()
                                            viewModel.UpdateCurrentEventsWithFilter(trainingId = currentlySelectedTrainingToView.value!!.id, authKey = currentTrainingHandler.GetAuthKey())
                                        }
                                    )
                                }

                                Spacer(Modifier.padding(PaddingStatic.Tiny))


                                val instructorFeedbackFilterFlow = viewModel.GetInstructorFeedbackFilterFlow().collectAsState()

                                //false means that the instructor feedback is not being removed from the feedback list
                                //so show the deeper functionalities of the filter option for instructor feedback
                                Row(modifier = Modifier.fillMaxWidth().height(24.dp)) {

                                    if(instructorFeedbackFilterFlow.value == false) {
                                        Spacer(Modifier.padding(PaddingStatic.Tiny))

                                        CurrentEventsListFilterButton(
                                            initialFilterValue = true, //filter values are by default true
                                            buttonText = "Manual",
                                            onClickAction = {
                                                viewModel.ToggleManualInstructorFeedbackFilter()
                                                viewModel.UpdateCurrentEventsWithFilter(trainingId = currentlySelectedTrainingToView.value!!.id, authKey = currentTrainingHandler.GetAuthKey())
                                            }
                                        )

                                        Spacer(Modifier.padding(PaddingStatic.Tiny))

                                        CurrentEventsListFilterButton(
                                            initialFilterValue = true, //filter values are by default true
                                            buttonText = "Quick",
                                            onClickAction = {
                                                viewModel.ToggleQuickInstructorFeedbackFilter()
                                                viewModel.UpdateCurrentEventsWithFilter(trainingId = currentlySelectedTrainingToView.value!!.id, authKey = currentTrainingHandler.GetAuthKey())
                                            }
                                        )

                                        Spacer(Modifier.padding(PaddingStatic.Tiny))

                                        CurrentEventsListFilterButton(
                                            initialFilterValue = true, //filter values are by default true
                                            buttonText = "Prescribed",
                                            onClickAction = {
                                                viewModel.TogglePrescribedEventsFeedbackFilter()
                                                viewModel.UpdateCurrentEventsWithFilter(trainingId = currentlySelectedTrainingToView.value!!.id, authKey = currentTrainingHandler.GetAuthKey())
                                            }
                                        )
                                    }
                                }

                                Spacer(Modifier.padding(PaddingStatic.Tiny))
                            }

                            if(allCurrentTrainingEvents.value != null && currentlySelectedTrainingToView.value != null) {
                                if(allCurrentTrainingEvents.value!!.size > 0) {
                                    LazyColumn(modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colors.onBackground)) {

                                        items(allCurrentTrainingEvents.value!!.size) {
                                            if(it == 0) Spacer(Modifier.padding(PaddingStatic.Small)) //because padding top = PaddingStatic.Small wasn't working

                                            if(currentlySelectedTrainingToView.value != null && allCurrentTrainingEvents.value != null) { //previous checks would still be ignored
                                                ExpandableTrainingEventListItem(trainingEvent = allCurrentTrainingEvents.value!![it], trainingSummary = currentlySelectedTrainingToView.value!!)
                                            }

                                            Spacer(Modifier.padding(PaddingStatic.Tiny))
                                        }
                                    }
                                }
                                else {
                                    Spacer(Modifier.padding(PaddingStatic.Medium))

                                    Text(modifier = Modifier.fillMaxWidth(),
                                        text = "There is no feedback for this training session \nMake sure your filter settings are correct.",
                                        color = Color.White,
                                        fontSize = FontSizeStatic.Normal,
                                        textAlign = TextAlign.Center)
                                }
                            }
                            else {
                                DynamicLoadingDisplay(
                                    loadingText = "The training events are being loaded",
                                    iconSize = IconSizeStatic.Large,
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

@Composable
fun FilterDateButton(dateDisplay: StateFlow<DateSelectAndDisplayObject>, viewModel: MainMenuViewModel, context: Context) {
    val dateDisplayState = dateDisplay.collectAsState()
    var displayDate by remember { mutableStateOf(dateDisplayState.value.displayDate) }
    var displayDay by remember { mutableStateOf(dateDisplayState.value.displayDay) }


    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            //updated the remember values otherwise it wouldn't update its values
            dateDisplayState.value.SetDateDisplay(LocalDate.of(mYear, mMonth + 1, mDayOfMonth))
            displayDate = dateDisplayState.value.displayDate
            displayDay = dateDisplayState.value.displayDay
            //sort the list by date
            viewModel.viewModelScope.launch {
                viewModel.GetAllTrainingSummaries(context = context, filterByDateAfter = true)
            }
        }, dateDisplayState.value.date.year, dateDisplayState.value.date.monthValue - 1, dateDisplayState.value.date.dayOfMonth
    )

    dateDisplayState.value.SetDateDisplay(dateDisplayState.value.date)

    Button(
        contentPadding = PaddingValues(4.dp),
        elevation = elevation(0.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(RoundedSizeStatic.Small))
            .background(MaterialTheme.colors.primary)
            .height(28.dp)
            .width(160.dp),
        onClick = {
            mDatePickerDialog.show()
        }) {

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = Modifier.weight(2f),text = "${dateDisplayState.value.displayText}:", color = Color.White, fontWeight = FontWeight.Bold,
                fontSize = FontSizeStatic.Tiny)

            Text(modifier = Modifier.weight(3f),text = "${displayDate}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = FontSizeStatic.Tiny)

            Box(modifier = Modifier.weight(1f)) {
                Image(
                    painter = painterResource(id = R.drawable.calendar_fill),
                    contentDescription = "calender_icon",
                    modifier = Modifier.size(IconSizeStatic.Small)
                )

                //MaterialTheme.colors.primary
                Text(text = "${dateDisplayState.value.displayDay}",color = MaterialTheme.colors.primary, fontWeight = FontWeight.Bold ,fontSize = FontSizeStatic.Mini,
                    modifier = Modifier.padding(top = 5.dp, start = 3.dp))

            }
        }
    }
}

@Composable
fun WelcomeMessageAndAdminButton(viewModel: MainMenuViewModel, loggedInUserHandler: LoggedInUserHandler) {
    if(loggedInUserHandler.UserIsAdmin() || loggedInUserHandler.UserIsSuperAdmin()) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = PaddingStatic.Small),horizontalArrangement = Arrangement.End) {
            Button(contentPadding = PaddingValues(4.dp),
                shape = RoundedCornerShape(100),
                modifier = Modifier
                    .size(80.dp)
                    .shadow(4.dp, RoundedCornerShape(100)),
                onClick = { viewModel.NavigateToPage(ScreenNavName.AdminMenu) }) {
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.padding(top = PaddingStatic.Mini))

                    Image(
                        modifier = Modifier.size(IconSizeStatic.Medium),
                        painter = painterResource(id = R.drawable.tools),
                        contentDescription = "admin tools icon"
                    )

                    Text(text = "Admin panel", color = Color.White, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
            }

            Spacer(Modifier.padding(end = PaddingStatic.Small))
        }
    }



    Text(text = "Welcome \n${loggedInUserHandler.GetFirstNameCurrentUser()}", textAlign = TextAlign.Center, fontSize = FontSizeStatic.MediumTitle,
        color = MaterialTheme.colors.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = PaddingStatic.ExtraLarge),
        style = TextShadowStatic.Medium()
    )
}

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
                    painter = painterResource(id = trainingEvent.GetIconPainterId()),
                    contentDescription = "event_icon",
                    modifier = Modifier
                        .size(IconSizeStatic.Medium)
                        .clip(RoundedCornerShape(if(trainingEvent.IsPrescribedEvent()) RoundedSizeStatic.Small else 0.dp))

                        .background(if(trainingEvent.IsPrescribedEvent()) Color.White else Color.Transparent)
                )
            }


            Row(modifier = Modifier.weight(7f), verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = Modifier.widthIn(max = 300.dp) ,text = trainingEvent.name, color = Color.White, fontSize = FontSizeStatic.Normal)

                Image(
                    painter = painterResource(id = R.drawable.dash_lg),
                    contentDescription = "line_icon",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    contentScale = ContentScale.FillBounds
                )
            }

            Text(modifier = Modifier.weight(2f), text = trainingEvent.timeStamp.GetHoursAndMinutesFormat(), color = Color.White, fontSize = FontSizeStatic.Normal
                , textAlign = TextAlign.Center)

            Image(
                painter = painterResource(id = if(isExpanded) R.drawable.chevron_up else R.drawable.chevron_down),
                contentDescription = "chevron_down_or_up_icon",
                modifier = Modifier
                    .size(IconSizeStatic.Small)
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
                    start = PaddingStatic.ExtraLarge,
                    top = PaddingStatic.Tiny,
                    end = PaddingStatic.Large,
                    bottom = PaddingStatic.Tiny
                )
            ) {
                if(trainingEvent.devidedFeedbackContainer.everyone.isNotBlank()) {
                    FeedbackDetail("Everyone",trainingEvent.devidedFeedbackContainer.everyone)
                }

                if(trainingEvent.devidedFeedbackContainer.studentOne.isNotBlank()) {
                    var studentOneName = "StudentOne"
                    if(trainingSummary.students.size > 0) studentOneName = trainingSummary.students[0].FullName()
                    FeedbackDetail(studentOneName,trainingEvent.devidedFeedbackContainer.studentOne)
                }

                if(trainingEvent.devidedFeedbackContainer.studentTwo.isNotBlank()) {
                    var studentTwoName = "StudentTwo"
                    if(trainingSummary.students.size > 1) studentTwoName = trainingSummary.students[1].FullName()
                    FeedbackDetail(studentTwoName,trainingEvent.devidedFeedbackContainer.studentTwo)
                }

//                Text(text = trainingEvent.message, color = Color.White, fontSize = FontSizeStatic.Small)
            }
        }

    }
}

@Composable
fun FeedbackDetail(targetTitle: String, content: String) {
    val density: Density = LocalDensity.current
    var textWidth by remember { mutableStateOf(0.dp)}

    Column(modifier = Modifier.fillMaxWidth()) {

        Text(text = targetTitle, modifier = Modifier.onGloballyPositioned { coordinates -> textWidth = with(density) { coordinates.size.width.dp - 32.dp} } , color = Color.White, fontSize = FontSizeStatic.Small, fontWeight = FontWeight.Bold)
        Box(modifier = Modifier
            .width(textWidth)
            .height(2.dp)
            .background(MaterialTheme.colors.primary))

        Spacer(Modifier.padding(PaddingStatic.Mini))

        Text(text = content, modifier = Modifier.fillMaxWidth() , color = Color.White, fontSize = FontSizeStatic.Small)

        Spacer(Modifier.padding(PaddingStatic.Small))
    }
}

@Composable
fun NewTrainingButton(viewModel: MainMenuViewModel) {
    if(viewModel.UserIsInstructorOrHigher(viewModel.getApplication<Application>().baseContext)) {
        FloatingActionButton(
        backgroundColor = MaterialTheme.colors.primary,
        onClick = { viewModel.NavigateToPage(ScreenNavName.NewTraining) }) {

            Row(modifier = Modifier.padding(top = PaddingStatic.Tiny, bottom = PaddingStatic.Tiny ,
                start = PaddingStatic.Small, end = PaddingStatic.Small),
                horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "New Training", color = Color.White, fontWeight = FontWeight.Bold, fontSize = FontSizeStatic.Small)
                Image(
                    modifier = Modifier.size(IconSizeStatic.Medium),
                    painter = painterResource(id = R.drawable.plus),
                    contentDescription = "plus icon"
                )
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DisplayListItemTrainingSummary(trainingSummary: TrainingSummary, viewModel: MainMenuViewModel, currentTrainingHandler: CurrentTrainingHandler) {
    val trainingIsNotInProgress = trainingSummary.IsViewable()

    var firstStudentName = ""
    var secondStudentName = ""

    if(trainingSummary.students.size > 0) firstStudentName = trainingSummary.students[0].FullName()
    if(trainingSummary.students.size > 1) secondStudentName = trainingSummary.students[1].FullName()

    Card(modifier = Modifier.height(136.dp), elevation = 4.dp, shape = RoundedCornerShape(topEnd = 14.dp, bottomEnd = 14.dp), backgroundColor = Color(0xFF262626)) {
        Row() {
            Column(
                Modifier
                    .weight(6f)
                    .padding(start = PaddingStatic.Tiny, end = PaddingStatic.Tiny)
            ) {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    if(trainingIsNotInProgress) {
                        Text(
                            text = "${trainingSummary.GetTrainingSummaryListDate()} | ${trainingSummary.GetReadableTime()}",
                            fontSize = FontSizeStatic.Normal,
                            color = MaterialTheme.colors.onSurface,
                            style = TextShadowStatic.Small()
                        )
                    }
                    else {
                        Text(
                            text = "${trainingSummary.GetTrainingSummaryListDate()} | ",
                            fontSize = FontSizeStatic.Normal,
                            color = MaterialTheme.colors.onSurface,
                            style = TextShadowStatic.Small()
                        )

                        Spacer(Modifier.padding(PaddingStatic.Mini))

                        Image(
                            painter = painterResource(id = R.drawable.clock_history),
                            contentDescription = "clock icon",
                            modifier = Modifier.size(IconSizeStatic.Small)
                        )

                        Spacer(Modifier.padding(PaddingStatic.Mini))

                        Text(
                            text = "in progress",
                            fontSize = FontSizeStatic.Small,
                            color = MaterialTheme.colors.onSurface,
                            style = TextShadowStatic.Small()
                        )
                    }
                }


                Spacer(Modifier.padding(3.dp))

                val iconSpace = 2.dp

                Row(verticalAlignment = Alignment.CenterVertically) {
                    //instructor
                    Image(
                        painter = painterResource(id = R.drawable.instructor_ico),
                        contentDescription = "instructor_icon",
                        modifier = Modifier.size(IconSizeStatic.Small)
                    )
                    Spacer(Modifier.padding(iconSpace))
                    Text(
                        text = "${trainingSummary.instructor.FullName()}",
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier.widthIn(134.dp, 134.dp),
                    )
                }

                Spacer(Modifier.padding(PaddingStatic.Tiny))

                Row(verticalAlignment = Alignment.CenterVertically) {

                    if(firstStudentName != "") DisplayStudent(name = firstStudentName)

                    Spacer(Modifier.padding(PaddingStatic.Mini))

                    if(secondStudentName != "") DisplayStudent(name = secondStudentName)

                }
            }

            Column(
                Modifier
                    .weight(2f)
                    .fillMaxWidth()
                    .fillMaxHeight(), verticalArrangement = Arrangement.Center) {

                if(trainingIsNotInProgress) {
                    //View & Close button
//                    val buttonIsOpen: MutableState<Boolean> = remember { mutableStateOf(false) }
                    var buttonIsActive = false
                    val currentlySelectedTraining = viewModel.uiCurrentlySelectedTraining.collectAsState()

                    if(currentlySelectedTraining.value == trainingSummary) {
                        //viewModel.ClearCurrentlySelectedTraining()
                        buttonIsActive = true
                    }

                    Button(
                        onClick = {
                            if(buttonIsActive) {
                                viewModel.ClearCurrentlySelectedTraining()
                            }
                            else {
                                viewModel.viewModelScope.launch {
                                    viewModel.LoadAndDisplayTrainingEvents(context = viewModel.getApplication<Application>().baseContext, selectedTrainingSum = trainingSummary)
                                }
                            }

                        },
                        contentPadding = PaddingValues(4.dp),
                        shape = RoundedCornerShape(38),
                        colors = ButtonDefaults.buttonColors(backgroundColor = if(buttonIsActive) NegativeActionColor else MaterialTheme.colors.primaryVariant ),
                        modifier = Modifier
                            .width(116.dp)
                            .height(34.dp)

                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = if (buttonIsActive) R.drawable.x_circle_fill else R.drawable.eye_fill),
                                contentDescription = "eye_icon",
                                modifier = Modifier.size(if (buttonIsActive) IconSizeStatic.Small - 2.dp else IconSizeStatic.Small)
                            )
                            Spacer(Modifier.padding(start = 8.dp, end = 4.dp))
                            Text(
                                text = if (buttonIsActive) "Close" else "View",
                                color = MaterialTheme.colors.onSurface,
                                fontSize = FontSizeStatic.Small
                            )
                        }
                    }
                }
                else {
                    Button(
                        onClick = {
                            //continue the chosen training
                            viewModel.ContinueTraining(trainingSumInfo = trainingSummary, currentTrainingHandler = currentTrainingHandler)
                        },
                        contentPadding = PaddingValues(4.dp),
                        shape = RoundedCornerShape(38),
                        colors = ButtonDefaults.buttonColors(backgroundColor = PositiveActionColor ),
                        modifier = Modifier
                            .width(116.dp)
                            .height(34.dp)

                    ) {
                        Text(
                            text = "Continue",
                            color = MaterialTheme.colors.onSurface,
                            fontSize = FontSizeStatic.Small
                        )
                    }
                }

            }

        } // end row

    } // end card
}


@Composable
fun DisplayStudent(name: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.person_ico_blue),
            contentDescription = "person_icon",
            modifier = Modifier.size(IconSizeStatic.Small)
        )
        Spacer(Modifier.padding(2.dp))
        if(name == "") {
            DynamicLoadingDisplay(
                loadingText = "Loading student name",
                iconSize = IconSizeStatic.Small,
                textColor = MaterialTheme.colors.primaryVariant,
                iconTint = MaterialTheme.colors.primaryVariant
            )
        }
        else {
            Text(
                text = name,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.widthIn(134.dp, 134.dp),
            )
        }
    }
}