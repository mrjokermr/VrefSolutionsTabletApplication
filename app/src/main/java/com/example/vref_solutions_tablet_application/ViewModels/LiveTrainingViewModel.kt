package com.example.vref_solutions_tablet_application.ViewModels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.vref_solutions_tablet_application.API.RequestBodies.EventRequestBody
import com.example.vref_solutions_tablet_application.API.RequestBodies.StopTrainingRequestBody
import com.example.vref_solutions_tablet_application.API.TrainingApi
import com.example.vref_solutions_tablet_application.API.UserApi
import com.example.vref_solutions_tablet_application.Enums.FeedbackTarget
import com.example.vref_solutions_tablet_application.Enums.PopUpType
import com.example.vref_solutions_tablet_application.Handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.Handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.Handlers.TimelineDisplayHandler
import com.example.vref_solutions_tablet_application.Mappers.TrainingMapper
import com.example.vref_solutions_tablet_application.Mappers.UserMapper
import com.example.vref_solutions_tablet_application.Models.*
import com.example.vref_solutions_tablet_application.Models.PopUpModels.AddEvent
import com.example.vref_solutions_tablet_application.Models.PopUpModels.BasePopUpScreen
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.`API-Retrofit`.RetrofitApiHandler
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime


class LiveTrainingViewModel(application: Application) : AndroidViewModel(application)  {

    @SuppressLint("StaticFieldLeak")
    lateinit var context: Context
    lateinit var scope: CoroutineScope
    lateinit var navController: NavController

    //Previously I used the VideoDisplayer model that contained the exoplayer and active camera link
    //But this wouldn't work with displaying the exoplayer for some reason so I put them seperatly back inside this viewmodel.
    //I really dislike exoplayer and its behaviour with Jetpack Compose :(
    var activeLargeVideoPlayerCameraLinkObject: CameraLinkObject = CameraLink.cockPitMiddle
    var activeSmallVideoPlayerCameraLinkObject: CameraLinkObject = CameraLink.mapCamera

    val largeVideoPlayer = MutableStateFlow<ExoPlayer?>(null)
    val smallVideoPlayer = MutableStateFlow<ExoPlayer?>(null)

    private val optionMenuIsOpen = MutableStateFlow(false)
    val uiOptionMenuIsOpen: StateFlow<Boolean> = optionMenuIsOpen

    private val popUpScreenIsOpen = MutableStateFlow(false)
    val uiPopUpScreenIsOpen: StateFlow<Boolean> = popUpScreenIsOpen

    private var fullScreenVideoIsOpen = MutableStateFlow(false)
    val uiFullScreenVideoIsOpen: StateFlow<Boolean> = fullScreenVideoIsOpen

    private val smallLeftBoxTargetIsSmallVideoPlayer = MutableStateFlow(true)
    val uiSmallLeftBoxTargetIsSmallVideoPlayer: StateFlow<Boolean> = smallLeftBoxTargetIsSmallVideoPlayer

    private val largeVideoIsChanged = MutableStateFlow(false)
    val uiLargeVideoIsChanged: StateFlow<Boolean> = largeVideoIsChanged

    private val smallVideoIsChanged = MutableStateFlow(false)
    val uiSmallVideoIsChanged: StateFlow<Boolean> = smallVideoIsChanged

    private val doubleTapCameraLinkOrder = listOf(
        CameraLink.mapCamera, CameraLink.cockPitMiddle, CameraLink.cockPitRight, CameraLink.pilotsDiscussing, CameraLink.navigationAndAltitudeDisplay
    )

    var fullScreenVideoPlayer = MutableStateFlow<ExoPlayer?>(null)

    private val popUpScreenType = MutableStateFlow<BasePopUpScreen>(AddEvent())
    val uiPopUpScreenType : StateFlow<BasePopUpScreen> = popUpScreenType

    private val eventToEdit = MutableStateFlow<TrainingEvent?>(null)
    val uiEventToEdit: StateFlow<TrainingEvent?> = eventToEdit

    private val currentlySelectedIconForEvent = MutableStateFlow("")
    val uiCurrentlySelectedIconForEvent: StateFlow<String> = currentlySelectedIconForEvent

    private val iconPickerIsOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val uiIconPickerIsOpen : StateFlow<Boolean> = iconPickerIsOpen

    private val allTrainingEvents: MutableStateFlow<List<TrainingEvent>?> = MutableStateFlow(null)
    val uiAllTrainingEvents: StateFlow<List<TrainingEvent>?> = allTrainingEvents

    private val allTrainingTimelineDisplayItems: MutableStateFlow<List<TimelineEventsDisplayDataItem>?> = MutableStateFlow(null)
    val uiAllTrainingTimelineDisplayItems: StateFlow<List<TimelineEventsDisplayDataItem>?> = allTrainingTimelineDisplayItems

    private val studentOne: MutableStateFlow<User?> = MutableStateFlow(null)
    val uiStudentOne: StateFlow<User?> = studentOne

    private val studentTwo: MutableStateFlow<User?> = MutableStateFlow(null)
    val uiStudentTwo: StateFlow<User?> = studentTwo

    private val feedbackTarget: MutableStateFlow<FeedbackTarget> = MutableStateFlow(FeedbackTarget.Everyone)
    val uiFeedbackTarget: StateFlow<FeedbackTarget> = feedbackTarget

    var devidedFeedbackContainer: DevidedFeedbackContainer = DevidedFeedbackContainer()

    //add event input fields:
    private val _inputEventTitle = MutableStateFlow("")
    val inputEventTitle: StateFlow<String> = _inputEventTitle

    private val _inputEventDescription = MutableStateFlow("")
    val inputEventDescription: StateFlow<String> = _inputEventDescription

    private val _inputQuickFeedback = MutableStateFlow("")
    val inputQuickFeedback: StateFlow<String> = _inputQuickFeedback

    private val trainingApi: TrainingApi = RetrofitApiHandler.GetTrainingsApi()
    private val userApi: UserApi = RetrofitApiHandler.GetUsersApi()

    fun InitVideoPlayers() {
        largeVideoPlayer.value = GetNewCameraPlayer(activeLargeVideoPlayerCameraLinkObject.url)
        smallVideoPlayer.value = GetNewCameraPlayer(activeSmallVideoPlayerCameraLinkObject.url)
    }

    fun FinishCurrentTraining(context: Context) {
        try {
            val currentTrainingHandler: CurrentTrainingHandler = CurrentTrainingHandler(currentContext = context)
            val currentTrainingId: Long = currentTrainingHandler.GetCurrentTrainingId().toLong()

            viewModelScope.launch {
                val result = trainingApi.StopTrainingById(trainingId = currentTrainingId,body = StopTrainingRequestBody(), authToken = currentTrainingHandler.GetAuthKey())
                val responseCode = result.raw().code

                if(responseCode >= 200 && responseCode < 300) {
                    //call was succesfull
                    Toast.makeText(context, "Succesfully finished the training", Toast.LENGTH_LONG).show()
                    NavigateToPage(ScreenNavName.MainMenu)
                }
                else Toast.makeText(context, "Could not finished the training", Toast.LENGTH_LONG).show() //call failed
            }
        }
        catch(e: Throwable) {
            Log.i("ERRROR","Couldn't finish training")
            Log.i("Error",e.message.toString() + " " +  e.cause)
        }
    }

    suspend fun GetStudentInfo(id: Long, authKey: String, targetIsStudentOne: Boolean) {
        try {
            //getUserById
            val result = userApi.getUserById(userId = id,authToken = authKey)
            val responseCode = result.raw().code
            val body = result.body()
            //Log.i("TEST",body?.size.toString())

            if(body != null && responseCode >= 200 && responseCode < 300) {
                val mappedUser = UserMapper.Map(entity = body).getOrNull()
                if(mappedUser != null) {
                    //call was successful
                    if(targetIsStudentOne) studentOne.value = mappedUser
                    else studentTwo.value = mappedUser
                }
            }
        }
        catch(e: Throwable) {
            Log.i("Error",e.message.toString() + " " +  e.cause)
        }
    }

    private suspend fun UpdateTimelineDisplayItems() {
        if (context != null) {
            val currentTrainingHandler = CurrentTrainingHandler(currentContext = context)

            if(allTrainingEvents.value != null) {
                this.allTrainingTimelineDisplayItems.emit(
                    TimelineDisplayHandler.ConvertTrainingEventsListToDisplayableTimelineEventsList(
                        allTrainingEvents = allTrainingEvents.value!!,
                        fromTimestamp = currentTrainingHandler.GetCurrentTrainingDateTimeAsCustomTimestamp(),
                        untilTimestamp = CustomTimestamp.GetCurrentDateAsTimestamp()
                    )
                )
            }
        }

    }

    fun LocalDateTimesAreWithinGivenHoursDifference(firstDate: LocalDateTime, secondDate: LocalDateTime, maxDifferenceInHours: Int): Boolean {
        val differenceInHours = Duration.between(firstDate, secondDate).toHours()
        if(differenceInHours <= maxDifferenceInHours && differenceInHours >= -maxDifferenceInHours) {
            return true
        }
        else return false
    }

    suspend fun GetAllTrainingEvents(trainingId: String,authKey: String) {
        try {
            val result = trainingApi.GetTrainingEvents(trainingId = trainingId.toLong(), authToken = authKey)
            val responseCode = result.raw().code
            val body = result.body()

            if(body != null && responseCode >= 200 && responseCode < 300) {
                //call was succesfull
                allTrainingEvents.emit(TrainingMapper.MapListTrainingEvents(body).reversed()) //to make the new training come on top
                UpdateTimelineDisplayItems()
            }

        }
        catch(e: Throwable) {
            Log.i("Error",e.message.toString() + " " +  e.cause + " trainingId = " + trainingId)
        }
    }

    fun NewPrescribedEvent(prescribedEvent: PrescribedEvent, currentTrainingId: String, authKey: String) {
        //since this was an extra functionality the base NewTrainingEvent kinda doesn't support this feature.
        //but we can simply set the _inputEventTitle, symbolName and feedbackMessage and then active the NewTrainingEvent

        //set the devidedfeedback info
        SetEventDescription(prescribedEvent.message)
        SetEventTitle(prescribedEvent.title)

        currentlySelectedIconForEvent.value = prescribedEvent.symbolName;
        //set the icon name
        viewModelScope.launch {
            NewTrainingEvent(currentTrainingId = currentTrainingId, authKey = authKey)
        }

    }

    suspend fun NewTrainingEvent(currentTrainingId: String, authKey: String) {
        try {
            val devidedFeedbackMessage = TrainingEvent.GetMessageAsReadableDevidedFeedback(feedbackContainer = devidedFeedbackContainer)
            val title = _inputEventTitle.value
            val symbolName = if(currentlySelectedIconForEvent.value != "") {
                //prescribed icon name is fully set in the NEwPrescribedEvent function, so it does not have  to be builded
                if(currentlySelectedIconForEvent.value.contains("fb_prescribed")) currentlySelectedIconForEvent.value
                else ("fb_manual_[" + currentlySelectedIconForEvent.value + "]")
            } else "fb_manual"

            if(devidedFeedbackMessage.length >= 3 && title.length >= 3) {
                val requestBody: EventRequestBody = EventRequestBody(
                    name = title,
                    symbol = symbolName,
                    timeStamp = CustomTimestamp.GetCurrentDateAsTimestamp(),
                    message = devidedFeedbackMessage,
                )

                val result = trainingApi.NewTrainingEvent(trainingId = currentTrainingId, body = requestBody, authToken = authKey)
                val responseCode = result.raw().code
                val body = result.body()


                if(body != null && responseCode >= 200 && responseCode < 300) {
                    //call was succesfull
                    //force refresh of the list
                    Toast.makeText(context,"Your feedback has been added", Toast.LENGTH_SHORT).show()
                    SetQuickFeedback("") //reset input field
                    GetAllTrainingEvents(trainingId = currentTrainingId, authKey = authKey) //load in
                }
                else {
                    Toast.makeText(context,"Something went wrong serverside when creating your feedback", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(context,"Your feedback message/title should at least contain 3 characters", Toast.LENGTH_SHORT).show()
            }

        }
        catch(e: Throwable) {
            Log.i("Error",e.message.toString() + " " +  e.cause)
        }
    }

    suspend fun DeleteTraining(targetTrainingId: String, authKey: String) {
        try {
            val result = trainingApi.DeleteTraining(trainingId = targetTrainingId, authToken = authKey)
            val responseCode = result.raw().code

            if(responseCode >= 200 && responseCode < 300) {
                //call was succesfull
                Log.i("Success","Succesfully deleted training $targetTrainingId")
            }

        }
        catch(e: Throwable) {
            Log.i("Error",e.message.toString() + " " +  e.cause)
        }
    }

    suspend fun DeleteTrainingEvent(currentTrainingId: String, authKey: String) {
        try {
            if(eventToEdit.value != null) {
                val result = trainingApi.DeleteTrainingEvent(trainingId = currentTrainingId, eventId = eventToEdit.value!!.id.toString(), authToken = authKey)
                val responseCode = result.raw().code

                if(responseCode >= 200 && responseCode < 300) {
                    //call was succesfull
                    GetAllTrainingEvents(trainingId = currentTrainingId, authKey = authKey) //refresh the training events list

                    //clear fields
                    SetEventTitle("")
                    //SetEventDescription("")
                }
            }
            else Log.i("Warning","EventToEdit was null")

        }
        catch(e: Throwable) {
            Log.i("Error",e.message.toString() + " " +  e.cause)
        }
    }

    suspend fun UpdateTrainingEvent(currentTrainingId: String,trainingEventId: String, authKey: String) {
        try {
            val devidedFeedbackMessage = TrainingEvent.GetMessageAsReadableDevidedFeedback(feedbackContainer = devidedFeedbackContainer)
            val title = _inputEventTitle.value
            val symbolName = if(eventToEdit.value != null) eventToEdit.value!!.symbol else "fb_manual"

            if(devidedFeedbackMessage.length >= 3 || title.length >= 3) {
                val requestBody: EventRequestBody = EventRequestBody(
                    name = title,
                    symbol = symbolName,
                    timeStamp = CustomTimestamp.GetCurrentDateAsTimestamp(),
                    message = devidedFeedbackMessage,
                )

                val result = trainingApi.PutTrainingEvent(trainingId = currentTrainingId,eventId = trainingEventId , body = requestBody, authToken = authKey)
                val responseCode = result.raw().code
                val body = result.body()

                if(body != null && responseCode >= 200 && responseCode < 300) {
                    //call was succesfull
                    //force refresh of the list
                    Toast.makeText(context,"Your feedback has been updated", Toast.LENGTH_SHORT).show()
                    SetQuickFeedback("") //reset input field
                    GetAllTrainingEvents(trainingId = currentTrainingId, authKey = authKey)
                }
            }
            else {
                Toast.makeText(context,"Your feedback/title message should at least contain 3 characters", Toast.LENGTH_SHORT).show()
            }

        }
        catch(e: Throwable) {
            Log.i("Error",e.message.toString() + " " +  e.cause)
        }
    }

    suspend fun NewQuickTrainingEvent(currentTrainingId: String, message: String, authKey: String) {
        try {
            if(message.length >= 3) {
                val requestBody: EventRequestBody = EventRequestBody(
                    name = "Quick feedback",
                    symbol = "fb_quick",
                    timeStamp = CustomTimestamp.GetCurrentDateAsTimestamp(),
                    message = message,
                )

                val result = trainingApi.NewTrainingEvent(trainingId = currentTrainingId, body = requestBody, authToken = authKey)
                val responseCode = result.raw().code
                val body = result.body()

                if(body != null && responseCode >= 200 && responseCode < 300) {
                    //call was succesfull
                    //force refresh of the list
                    Toast.makeText(context,"Your quick feedback has been added", Toast.LENGTH_SHORT).show()
                    SetQuickFeedback("") //reset input field
                    GetAllTrainingEvents(trainingId = currentTrainingId, authKey = authKey)
                }
                else {
                    //call failed
                    Log.i("Error","NewQuickTrainingEvent body was null or negative response code")
                }
            }
            else {
                Toast.makeText(context,"Your feedback message should at least contain 3 characters", Toast.LENGTH_SHORT).show()
            }

        }
        catch(e: Throwable) {
            Log.i("Error",e.message.toString() + " " +  e.cause)
        }
    }

    fun SetEventTitle(input: String) {
        _inputEventTitle.value = input
    }

    fun SetQuickFeedback(input: String) {
        if(input.length < 1000) {
            _inputQuickFeedback.value = input
        }
        else {
            if(context != null) Toast.makeText(context, "Maximum amount of characters for the event description has been reached", Toast.LENGTH_LONG).show()
        }
    }

    fun SetFeedbackTarget(newTarget: FeedbackTarget) {
        feedbackTarget.value = newTarget

        SwitchEventDescription()
    }

    fun SetEventDescription(input: String) {
        if(input.length < 1000) {
            _inputEventDescription.value = input

            if(feedbackTarget.value == FeedbackTarget.Everyone) devidedFeedbackContainer.everyone = input
            else if(feedbackTarget.value == FeedbackTarget.StudentOne) devidedFeedbackContainer.studentOne = input
            else devidedFeedbackContainer.studentTwo = input
        }
        else {
            if(context != null) Toast.makeText(context, "Maximum amount of characters for the event description has been reached", Toast.LENGTH_LONG).show()
        }

    }

    fun SetOrResetDevidedFeedbackInfo(feedbackContainer: DevidedFeedbackContainer) {
        SetFeedbackTarget(FeedbackTarget.Everyone)

        devidedFeedbackContainer = feedbackContainer
        SetEventDescription(feedbackContainer.GetFeedbackByFeedbackTarget(FeedbackTarget.Everyone))

    }

    private fun SetDevidedFeedbackContainerInfo(feedbackContainer: DevidedFeedbackContainer) {
        devidedFeedbackContainer = feedbackContainer
    }

    fun SwitchEventDescription() {
        Log.i("Values-FeedbackContainer", devidedFeedbackContainer.everyone + " , " + devidedFeedbackContainer.studentOne + " , " + devidedFeedbackContainer.studentTwo)
        Log.i("Value-FeedbackContainer", devidedFeedbackContainer.GetFeedbackByFeedbackTarget(feedbackTarget.value))
        _inputEventDescription.value = devidedFeedbackContainer.GetFeedbackByFeedbackTarget(feedbackTarget.value)
    }

    fun UpdateLargeVideoDisplayCameraLinkObject(newActiveObject: CameraLinkObject) {
        this.activeLargeVideoPlayerCameraLinkObject = newActiveObject
    }

    fun UpdateSmallVideoDisplayCameraLinkObject(newActiveObject: CameraLinkObject) {
        this.activeSmallVideoPlayerCameraLinkObject = newActiveObject
    }

    fun GetLargeVideoPlayerActiveCameraLinkObject(): CameraLinkObject {
        return activeLargeVideoPlayerCameraLinkObject
    }

    fun GetSmallVideoPlayerActiveCameraLinkObject(): CameraLinkObject {
        return activeSmallVideoPlayerCameraLinkObject
    }

    fun ToggleOptionMenu() {
        optionMenuIsOpen.value = !optionMenuIsOpen.value
    }

    fun ToggleSmallScreenFunctionTarget() {
        smallLeftBoxTargetIsSmallVideoPlayer.value = !smallLeftBoxTargetIsSmallVideoPlayer.value
    }


    fun OpenPopUpScreenEditEvent(trainingEvent: TrainingEvent, type: BasePopUpScreen) {
        eventToEdit.value = trainingEvent

        SelectIconForEvent(trainingEvent.symbol)
        SetEventTitle(trainingEvent.name)
        SetOrResetDevidedFeedbackInfo(trainingEvent.devidedFeedbackContainer)
        OpenPopUpScreen(type)
    }

    fun SelectIconForEvent(symbolName: String) {
        currentlySelectedIconForEvent.value = symbolName
    }

    fun ToggleIconPickerPopUp(onCloseReset: Boolean) {
        //user is closing icon picker and confirming his/her choice:
        if(onCloseReset == false && eventToEdit.value != null && iconPickerIsOpen.value == true) eventToEdit.value!!.SetNewSymbolName(currentlySelectedIconForEvent.value)

        iconPickerIsOpen.value = !iconPickerIsOpen.value

        if(!iconPickerIsOpen.value && onCloseReset && eventToEdit.value != null) currentlySelectedIconForEvent.value = eventToEdit.value!!.symbol
        else if(!iconPickerIsOpen.value && onCloseReset) currentlySelectedIconForEvent.value = ""
    }

    fun OpenPopUpScreen(type: BasePopUpScreen) {
        optionMenuIsOpen.value = false

        //opens the actual dynamic pop up screen for the live training
        popUpScreenType.value = type
        popUpScreenIsOpen.value = true

        //checks if some input field texts have to be changed/reset
        if(type.type == PopUpType.ADD_EVENT) {
            eventToEdit.value = null
            currentlySelectedIconForEvent.value = ""
            _inputEventTitle.value = ""
            _inputEventDescription.value = ""
            SetOrResetDevidedFeedbackInfo(feedbackContainer = DevidedFeedbackContainer())
        }
    }

    fun ClosePopUpScreen() {
        iconPickerIsOpen.value = false
        popUpScreenIsOpen.value = false
    }

    fun TogglePopUpScreen() {
        popUpScreenIsOpen.value = !popUpScreenIsOpen.value
    }

    fun GetNewCameraPlayer(url: String): ExoPlayer {
        var rendersFactory = DefaultRenderersFactory(context).forceEnableMediaCodecAsynchronousQueueing() //prevents buffering memory leaks

        var newExoPlayer: ExoPlayer = ExoPlayer.Builder(context).setRenderersFactory(rendersFactory).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            repeatMode = ExoPlayer.REPEAT_MODE_ONE
            playWhenReady = true

            prepare()
            //play()
        }

        return newExoPlayer
    }

    fun DisplayFullscreenVideoplayer(targetIsLargeVideoDisplay: Boolean) {
//        //I have disabled fullscreen functionality because HideFullScreenVideoplayer is causing memory leaks
//
//        ToggleFullScreenVideoDisplay()
//
//        if(targetIsLargeVideoDisplay) {
//            fullScreenVideoPlayer.value = GetNewCameraPlayer(activeLargeVideoPlayerCameraLinkObject.url)
//        }
//        else {
//            //target was small camera display
//            fullScreenVideoPlayer.value = GetNewCameraPlayer(activeSmallVideoPlayerCameraLinkObject.url)
//        }
    }

    fun HideFullScreenVideoplayer() {
        if(fullScreenVideoPlayer.value != null) {
            fullScreenVideoPlayer.value!!.stop()
            fullScreenVideoPlayer.value!!.removeMediaItems(0,1)
            fullScreenVideoPlayer.value!!.release()

            //fullScreenVideoPlayer.value = null
        }

        ToggleFullScreenVideoDisplay()
    }

    fun ToggleFullScreenVideoDisplay() {
        //fullScreenVideoIsOpen.value = !fullScreenVideoIsOpen.value
    }

    fun DoubleTapNextVideo(targetIsLargeVideoDisplay: Boolean) {
        var videoPlayerCameraCurrentLinkObject: CameraLinkObject? = null
        if(targetIsLargeVideoDisplay) videoPlayerCameraCurrentLinkObject = activeLargeVideoPlayerCameraLinkObject
        else videoPlayerCameraCurrentLinkObject = activeSmallVideoPlayerCameraLinkObject

        val currIndex = doubleTapCameraLinkOrder.indexOf(videoPlayerCameraCurrentLinkObject)

        if(currIndex < doubleTapCameraLinkOrder.size - 1) {
            if(targetIsLargeVideoDisplay) SwitchLargeVideoDisplay(doubleTapCameraLinkOrder[currIndex + 1])
            else SwitchSmallVideoDisplay(doubleTapCameraLinkOrder[currIndex + 1])
        }
        else {
            if(targetIsLargeVideoDisplay) SwitchLargeVideoDisplay(doubleTapCameraLinkOrder[0])
            else SwitchSmallVideoDisplay(doubleTapCameraLinkOrder[0])
        }
    }

//    fun InitLargeVideoDisplay(initialCameraLinkObject: CameraLinkObject): VideoDisplayer {
//
//        val newLargeVideoDisplayer = VideoDisplayer(videoPlayer = GetNewCameraPlayer(initialCameraLinkObject.url), initialCameraLinkObject = initialCameraLinkObject)
//
//        //largeVideoDisplay = newLargeVideoDisplayer
//        largeVideoPlayer.value = newLargeVideoDisplayer.videoPlayer
//
//        return newLargeVideoDisplayer
//    }
//
//    fun InitSmallVideoDisplay(initialCameraLinkObject: CameraLinkObject): VideoDisplayer {
//        val newSmallVideoDisplayer = VideoDisplayer(videoPlayer = GetNewCameraPlayer(initialCameraLinkObject.url), initialCameraLinkObject = initialCameraLinkObject)
//        smallVideoDisplay = newSmallVideoDisplayer
//
//        smallVideoPlayer.value = newSmallVideoDisplayer.videoPlayer
//
//        return smallVideoDisplay
//    }

    fun SwitchLargeVideoDisplay(cameraLinkObject: CameraLinkObject) {
        UpdateLargeVideoDisplayCameraLinkObject(cameraLinkObject)

        if(largeVideoPlayer.value != null) largeVideoPlayer.value!!.release()
        largeVideoPlayer.value = GetNewCameraPlayer(cameraLinkObject.url)
        largeVideoPlayer.value!!.prepare()

        //THIS WILL FORCE A RECOMPESITION OF THE LARGE VIDEO PLAYER OTHERWISE IT WOULD NEVER UPDATE
        //I tried everything but only this solution worked
        largeVideoIsChanged.value = !largeVideoIsChanged.value

    }

    fun SwitchSmallVideoDisplay(cameraLinkObject: CameraLinkObject) {
        UpdateSmallVideoDisplayCameraLinkObject(cameraLinkObject)

        if(smallVideoPlayer.value != null) smallVideoPlayer.value!!.release()
        smallVideoPlayer.value = GetNewCameraPlayer(cameraLinkObject.url)
        smallVideoPlayer.value!!.prepare()

        smallVideoIsChanged.value = !smallVideoIsChanged.value
    }

    fun NavigateToPage(navigateTo: ScreenNavName) {
//        largeVideoDisplay.videoPlayer.release() //this is a different one then the one that is disposed on screen
//        smallVideoDisplay.videoPlayer.release() //because these videoplayers wouldn't switch video when the video-url was updated
                                                //but I will keep the structure inside the application when this bug with videoplayer is fixed it can be implemented with
                                                //low effort
        if(largeVideoPlayer.value != null) largeVideoPlayer.value!!.release()
        if(smallVideoPlayer.value != null) smallVideoPlayer.value!!.release()

        navController.navigate(route = navigateTo.route) {
            popUpTo(navigateTo.route) {
                inclusive = true
            }
        }
    }

}