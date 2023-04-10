package com.example.vref_solutions_tablet_application.screens

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.vref_solutions_tablet_application.components.TopBarComp
import com.example.vref_solutions_tablet_application.viewModels.LiveTrainingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vref_solutions_tablet_application.components.DyanmicPopUpScreenLiveTraining
import com.example.vref_solutions_tablet_application.components.ExoVideoPlayer
import com.example.vref_solutions_tablet_application.handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.models.*
import com.example.vref_solutions_tablet_application.models.popUpModels.*
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.screens.liveTraningScreen.*
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.*

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LiveTrainingScreen(viewModel: LiveTrainingViewModel = viewModel(), navController: NavController) {
    viewModel.scope = rememberCoroutineScope()
    viewModel.navController = navController

    viewModel.initVideoPlayers()

    val currentTrainingInfoHandler = CurrentTrainingHandler(currentContext = LocalContext.current)

    var largeVideoPlayer = viewModel.largeVideoPlayer.collectAsState()
    var smallVideoPlayer  = viewModel.smallVideoPlayer.collectAsState()

    //initiate the loading of the data
    viewModel.launchLoadingOfNecessaryStartingData(currentTrainingInfoHandler = currentTrainingInfoHandler)

    //Main box for being able to place the:
    //Button for switching small camera display to prescribed events (and maybe timeline)
    //on top of the small camera display
    Box(modifier = Modifier.fillMaxSize()) {

        DisposableEffect(
            Scaffold(
                topBar = { TopBarComp(ScreenNavName.LiveTraining, navController = navController, hideLogOutButton = true) },
                floatingActionButton = {
                    Box(modifier = Modifier
                        .padding(start = MaterialTheme.padding.small)
                        .fillMaxWidth()
                        .height(200.dp)
                        .zIndex(10f)) {
                        FloatingActionButtonsMenu(viewModel)
                    }
                }
            ) {

                //start display all the screens and menu options
                Column(modifier = Modifier
                    .padding(MaterialTheme.padding.small)
                    .zIndex(2f)) {
                    //Large Video Display
                    //here
                    Box(modifier = Modifier
                        .weight(3f)
                        .fillMaxSize()) {

                        Row(modifier = Modifier
                            .zIndex(2f)
                            .fillMaxWidth()
                            .padding(MaterialTheme.padding.small), horizontalArrangement = Arrangement.End) {
                            Image(
                                painter = painterResource(id = R.drawable.expand_to_fullscreen),
                                contentDescription = stringResource(R.string.cd_expand_screen_icon),
                                modifier = Modifier
                                    .size(MaterialTheme.iconSize.large)
                                    .zIndex(2f)
                                    .clickable {
                                        //make the video/livestream of the large video display as fullscreen video
                                        viewModel.displayFullscreenVideoplayer(
                                            targetIsLargeVideoDisplay = true
                                        )
                                    }
                            )

                        }

                        var forcedRecompesitionLargePlayer = viewModel.uiLargeVideoIsChanged.collectAsState()

                        //forced recomposition because otherwise when SetMediaItem simulating a camera switch
                        //the video wouldn't update...
                        if(forcedRecompesitionLargePlayer.value) {
                            ExoVideoPlayer(videoPlayer = largeVideoPlayer.value, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(
                                MaterialTheme.roundedCornerShape.medium)).shadow(elevation = 4.dp, shape = RoundedCornerShape(
                                MaterialTheme.roundedCornerShape.medium), spotColor = Color.White, ambientColor = Color.White).zIndex(1f),
                                context = LocalContext.current,
                                doubleTapAction = {
                                    viewModel.doubleTapNextVideo(
                                        targetIsLargeVideoDisplay = true
                                    )
                                }
                            )
                        }

                        if(!forcedRecompesitionLargePlayer.value) {
                            ExoVideoPlayer(videoPlayer = largeVideoPlayer.value, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(
                                MaterialTheme.roundedCornerShape.medium)).shadow(elevation = 4.dp, shape = RoundedCornerShape(
                                MaterialTheme.roundedCornerShape.medium), spotColor = Color.White, ambientColor = Color.White).zIndex(1f),
                                context = LocalContext.current,
                                doubleTapAction = {
                                    viewModel.doubleTapNextVideo(
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
                                    .padding(MaterialTheme.padding.small), horizontalArrangement = Arrangement.End) {
                                    Image(painter = painterResource(id = R.drawable.expand_to_fullscreen), contentDescription = stringResource(R.string.cd_expand_screen_icon),
                                        modifier = Modifier.size(MaterialTheme.iconSize.medium).zIndex(2f)
                                            .clickable {
                                                //make the video/livestream of the large video display as fullscreen video
                                                viewModel.displayFullscreenVideoplayer(
                                                    targetIsLargeVideoDisplay = false
                                                )
                                            }
                                    )

                                }

                                var forcedRecompositionSmallPlayer = viewModel.uiSmallVideoIsChanged.collectAsState() //by remember { viewModel.smallVideoIsChanged }

                                //forced recomposition because otherwise when SetMediaItem simulating a camera switch
                                //the video wouldn't update...
                                if(forcedRecompositionSmallPlayer.value) {
                                    ExoVideoPlayer(videoPlayer = smallVideoPlayer.value, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(
                                        MaterialTheme.roundedCornerShape.medium)).zIndex(3f),
                                        context = LocalContext.current,
                                        doubleTapAction = {
                                            viewModel.doubleTapNextVideo(
                                                targetIsLargeVideoDisplay = false
                                            )
                                        }
                                    )
                                }

                                if(!forcedRecompositionSmallPlayer.value) {
                                    ExoVideoPlayer(videoPlayer = smallVideoPlayer.value, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(
                                        MaterialTheme.roundedCornerShape.medium)).zIndex(3f),
                                        context = LocalContext.current,
                                        doubleTapAction = {
                                            viewModel.doubleTapNextVideo(
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

                        Spacer(Modifier.padding(MaterialTheme.padding.small))

                        //events list
                        Column(modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(MaterialTheme.roundedCornerShape.medium))
                            .fillMaxSize()
                            .background(MaterialTheme.colors.onBackground)
                            .padding(MaterialTheme.padding.small)) {
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
                        ) { viewModel.closePopUpScreen() }) //doesn't work for some reason for now
                }

                var showFullScreenVideo = viewModel.uiFullScreenVideoIsOpen.collectAsState()
                var fullScreenExoPlayer = viewModel.fullScreenVideoPlayer.collectAsState()

                //AnimatedVisibility(modifier = Modifier.zIndex(11f),visible = showFullScreenVideo.value, enter = expandIn()) {
                    if(showFullScreenVideo.value && fullScreenExoPlayer.value != null) {
                        Box(modifier = Modifier.zIndex(12f)) {
                            Row(modifier = Modifier.zIndex(2f).fillMaxWidth().padding(MaterialTheme.padding.small), horizontalArrangement = Arrangement.End) {
                                Image(
                                    painter = painterResource(id = R.drawable.collapse_fullscreen),
                                    contentDescription = stringResource(R.string.cd_collapse_screen_icon),
                                    modifier = Modifier.size(MaterialTheme.iconSize.large).zIndex(2f)
                                        .clickable {
                                            //collapse the full screen display and release the exoplayer
                                            viewModel.hideFullScreenVideoplayer()
                                        }
                                )
                            }

                            //fullscreen videoplayer:
                            ExoVideoPlayer(videoPlayer = fullScreenExoPlayer.value, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(
                                MaterialTheme.roundedCornerShape.medium)).zIndex(1f),
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
                shape = RoundedCornerShape(topStart = MaterialTheme.roundedCornerShape.medium, topEnd = MaterialTheme.roundedCornerShape.medium),
                modifier = Modifier.padding(start = 28.dp).width(34.dp).height(28.dp).align(Alignment.BottomStart).zIndex(1f),
                onClick = { viewModel.toggleSmallScreenFunctionTarget() }) {
                Image(
                    modifier = Modifier.padding(top = MaterialTheme.padding.small).size(MaterialTheme.iconSize.medium),
                    painter = painterResource(R.drawable.calendar_with_squares),
                    contentDescription = stringResource(R.string.cd_collapse_screen_icon),
                    colorFilter = ColorFilter.tint(Color.White),
                )
            }
        }


    } //end main box wrapper

}









