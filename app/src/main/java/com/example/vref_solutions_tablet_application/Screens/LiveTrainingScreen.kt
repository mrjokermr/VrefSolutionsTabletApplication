package com.example.vref_solutions_tablet_application.Screens

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewModelScope
import com.example.vref_solutions_tablet_application.Components.TopBarComp
import com.example.vref_solutions_tablet_application.ViewModels.LiveTrainingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vref_solutions_tablet_application.Components.DyanmicPopUpScreenLiveTraining
import com.example.vref_solutions_tablet_application.Components.DynamicLoadingDisplay
import com.example.vref_solutions_tablet_application.Components.`Text-UI`.SmallAditionalInfoText
import com.example.vref_solutions_tablet_application.Enums.FeedbackTarget
import com.example.vref_solutions_tablet_application.Enums.PopUpType
import com.example.vref_solutions_tablet_application.Handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.Handlers.PrescribedEventsHandler
import com.example.vref_solutions_tablet_application.Handlers.TimelineDisplayHandler
import com.example.vref_solutions_tablet_application.Models.*
import com.example.vref_solutions_tablet_application.Models.PopUpModels.*
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.StylingClasses.*
import com.example.vref_solutions_tablet_application.ui.theme.DarkerMainPurple
import com.example.vref_solutions_tablet_application.ui.theme.PopUpBoxDarkBackground
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LiveTrainingScreen(viewModel: LiveTrainingViewModel = viewModel(), navController: NavController) {
    viewModel.context = LocalContext.current
    viewModel.scope = rememberCoroutineScope()
    viewModel.navController = navController

    viewModel.InitVideoPlayers()

    val currentTrainingInfoHandler = CurrentTrainingHandler(currentContext = LocalContext.current)

    var largeVideoPlayer = viewModel.largeVideoPlayer.collectAsState()
    var smallVideoPlayer  = viewModel.smallVideoPlayer.collectAsState()

    //initiate the loading of the data
    viewModel.viewModelScope.launch {
        viewModel.GetAllTrainingEvents(trainingId = currentTrainingInfoHandler.GetCurrentTrainingId(), authKey = currentTrainingInfoHandler.GetAuthKey())
        viewModel.GetStudentInfo(id = currentTrainingInfoHandler.GetFirstStudentId().toLong(), authKey = currentTrainingInfoHandler.GetAuthKey(), targetIsStudentOne = true)
        viewModel.GetStudentInfo(id = currentTrainingInfoHandler.GetSecondStudentId().toLong(), authKey = currentTrainingInfoHandler.GetAuthKey(), targetIsStudentOne = false)
    }

    //Main box for being able to place the:
    //Button for switching small camera display to prescribed events (and maybe timeline)
    //on top of the small camera display
    Box(modifier = Modifier.fillMaxSize()) {

        DisposableEffect(
            Scaffold(
                topBar = { TopBarComp(ScreenNavName.LiveTraining, navController = navController, hideLogOutButton = true) },
                floatingActionButton = {
                    Box(modifier = Modifier
                        .padding(start = PaddingStatic.Small)
                        .fillMaxWidth()
                        .height(200.dp)
                        .zIndex(10f)) {
                        FloatingActionButtonsMenu(viewModel)
                    }
                }
            ) {

                //start display all the screens and menu options
                Column(modifier = Modifier
                    .padding(PaddingStatic.Small)
                    .zIndex(2f)) {
                    //Large Video Display
                    //here
                    Box(modifier = Modifier
                        .weight(3f)
                        .fillMaxSize()) {

                        Row(modifier = Modifier
                            .zIndex(2f)
                            .fillMaxWidth()
                            .padding(PaddingStatic.Small), horizontalArrangement = Arrangement.End) {
                            Image(
                                painter = painterResource(id = R.drawable.expand_to_fullscreen),
                                contentDescription = "expand_screen",
                                modifier = Modifier
                                    .size(IconSizeStatic.Large)
                                    .zIndex(2f)
                                    .clickable {
                                        //make the video/livestream of the large video display as fullscreen video
                                        viewModel.DisplayFullscreenVideoplayer(
                                            targetIsLargeVideoDisplay = true
                                        )
                                    }
                            )

                        }

                        var forcedRecompesitionLargePlayer = viewModel.uiLargeVideoIsChanged.collectAsState()

                        //forced recomposition because otherwise when SetMediaItem simulating a camera switch
                        //the video wouldn't update...
                        if(forcedRecompesitionLargePlayer.value) {
                            ExoVideoPlayer(videoPlayer = largeVideoPlayer.value, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(RoundedSizeStatic.Medium)).shadow(elevation = 4.dp, shape = RoundedCornerShape(RoundedSizeStatic.Medium), spotColor = Color.White, ambientColor = Color.White).zIndex(1f),
                                context = LocalContext.current,
                                doubleTapAction = {
                                    viewModel.DoubleTapNextVideo(
                                        targetIsLargeVideoDisplay = true
                                    )
                                }
                            )
                        }

                        if(!forcedRecompesitionLargePlayer.value) {
                            ExoVideoPlayer(videoPlayer = largeVideoPlayer.value, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(RoundedSizeStatic.Medium)).shadow(elevation = 4.dp, shape = RoundedCornerShape(RoundedSizeStatic.Medium), spotColor = Color.White, ambientColor = Color.White).zIndex(1f),
                                context = LocalContext.current,
                                doubleTapAction = {
                                    viewModel.DoubleTapNextVideo(
                                        targetIsLargeVideoDisplay = true
                                    )
                                }
                            )
                        }
                    } //end box for placing expand icon ontop

                    Spacer(Modifier.padding(3.dp))

                    //bottom bar with video and events list
                    Row(modifier = Modifier
                        .weight(2f)
                        .fillMaxSize()) {

                        Box( modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()) {

                            var smallLeftBoxTargetIsVideoPlayer = viewModel.uiSmallLeftBoxTargetIsSmallVideoPlayer.collectAsState()

                            if(smallLeftBoxTargetIsVideoPlayer.value) {

                                Row(modifier = Modifier
                                    .zIndex(4f)
                                    .fillMaxWidth()
                                    .padding(PaddingStatic.Small), horizontalArrangement = Arrangement.End) {
                                    Image(painter = painterResource(id = R.drawable.expand_to_fullscreen), contentDescription = "expand_screen",
                                        modifier = Modifier.size(IconSizeStatic.Medium).zIndex(2f)
                                            .clickable {
                                                //make the video/livestream of the large video display as fullscreen video
                                                viewModel.DisplayFullscreenVideoplayer(
                                                    targetIsLargeVideoDisplay = false
                                                )
                                            }
                                    )

                                }

                                var forcedRecompositionSmallPlayer = viewModel.uiSmallVideoIsChanged.collectAsState() //by remember { viewModel.smallVideoIsChanged }

                                //forced recomposition because otherwise when SetMediaItem simulating a camera switch
                                //the video wouldn't update...
                                if(forcedRecompositionSmallPlayer.value) {
                                    ExoVideoPlayer(videoPlayer = smallVideoPlayer.value, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(RoundedSizeStatic.Medium)).zIndex(3f),
                                        context = LocalContext.current,
                                        doubleTapAction = {
                                            viewModel.DoubleTapNextVideo(
                                                targetIsLargeVideoDisplay = false
                                            )
                                        }
                                    )
                                }

                                if(!forcedRecompositionSmallPlayer.value) {
                                    ExoVideoPlayer(videoPlayer = smallVideoPlayer.value, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(RoundedSizeStatic.Medium)).zIndex(3f),
                                        context = LocalContext.current,
                                        doubleTapAction = {
                                            viewModel.DoubleTapNextVideo(
                                                targetIsLargeVideoDisplay = false
                                            )
                                        }
                                    )

                                } //end small video player
                            }
                            else {
                                //display the prescribed events options and timeline
                                PrescribedEventsAndTimelineUI(viewModel = viewModel, currentTrainingHandler = currentTrainingInfoHandler)
                            }
                        }

                        Spacer(Modifier.padding(PaddingStatic.Small))

                        //events list
                        Column(modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(RoundedSizeStatic.Medium))
                            .fillMaxSize()
                            .background(MaterialTheme.colors.onBackground)
                            .padding(PaddingStatic.Small)) {
                            DisplayAllTrainingEventsAndQuickFeedbackUI(viewModel = viewModel, currentTrainingInfoHandler = currentTrainingInfoHandler)

                        }
                    }
                } //end display all the screens and menu options

                var showPopUpScreen = viewModel.uiPopUpScreenIsOpen.collectAsState()
                var popUpType = viewModel.uiPopUpScreenType.collectAsState()

                AnimatedVisibility(modifier = Modifier.zIndex(11f),visible = showPopUpScreen.value, enter = slideIn(tween(200, easing = LinearEasing), initialOffset = { IntOffset(x = 0, y = 300) })) {
                    //pop up container
                    if(showPopUpScreen.value) {
                        Box(modifier = Modifier.zIndex(11f)) {
                            //add this background box as a quality of live feature so that clicking on the side of the box will make you also return back to the livetraining
                            //which is a feature also used in many more applications
                            Row(modifier = Modifier
                                .fillMaxSize()
                                , verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

                                    DyanmicPopUpScreenLiveTraining(popUpScreenHandler = popUpType.value, viewModel = viewModel)

                                } // pop up main column
                            } // end pop up screen row

                        }

                    } // end if pop up screen
                }

                if(showPopUpScreen.value) {
                    val interactionSource = remember { MutableInteractionSource() }
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)).zIndex(10f) //transparent-black background screen filler
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { viewModel.ClosePopUpScreen() }) //doesn't work for some reason for now
                }

                var showFullScreenVideo = viewModel.uiFullScreenVideoIsOpen.collectAsState()
                var fullScreenExoPlayer = viewModel.fullScreenVideoPlayer.collectAsState()

                //AnimatedVisibility(modifier = Modifier.zIndex(11f),visible = showFullScreenVideo.value, enter = expandIn()) {
                    if(showFullScreenVideo.value && fullScreenExoPlayer.value != null) {
                        Box(modifier = Modifier.zIndex(12f)) {
                            Row(modifier = Modifier.zIndex(2f).fillMaxWidth().padding(PaddingStatic.Small), horizontalArrangement = Arrangement.End) {
                                Image(
                                    painter = painterResource(id = R.drawable.collapse_fullscreen),
                                    contentDescription = "collapse_screen",
                                    modifier = Modifier.size(IconSizeStatic.Large).zIndex(2f)
                                        .clickable {
                                            //collapse the full screen display and release the exoplayer
                                            viewModel.HideFullScreenVideoplayer()
                                        }
                                )
                            }

                            //fullscreen videoplayer:
                            ExoVideoPlayer(videoPlayer = fullScreenExoPlayer.value, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(RoundedSizeStatic.Medium)).zIndex(1f),
                                context = LocalContext.current,
                                doubleTapAction = {}
                            )
                        }
                    }
                //}
            }) {
            onDispose {
                //Whenever the screen gets popped, the video players will be released
                if(largeVideoPlayer.value != null) largeVideoPlayer.value!!.release()
                if(smallVideoPlayer.value != null) smallVideoPlayer.value!!.release()
                if(viewModel.fullScreenVideoPlayer.value != null) viewModel.fullScreenVideoPlayer.value!!.release()

            }
        }

        //Button for switching small camera display
        //because this box is outside the other boxes scope zIndex won't work and the button will still be displayed when toggling fullscreen display
        //so for that reason manually hide this button when fullscreen is being displayed
        var fullScreenIsOpen = viewModel.uiFullScreenVideoIsOpen.collectAsState()

        if(!fullScreenIsOpen.value) {
            Button(contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(topStart = RoundedSizeStatic.Medium, topEnd = RoundedSizeStatic.Medium),
                modifier = Modifier.padding(start = 28.dp).width(34.dp).height(28.dp).align(Alignment.BottomStart).zIndex(1f),
                onClick = { viewModel.ToggleSmallScreenFunctionTarget() }) {
                Image(
                    modifier = Modifier.padding(top = PaddingStatic.Small).size(IconSizeStatic.Medium),
                    painter = painterResource(R.drawable.calendar_with_squares),
                    contentDescription = "collapse icon",
                    colorFilter = ColorFilter.tint(Color.White),
                )
            }
        }


    } //end main box wrapper

}

@Composable
fun DisplayAllTrainingEventsAndQuickFeedbackUI(viewModel: LiveTrainingViewModel, currentTrainingInfoHandler: CurrentTrainingHandler) {
        var allTrainingEvents = viewModel.uiAllTrainingEvents.collectAsState()

        //row with quick feedback text and add text
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(modifier = Modifier.weight(5f),text = "Quick feedback", textDecoration = TextDecoration.Underline, color = Color.Gray,
                fontSize = FontSizeStatic.Small, style = TextShadowStatic.Small())
            Text(modifier = Modifier
                .weight(1f)
                .fillMaxWidth(), textAlign = TextAlign.Center,text = "Add", textDecoration = TextDecoration.Underline, color = Color.Gray,
                fontSize = FontSizeStatic.Small, style = TextShadowStatic.Small())
        }

        Spacer(Modifier.padding(PaddingStatic.Mini))

        var inputFieldQuickFeedback = viewModel.inputQuickFeedback.collectAsState()

        //row with quick feedback input field and add quick feedback button
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(54.dp), verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.SpaceBetween) {
            TextField(
                modifier = Modifier.weight(5f).height(54.dp)
                    .clip(RoundedCornerShape(RoundedSizeStatic.Medium))
                    .padding(0.dp),
                value = inputFieldQuickFeedback.value,
                singleLine = true,
                onValueChange = { viewModel.SetQuickFeedback(input = it) },
                placeholder = { Text("Write some quick feedback here...") },
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
                    fontSize = FontSizeStatic.Small,
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
                        viewModel.viewModelScope.launch {
                            viewModel.NewQuickTrainingEvent(
                                currentTrainingId = currentTrainingInfoHandler.GetCurrentTrainingId(),
                                message = inputFieldQuickFeedback.value,
                                authKey = currentTrainingInfoHandler.GetAuthKey()
                            )

                        }
                    }) {
                    Image(
                        modifier = Modifier.size(IconSizeStatic.Small),
                        painter = painterResource(id = R.drawable.plus),
                        contentDescription = "plus_icon"
                    )
                }
            }
        }

        Spacer(Modifier.padding(PaddingStatic.Tiny))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            if(allTrainingEvents.value != null) {
                if(allTrainingEvents.value!!.size > 0) {
                    items(allTrainingEvents.value!!.size) {
                        EventDisplayListItem(trainingEvent = allTrainingEvents.value!![it], viewModel = viewModel)

                        Spacer(Modifier.padding(PaddingStatic.Mini))
                    }
                }
                else {
                    item() {
                        Text(modifier = Modifier.fillMaxWidth(),text = "There is no event/feedback made \nfor this training yet.", color = Color.White,
                            fontSize = FontSizeStatic.Small,
                            textAlign = TextAlign.Center)
                    }
                }
            }
            else {
                item() {
                    Column() {
                        DynamicLoadingDisplay(loadingText = "Loading events", iconSize = IconSizeStatic.Medium, iconTint = Color.White, textColor = Color.White)

                        Spacer(Modifier.padding(PaddingStatic.Mini))

                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = PaddingStatic.Medium), horizontalArrangement = Arrangement.Center) {
                            Button(
                                onClick = {
                                    viewModel.viewModelScope.launch {
                                        viewModel.GetAllTrainingEvents(trainingId = currentTrainingInfoHandler.GetCurrentTrainingId(), authKey = currentTrainingInfoHandler.GetAuthKey())
                                    }
                                },
                                contentPadding = PaddingValues(start = PaddingStatic.Tiny, end = PaddingStatic.Tiny),
                                shape = RoundedCornerShape(38),
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary ),
                                modifier = Modifier.height(24.dp).width(204.dp)
                            ) {
                                Text(
                                    text = "Manual reload events",
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

@Composable
fun ExoVideoPlayer(videoPlayer: ExoPlayer?, modifier: Modifier, context: Context, doubleTapAction: ()-> Unit) {
    DisposableEffect(
        AndroidView(
            modifier = modifier.pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        doubleTapAction()
                    }
                )
            },
            factory = {
                StyledPlayerView(context).apply {
                    player = videoPlayer

                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }
        )
    )
    {
        onDispose {
            if(videoPlayer != null) videoPlayer.release()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PrescribedEventsAndTimelineUI(viewModel: LiveTrainingViewModel, currentTrainingHandler: CurrentTrainingHandler ) {
    //hier
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(RoundedSizeStatic.Medium))
        .background(MaterialTheme.colors.onBackground)
        .padding(PaddingStatic.Small)) {

        item() {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Prescribed events", color = Color.Gray, fontSize = FontSizeStatic.Tiny, style = TextShadowStatic.Small())

                    Spacer(Modifier.padding(PaddingStatic.Mini))

                    Box(
                        modifier = Modifier
                            .size(IconSizeStatic.Small)
                            .clip(RoundedCornerShape(100))
                            .background(Color.White)
                            .clickable {
                                viewModel.OpenPopUpScreen(PrescribedEventsDetails())
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.info_circle_fill),
                            colorFilter = ColorFilter.tint(Color(0xFF990FEE)),
                            contentDescription = "event_icon",
                            modifier = Modifier.size(IconSizeStatic.Small),
                        )
                    }
                }

                Spacer(Modifier.padding(PaddingStatic.Mini))

                var studentOne = viewModel.uiStudentOne.collectAsState()
                var studentTwo = viewModel.uiStudentTwo.collectAsState()
                var feedbackTarget = viewModel.uiFeedbackTarget.collectAsState()

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

                    SmallAditionalInfoText(text = "For:", modifier = Modifier.weight(2f), textSize = FontSizeStatic.Tiny, textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Normal)

                    //list with feedback-target description info
                    Row(modifier = Modifier.weight(5f), horizontalArrangement = Arrangement.SpaceAround) {

                        Column(modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.SetFeedbackTarget(FeedbackTarget.Everyone)
                            }) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Everyone",
                                textAlign = TextAlign.Center,
                                fontSize = FontSizeStatic.Tiny,
                                color = Color.White,
                                style = TextShadowStatic.Small()
                            )

                            AnimatedVisibility(
                                modifier = Modifier.zIndex(18f),
                                visible = feedbackTarget.value == FeedbackTarget.Everyone,
                                enter = scaleIn(
                                    tween(
                                        180,
                                        easing = FastOutSlowInEasing
                                    )
                                )
                            ) {
                                if (feedbackTarget.value == FeedbackTarget.Everyone) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(2.dp)
                                            .background(MaterialTheme.colors.primary)
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.SetFeedbackTarget(FeedbackTarget.StudentOne)
                            }) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = if (studentOne.value != null) studentOne.value!!.GetFirstNameAndInitialLastName() else "unknown",
                                textAlign = TextAlign.Center,
                                fontSize = FontSizeStatic.Tiny,
                                color = Color.White,
                                style = TextShadowStatic.Small()
                            )

                            AnimatedVisibility(modifier = Modifier.zIndex(18f), visible = feedbackTarget.value == FeedbackTarget.StudentOne,
                                enter = scaleIn(
                                    tween(
                                        180,
                                        easing = FastOutSlowInEasing
                                    )
                                )
                            ) {
                                if (feedbackTarget.value == FeedbackTarget.StudentOne) {
                                    Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(MaterialTheme.colors.primary))
                                }
                            }

                        }

                        Column(modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.SetFeedbackTarget(FeedbackTarget.StudentTwo)
                            }) {
                            Text(
                                modifier = Modifier.fillMaxWidth(), text = if (studentTwo.value != null) studentTwo.value!!.GetFirstNameAndInitialLastName() else "unknown",
                                textAlign = TextAlign.Center, fontSize = FontSizeStatic.Tiny, color = Color.White, style = TextShadowStatic.Small()
                            )

                            AnimatedVisibility(
                                modifier = Modifier.zIndex(18f),
                                visible = feedbackTarget.value == FeedbackTarget.StudentTwo,
                                enter = scaleIn(
                                    tween(
                                        180,
                                        easing = FastOutSlowInEasing
                                    )
                                )
                            ) {
                                if (feedbackTarget.value == FeedbackTarget.StudentTwo) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().height(2.dp).background(MaterialTheme.colors.primary)
                                    )
                                }
                            }
                        }
                    }
                } //end row with the feedback target selection

                Spacer(Modifier.padding(PaddingStatic.Mini))

                LazyRow(modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(RoundedSizeStatic.Small)).background(PopUpBoxDarkBackground)
                    .padding(PaddingStatic.Mini), verticalAlignment = Alignment.CenterVertically) {
                    val allPrescribedEvents = PrescribedEventsHandler.allPescribedEvents

                    items(allPrescribedEvents.size) {
                        var prescribedEvent = allPrescribedEvents[it]

                        Box(modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(RoundedSizeStatic.Small))
                            .background(Color.White)
                            .clickable {
                                viewModel.NewPrescribedEvent(
                                    prescribedEvent = prescribedEvent,
                                    currentTrainingId = currentTrainingHandler.GetCurrentTrainingId(),
                                    authKey = currentTrainingHandler.GetAuthKey()
                                )
                            }) {
                            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Image(
                                    modifier = Modifier.fillMaxSize(),
                                    painter = painterResource(id = prescribedEvent.drawableId),
                                    contentDescription = "prescribedEventIcon_${prescribedEvent.title}"
                                )


                            }
                        }

                        Spacer(Modifier.padding(PaddingStatic.Small))
                    }
                }

                Spacer(Modifier.padding(PaddingStatic.Mini))

                TimelineUiDisplay(viewModel = viewModel, currentTrainingHandler = currentTrainingHandler)
            }
        }
    }

}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun TimelineUiDisplay(viewModel: LiveTrainingViewModel, currentTrainingHandler: CurrentTrainingHandler) {

    Row() {
        Text(text = "Timeline ", color = Color.Gray, fontSize = FontSizeStatic.Tiny, style = TextShadowStatic.Small())

        Text(text = "*Per ${TimelineDisplayHandler.secondsPerBlock/60} minutes", color = Color.Gray,modifier = Modifier.padding(bottom = 4.dp) , fontSize = FontSizeStatic.Mini, style = TextShadowStatic.Small())

    }
    if(viewModel.LocalDateTimesAreWithinGivenHoursDifference(LocalDateTime.now(),currentTrainingHandler.GetCurrentTrainingDateAsDateTimeObject(),12)) {
        var allTrainingTimeLineDisplayItems = viewModel.uiAllTrainingTimelineDisplayItems.collectAsState()
        val listState = rememberLazyListState()

        // a CoroutineScope to be able to launch
        val coroutineScope = rememberCoroutineScope()

        LazyRow(state = listState,modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(RoundedSizeStatic.Small))
            .background(PopUpBoxDarkBackground)
            .padding(PaddingStatic.Mini),
            verticalAlignment = Alignment.CenterVertically) {

            if(allTrainingTimeLineDisplayItems.value != null) {

                items(allTrainingTimeLineDisplayItems.value!!.size) {

                    TimelineItemDisplay(item = allTrainingTimeLineDisplayItems.value!![it], isMostRecentTimelineItem = it == allTrainingTimeLineDisplayItems.value!!.size - 1)

                }
            }
        }

        Spacer(Modifier.padding(PaddingStatic.Mini))

        //row with the buttons to navigate to the start or end of the list
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if(allTrainingTimeLineDisplayItems.value != null) {

                Button(
                    onClick = {
                        coroutineScope.launch {
                            listState.scrollToItem(index = 0)
                        }
                    },
                    contentPadding = PaddingValues(start = PaddingStatic.Tiny, end = PaddingStatic.Tiny),
                    shape = RoundedCornerShape(38),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary ),
                    modifier = Modifier.height(18.dp)

                ) {
                    Text(
                        text = "Jump to start",
                        color = MaterialTheme.colors.onSurface ,
                        fontSize = FontSizeStatic.Mini
                    )
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            if(allTrainingTimeLineDisplayItems.value!!.size > 5) listState.scrollToItem(index = allTrainingTimeLineDisplayItems.value!!.lastIndex - 3)
                        }
                    },
                    contentPadding = PaddingValues(start = PaddingStatic.Tiny, end = PaddingStatic.Tiny),
                    shape = RoundedCornerShape(38),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary ),
                    modifier = Modifier.height(18.dp)

                ) {
                    Text(
                        text = "Jump to now",
                        color = MaterialTheme.colors.onSurface ,
                        fontSize = FontSizeStatic.Mini
                    )
                }

            }

        }

        //start the auto refresher of the allevents list which will automatically update the timeline
        LaunchedEffect(Unit) {
            while(true) {
                viewModel.GetAllTrainingEvents(trainingId = currentTrainingHandler.GetCurrentTrainingId(), authKey = currentTrainingHandler.GetAuthKey())
                delay(TimelineDisplayHandler.secondsPerBlock.toLong())

            }
        }
    }
    else {
        Row(modifier = Modifier.fillMaxWidth().height(64.dp).clip(RoundedCornerShape(RoundedSizeStatic.Small)).background(PopUpBoxDarkBackground).padding(PaddingStatic.Tiny),
                verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Can't display timeline because the timeline will be longer then 12 hours. Training should probably be saved & closed.",color = Color.White,
                fontSize = FontSizeStatic.Tiny, style = TextShadowStatic.Small(), fontWeight = FontWeight.Bold)
        }

    }




    Spacer(Modifier.padding(PaddingStatic.Tiny))
}

@Composable
fun TimelineItemDisplay(item: TimelineEventsDisplayDataItem, isMostRecentTimelineItem: Boolean) {
    BoxWithConstraints(modifier = Modifier
        .width(TimelineDisplayHandler.spacePerDisplayBlock)
        .fillMaxHeight()) {
        Column() {

            Column(Modifier.height(40.dp), horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween) {

                for (i in 0..2) {
                    Image(
                        painter = painterResource(id = R.drawable.line_top_down),
                        contentDescription = "line_icon",
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
                Text(text = if(!isMostRecentTimelineItem) item.fromCustomTimestamp.GetHoursMinutesAndSecondsFormat() else "Now",
                    color = Color.Gray, fontSize = FontSizeStatic.Mini, fontWeight = FontWeight.Bold,
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
                    Text(text = "+${thisItemTrainingEventCount - 2}", color = Color.White, style = TextShadowStatic.Small(), fontSize = FontSizeStatic.Mini)
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

@Composable
fun TrainingEventInTimeLineDisplay(trainingEvent: TrainingEvent) {
    Column(Modifier.width(TimelineDisplayHandler.spacePerTimeLineItem)
        , horizontalAlignment = Alignment.CenterHorizontally) {

        Image(
            painter = painterResource(id = trainingEvent.GetIconPainterId()),
            contentDescription = "event_icon",
            modifier = Modifier
                .size(IconSizeStatic.Tiny)
                .clip(RoundedCornerShape(if (trainingEvent.IsPrescribedEvent()) RoundedSizeStatic.Small else 0.dp))
                .background(if (trainingEvent.IsPrescribedEvent()) Color.White else Color.Transparent)
        )

        Spacer(Modifier.padding(2.dp))

        Text(text = trainingEvent.timeStamp.GetMinutesAndSecondsFormat(), color = Color.White, fontSize = 8.sp, style = TextShadowStatic.Small())
    }

}


@Composable
fun EventDisplayListItem(trainingEvent: TrainingEvent,viewModel: LiveTrainingViewModel) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Button(
            onClick = {
                if(!trainingEvent.IsAutomaticFeedback()) {
                    viewModel.OpenPopUpScreenEditEvent(trainingEvent = trainingEvent,type = EditEvent())
                }
                else {
                    Toast.makeText(viewModel.context,"Can't edit automatic feedback!", Toast.LENGTH_SHORT).show()
                }
            },
            contentPadding = PaddingValues(start = PaddingStatic.Tiny, end = PaddingStatic.Tiny),
            shape = RoundedCornerShape(38),
            colors = ButtonDefaults.buttonColors(backgroundColor = if(trainingEvent.IsAutomaticFeedback()) DarkerMainPurple else MaterialTheme.colors.primary ),
            modifier = Modifier
                .height(24.dp)
                .weight(3f)

        ) {
            Text(
                text = if(trainingEvent.IsAutomaticFeedback()) "Generated" else "Change",
                color = MaterialTheme.colors.onSurface ,
                fontSize = FontSizeStatic.Tiny
            )
        }

        Spacer(Modifier.padding(PaddingStatic.Mini))

        Text(modifier = Modifier.weight(2f),text = trainingEvent.timeStamp.GetHoursAndMinutesFormat(), color = Color.White, textAlign = TextAlign.Center,
            fontSize = FontSizeStatic.Small, style = TextShadowStatic.Small())

        Spacer(Modifier.padding(PaddingStatic.Mini))

        Row(modifier = Modifier
            .fillMaxHeight()
            .weight(1f)) {
            Spacer(Modifier.weight(1f))

            Image(
                painter = painterResource(id = trainingEvent.GetIconPainterId()),
                contentDescription = "event_icon",
                modifier = Modifier
                    .size(IconSizeStatic.Small)
                    .clip(
                        RoundedCornerShape(
                            if (trainingEvent.IsPrescribedEvent()) RoundedSizeStatic.Small else 0.dp
                        )
                    )
                    .background(if (trainingEvent.IsPrescribedEvent()) Color.White else Color.Transparent)
                    .weight(2f)
            )

            Spacer(Modifier.weight(1f))
        }


        Spacer(Modifier.padding(PaddingStatic.Mini))

        Text(modifier = Modifier.weight(7f),text = trainingEvent.name, color = Color.White, fontSize = FontSizeStatic.Small, style = TextShadowStatic.Small())
    }
}


@Composable
fun FloatingActionButtonsMenu(viewModel: LiveTrainingViewModel) {
    var menuIsOpen = viewModel.uiOptionMenuIsOpen.collectAsState()

    Row() {
        Spacer(Modifier.weight(1f))
        BoxWithConstraints(
            modifier = Modifier
                .width(200.dp)
                .fillMaxHeight()
        ) {
            //main large button
            Button(contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(100),
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.BottomEnd)
                    .shadow(4.dp, RoundedCornerShape(100)),
                onClick = { viewModel.ToggleOptionMenu() }) {
                Image(
                    modifier = Modifier.size(if(!menuIsOpen.value) IconSizeStatic.MenuBurgerico else IconSizeStatic.Medium),
                    painter = painterResource(id = if(!menuIsOpen.value) R.drawable.menu_ico else R.drawable.x_lg_white),
                    contentDescription = "menu icon"
                )
            }

            if (menuIsOpen.value) {
                //exit training button
                Button(contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(100),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 130.dp)
                        .size(50.dp)
                        .shadow(4.dp, RoundedCornerShape(100)),
                    onClick = { viewModel.OpenPopUpScreen(ExitTraining()) }) {

                    Image(
                        modifier = Modifier
                            .size(IconSizeStatic.MenuExitico)
                            .padding(start = 4.dp),
                        painter = painterResource(id = R.drawable.exit),
                        contentDescription = "exit icon"
                    )

                }

                //switch camera 2
                Button(contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(100),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 100.dp, bottom = 52.dp)
                        .size(50.dp)
                        .shadow(4.dp, RoundedCornerShape(100)),
                    onClick = { viewModel.OpenPopUpScreen(SwitchCamera(PopUpType.SWITCH_CAMERA_2, viewModel = viewModel)) }) {
                    Image(
                        modifier = Modifier.size(IconSizeStatic.MenuCameraIco),
                        painter = painterResource(id = R.drawable.switch_camera_2),
                        contentDescription = "switch camera 2 icon"
                    )
                }

                //switch camera 1
                Button(contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(100),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 60.dp, bottom = 94.dp)
                        .size(50.dp)
                        .shadow(4.dp, RoundedCornerShape(100)),
                    onClick = {
                        viewModel.OpenPopUpScreen(SwitchCamera(PopUpType.SWITCH_CAMERA_1, viewModel = viewModel))

                    }) {
                    Image(
                        modifier = Modifier.size(IconSizeStatic.MenuCameraIco),
                        painter = painterResource(id = R.drawable.switch_camera_1),
                        contentDescription = "switch camera 1 icon"
                    )
                }

                //plus icon
                Button(contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(100),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 10.dp, bottom = 126.dp)
                        .size(50.dp)
                        .shadow(4.dp, RoundedCornerShape(100)),
                    onClick = { viewModel.OpenPopUpScreen(AddEvent()) }) {
                    Image(
                        modifier = Modifier.size(IconSizeStatic.MenuPlusIco),
                        painter = painterResource(id = R.drawable.plus),
                        contentDescription = "plus icon"
                    )
                }
            }


        }
    }
}