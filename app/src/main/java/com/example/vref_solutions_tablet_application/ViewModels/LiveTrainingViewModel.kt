package com.example.vref_solutions_tablet_application.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.vref_solutions_tablet_application.api.requestBodies.EventRequestBody
import com.example.vref_solutions_tablet_application.api.requestBodies.StopTrainingRequestBody
import com.example.vref_solutions_tablet_application.api.TrainingApi
import com.example.vref_solutions_tablet_application.api.UserApi
import com.example.vref_solutions_tablet_application.enums.FeedbackTarget
import com.example.vref_solutions_tablet_application.enums.PopUpType
import com.example.vref_solutions_tablet_application.handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.handlers.TimelineDisplayHandler
import com.example.vref_solutions_tablet_application.mappers.TrainingMapper
import com.example.vref_solutions_tablet_application.mappers.UserMapper
import com.example.vref_solutions_tablet_application.models.*
import com.example.vref_solutions_tablet_application.models.popUpModels.AddEvent
import com.example.vref_solutions_tablet_application.models.popUpModels.BasePopUpScreen
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.apiretrofit.RetrofitApiHandler
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

    private val trainingApi: TrainingApi = RetrofitApiHandler.getTrainingsApi()
    private val userApi: UserApi = RetrofitApiHandler.getUsersApi()

    fun initVideoPlayers() {
        largeVideoPlayer.value = getNewCameraPlayer(activeLargeVideoPlayerCameraLinkObject.url)
        smallVideoPlayer.value = getNewCameraPlayer(activeSmallVideoPlayerCameraLinkObject.url)
    }

    fun finishCurrentTraining() {
        try {
            val currentTrainingHandler: CurrentTrainingHandler = CurrentTrainingHandler(currentContext = getApplication<Application>().baseContext)
            val currentTrainingId: Long = currentTrainingHandler.getCurrentTrainingId().toLong()

            viewModelScope.launch {
                val result = trainingApi.stopTrainingById(trainingId = currentTrainingId,body = StopTrainingRequestBody(), authToken = currentTrainingHandler.getAuthKey())
                val responseCode = result.raw().code

                if(responseCode >= 200 && responseCode < 300) {
                    //call was succesfull
                    Toast.makeText(getApplication<Application>().baseContext, "Succesfully finished the training", Toast.LENGTH_LONG).show()
                    navigateToPage(ScreenNavName.MainMenu)
                }
                else Toast.makeText(getApplication<Application>().baseContext, "Could not finished the training", Toast.LENGTH_LONG).show() //call failed
            }
        }
        catch(e: Throwable) {
            Log.i("ERRROR","Couldn't finish training")
            Log.i("Error",e.message.toString() + " " +  e.cause)
        }
    }

    fun launchLoadingOfNecessaryStartingData(currentTrainingInfoHandler: CurrentTrainingHandler) {
        viewModelScope.launch {
            getAllTrainingEvents(trainingId = currentTrainingInfoHandler.getCurrentTrainingId(), authKey = currentTrainingInfoHandler.getAuthKey())
            getStudentInfo(id = currentTrainingInfoHandler.getFirstStudentId().toLong(), authKey = currentTrainingInfoHandler.getAuthKey(), targetIsStudentOne = true)
            getStudentInfo(id = currentTrainingInfoHandler.getSecondStudentId().toLong(), authKey = currentTrainingInfoHandler.getAuthKey(), targetIsStudentOne = false)
        }
    }

    suspend fun getStudentInfo(id: Long, authKey: String, targetIsStudentOne: Boolean) {
        try {
            //getUserById
            val result = userApi.getUserById(userId = id,authToken = authKey)
            val responseCode = result.raw().code
            val body = result.body()
            //Log.i("TEST",body?.size.toString())

            if(body != null && responseCode >= 200 && responseCode < 300) {
                val mappedUser = UserMapper.map(entity = body).getOrNull()
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

    private suspend fun updateTimelineDisplayItems() {
        if (getApplication<Application>().baseContext != null) {
            val currentTrainingHandler = CurrentTrainingHandler(currentContext = getApplication<Application>().baseContext)

            if(allTrainingEvents.value != null) {
                this.allTrainingTimelineDisplayItems.emit(
                    TimelineDisplayHandler.convertTrainingEventsListToDisplayableTimelineEventsList(
                        allTrainingEvents = allTrainingEvents.value!!,
                        fromTimestamp = currentTrainingHandler.getCurrentTrainingDateTimeAsCustomTimestamp(),
                        untilTimestamp = CustomTimestamp.getCurrentDateAsTimestamp()
                    )
                )
            }
        }

    }

    fun localDateTimesAreWithinGivenHoursDifference(firstDate: LocalDateTime, secondDate: LocalDateTime, maxDifferenceInHours: Int): Boolean {
        val differenceInHours = Duration.between(firstDate, secondDate).toHours()
        if(differenceInHours <= maxDifferenceInHours && differenceInHours >= -maxDifferenceInHours) {
            return true
        }
        else return false
    }

    fun launchGetAllTrainingEvents(trainingId: String, authKey: String) {
        viewModelScope.launch {
            getAllTrainingEvents(trainingId = trainingId, authKey = authKey)
        }
    }

    suspend fun getAllTrainingEvents(trainingId: String, authKey: String) {
        try {
            val result = trainingApi.getTrainingEvents(trainingId = trainingId.toLong(), authToken = authKey)
            val responseCode = result.raw().code
            val body = result.body()

            if(body != null && responseCode >= 200 && responseCode < 300) {
                //call was succesfull
                allTrainingEvents.emit(TrainingMapper.mapListTrainingEvents(body).reversed()) //to make the new training come on top
                updateTimelineDisplayItems()
            }

        }
        catch(e: Throwable) {
            Log.i("Error",e.message.toString() + " " +  e.cause + " trainingId = " + trainingId)
        }
    }

    fun newPrescribedEvent(prescribedEvent: PrescribedEvent, currentTrainingId: String, authKey: String) {
        //since this was an extra functionality the base NewTrainingEvent kinda doesn't support this feature.
        //but we can simply set the _inputEventTitle, symbolName and feedbackMessage and then active the NewTrainingEvent

        //set the devidedfeedback info
        setEventDescription(prescribedEvent.message)
        setEventTitle(prescribedEvent.title)

        currentlySelectedIconForEvent.value = prescribedEvent.symbolName;
        //set the icon name
        viewModelScope.launch {
            newTrainingEvent(currentTrainingId = currentTrainingId, authKey = authKey)
        }

    }

    fun launchNewTrainingEvent() {
        val currentTrainingHandler = CurrentTrainingHandler(currentContext = getApplication<Application>().baseContext)
        viewModelScope.launch {
            newTrainingEvent(currentTrainingId = currentTrainingHandler.getCurrentTrainingId(), authKey = currentTrainingHandler.getAuthKey())
        }
    }

    suspend fun newTrainingEvent(currentTrainingId: String, authKey: String) {
        try {
            val devidedFeedbackMessage = TrainingEvent.getMessageAsReadableDevidedFeedback(feedbackContainer = devidedFeedbackContainer)
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
                    timeStamp = CustomTimestamp.getCurrentDateAsTimestamp(),
                    message = devidedFeedbackMessage,
                )

                val result = trainingApi.newTrainingEvent(trainingId = currentTrainingId, body = requestBody, authToken = authKey)
                val responseCode = result.raw().code
                val body = result.body()


                if(body != null && responseCode >= 200 && responseCode < 300) {
                    //call was succesfull
                    //force refresh of the list
                    Toast.makeText(getApplication<Application>().baseContext,"Your feedback has been added", Toast.LENGTH_SHORT).show()
                    setQuickFeedback("") //reset input field
                    getAllTrainingEvents(trainingId = currentTrainingId, authKey = authKey) //load in
                }
                else {
                    Toast.makeText(getApplication<Application>().baseContext,"Something went wrong serverside when creating your feedback", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(getApplication<Application>().baseContext,"Your feedback message/title should at least contain 3 characters", Toast.LENGTH_SHORT).show()
            }

        }
        catch(e: Throwable) {
            Log.i("Error",e.message.toString() + " " +  e.cause)
        }
    }

    fun launchDeleteTraining(currentTrainingHandler: CurrentTrainingHandler) {
        viewModelScope.launch {
            deleteTraining(targetTrainingId = currentTrainingHandler.getCurrentTrainingId(), authKey = currentTrainingHandler.getAuthKey())

            navigateToPage(ScreenNavName.MainMenu)
        }
    }

    suspend fun deleteTraining(targetTrainingId: String, authKey: String) {
        try {
            val result = trainingApi.deleteTraining(trainingId = targetTrainingId, authToken = authKey)
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

    fun launchDeleteTrainingEvent(currentTrainingHandler: CurrentTrainingHandler) {
        viewModelScope.launch {
            deleteTrainingEvent(
                currentTrainingId = currentTrainingHandler.getCurrentTrainingId(),
                authKey = currentTrainingHandler.getAuthKey()
            )

            togglePopUpScreen()
        }
    }

    suspend fun deleteTrainingEvent(currentTrainingId: String, authKey: String) {
        try {
            if(eventToEdit.value != null) {
                val result = trainingApi.deleteTrainingEvent(trainingId = currentTrainingId, eventId = eventToEdit.value!!.id.toString(), authToken = authKey)
                val responseCode = result.raw().code

                if(responseCode >= 200 && responseCode < 300) {
                    //call was succesfull
                    getAllTrainingEvents(trainingId = currentTrainingId, authKey = authKey) //refresh the training events list

                    //clear fields
                    setEventTitle("")
                    //SetEventDescription("")
                }
            }
            else Log.i("Warning","EventToEdit was null")

        }
        catch(e: Throwable) {
            Log.i("Error",e.message.toString() + " " +  e.cause)
        }
    }

    fun launchUpdateTrainingEvent() {
        val currentTrainingHandler: CurrentTrainingHandler = CurrentTrainingHandler(currentContext = getApplication<Application>().baseContext)
        viewModelScope.launch {
            if(uiEventToEdit.value != null) {
                updateTrainingEvent(currentTrainingId = currentTrainingHandler.getCurrentTrainingId(), authKey = currentTrainingHandler.getAuthKey(),
                    trainingEventId = uiEventToEdit.value!!.id.toString())

                //clear previous input
                setEventTitle("")
            }

        }
    }

    suspend fun updateTrainingEvent(currentTrainingId: String, trainingEventId: String, authKey: String) {
        try {
            val devidedFeedbackMessage = TrainingEvent.getMessageAsReadableDevidedFeedback(feedbackContainer = devidedFeedbackContainer)
            val title = _inputEventTitle.value
            val symbolName = if(eventToEdit.value != null) eventToEdit.value!!.symbol else "fb_manual"

            if(devidedFeedbackMessage.length >= 3 || title.length >= 3) {
                val requestBody: EventRequestBody = EventRequestBody(
                    name = title,
                    symbol = symbolName,
                    timeStamp = CustomTimestamp.getCurrentDateAsTimestamp(),
                    message = devidedFeedbackMessage,
                )

                val result = trainingApi.putTrainingEvent(trainingId = currentTrainingId,eventId = trainingEventId , body = requestBody, authToken = authKey)
                val responseCode = result.raw().code
                val body = result.body()

                if(body != null && responseCode >= 200 && responseCode < 300) {
                    //call was succesfull
                    //force refresh of the list
                    Toast.makeText(getApplication<Application>().baseContext,"Your feedback has been updated", Toast.LENGTH_SHORT).show()
                    setQuickFeedback("") //reset input field
                    getAllTrainingEvents(trainingId = currentTrainingId, authKey = authKey)
                }
            }
            else {
                Toast.makeText(getApplication<Application>().baseContext,"Your feedback/title message should at least contain 3 characters", Toast.LENGTH_SHORT).show()
            }

        }
        catch(e: Throwable) {
            Log.i("Error",e.message.toString() + " " +  e.cause)
        }
    }

    fun launchNewQuickTrainingEvent(currentTrainingId: String, message: String, authKey: String) {
        viewModelScope.launch {
            newQuickTrainingEvent(currentTrainingId = currentTrainingId, message = message, authKey = authKey)
        }
    }

    suspend fun newQuickTrainingEvent(currentTrainingId: String, message: String, authKey: String) {
        try {
            if(message.length >= 3) {
                val requestBody: EventRequestBody = EventRequestBody(
                    name = "Quick feedback",
                    symbol = "fb_quick",
                    timeStamp = CustomTimestamp.getCurrentDateAsTimestamp(),
                    message = message,
                )

                val result = trainingApi.newTrainingEvent(trainingId = currentTrainingId, body = requestBody, authToken = authKey)
                val responseCode = result.raw().code
                val body = result.body()

                if(body != null && responseCode >= 200 && responseCode < 300) {
                    //call was succesfull
                    //force refresh of the list
                    Toast.makeText(getApplication<Application>().baseContext,"Your quick feedback has been added", Toast.LENGTH_SHORT).show()
                    setQuickFeedback("") //reset input field
                    getAllTrainingEvents(trainingId = currentTrainingId, authKey = authKey)
                }
                else {
                    //call failed
                    Log.i("Error","NewQuickTrainingEvent body was null or negative response code")
                }
            }
            else {
                Toast.makeText(getApplication<Application>().baseContext,"Your feedback message should at least contain 3 characters", Toast.LENGTH_SHORT).show()
            }

        }
        catch(e: Throwable) {
            Log.i("Error",e.message.toString() + " " +  e.cause)
        }
    }

    fun setEventTitle(input: String) {
        _inputEventTitle.value = input
    }

    fun setQuickFeedback(input: String) {
        if(input.length < 1000) {
            _inputQuickFeedback.value = input
        }
        else {
            if(getApplication<Application>().baseContext != null) Toast.makeText(getApplication<Application>().baseContext, "Maximum amount of characters for the event description has been reached", Toast.LENGTH_LONG).show()
        }
    }

    fun setFeedbackTarget(newTarget: FeedbackTarget) {
        feedbackTarget.value = newTarget

        switchEventDescription()
    }

    fun setEventDescription(input: String) {
        if(input.length < 1000) {
            _inputEventDescription.value = input

            if(feedbackTarget.value == FeedbackTarget.Everyone) devidedFeedbackContainer.everyone = input
            else if(feedbackTarget.value == FeedbackTarget.StudentOne) devidedFeedbackContainer.studentOne = input
            else devidedFeedbackContainer.studentTwo = input
        }
        else {
            if(getApplication<Application>().baseContext != null) Toast.makeText(getApplication<Application>().baseContext, "Maximum amount of characters for the event description has been reached", Toast.LENGTH_LONG).show()
        }

    }

    fun setOrResetDevidedFeedbackInfo(feedbackContainer: DevidedFeedbackContainer) {
        setFeedbackTarget(FeedbackTarget.Everyone)

        devidedFeedbackContainer = feedbackContainer
        setEventDescription(feedbackContainer.getFeedbackByFeedbackTarget(FeedbackTarget.Everyone))

    }

    private fun setDevidedFeedbackContainerInfo(feedbackContainer: DevidedFeedbackContainer) {
        devidedFeedbackContainer = feedbackContainer
    }

    fun switchEventDescription() {
        Log.i("Values-FeedbackContainer", devidedFeedbackContainer.everyone + " , " + devidedFeedbackContainer.studentOne + " , " + devidedFeedbackContainer.studentTwo)
        Log.i("Value-FeedbackContainer", devidedFeedbackContainer.getFeedbackByFeedbackTarget(feedbackTarget.value))
        _inputEventDescription.value = devidedFeedbackContainer.getFeedbackByFeedbackTarget(feedbackTarget.value)
    }

    fun updateLargeVideoDisplayCameraLinkObject(newActiveObject: CameraLinkObject) {
        this.activeLargeVideoPlayerCameraLinkObject = newActiveObject
    }

    fun updateSmallVideoDisplayCameraLinkObject(newActiveObject: CameraLinkObject) {
        this.activeSmallVideoPlayerCameraLinkObject = newActiveObject
    }

    fun getLargeVideoPlayerActiveCameraLinkObject(): CameraLinkObject {
        return activeLargeVideoPlayerCameraLinkObject
    }

    fun getSmallVideoPlayerActiveCameraLinkObject(): CameraLinkObject {
        return activeSmallVideoPlayerCameraLinkObject
    }

    fun toggleOptionMenu() {
        optionMenuIsOpen.value = !optionMenuIsOpen.value
    }

    fun toggleSmallScreenFunctionTarget() {
        smallLeftBoxTargetIsSmallVideoPlayer.value = !smallLeftBoxTargetIsSmallVideoPlayer.value
    }


    fun openPopUpScreenEditEvent(trainingEvent: TrainingEvent, type: BasePopUpScreen) {
        eventToEdit.value = trainingEvent

        selectIconForEvent(trainingEvent.symbol)
        setEventTitle(trainingEvent.name)
        setOrResetDevidedFeedbackInfo(trainingEvent.devidedFeedbackContainer)
        openPopUpScreen(type)
    }

    fun selectIconForEvent(symbolName: String) {
        currentlySelectedIconForEvent.value = symbolName
    }

    fun toggleIconPickerPopUp(onCloseReset: Boolean) {
        //user is closing icon picker and confirming his/her choice:
        if(onCloseReset == false && eventToEdit.value != null && iconPickerIsOpen.value == true) eventToEdit.value!!.setNewSymbolName(currentlySelectedIconForEvent.value)

        iconPickerIsOpen.value = !iconPickerIsOpen.value

        if(!iconPickerIsOpen.value && onCloseReset && eventToEdit.value != null) currentlySelectedIconForEvent.value = eventToEdit.value!!.symbol
        else if(!iconPickerIsOpen.value && onCloseReset) currentlySelectedIconForEvent.value = ""
    }

    fun openPopUpScreen(type: BasePopUpScreen) {
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
            setOrResetDevidedFeedbackInfo(feedbackContainer = DevidedFeedbackContainer())
        }
    }

    fun closePopUpScreen() {
        iconPickerIsOpen.value = false
        popUpScreenIsOpen.value = false
    }

    fun togglePopUpScreen() {
        popUpScreenIsOpen.value = !popUpScreenIsOpen.value
    }

    fun getNewCameraPlayer(url: String): ExoPlayer {
        var rendersFactory = DefaultRenderersFactory(getApplication<Application>().baseContext).forceEnableMediaCodecAsynchronousQueueing() //prevents buffering memory leaks

        var newExoPlayer: ExoPlayer = ExoPlayer.Builder(getApplication<Application>().baseContext).setRenderersFactory(rendersFactory).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            repeatMode = ExoPlayer.REPEAT_MODE_ONE
            playWhenReady = true

            prepare()
            //play()
        }

        return newExoPlayer
    }

    fun displayFullscreenVideoplayer(targetIsLargeVideoDisplay: Boolean) {
        //I have disabled fullscreen functionality because HideFullScreenVideoplayer is causing memory leaks for some reason
        //while the exoplayer does have a OnDispose function and the player gets released in HideFullScreenVideoPlayer()
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

    fun hideFullScreenVideoplayer() {
        if(fullScreenVideoPlayer.value != null) {
            fullScreenVideoPlayer.value!!.stop()
            fullScreenVideoPlayer.value!!.removeMediaItems(0,1)
            fullScreenVideoPlayer.value!!.release()

            //fullScreenVideoPlayer.value = null
        }

        toggleFullScreenVideoDisplay()
    }

    fun toggleFullScreenVideoDisplay() {
        //fullScreenVideoIsOpen.value = !fullScreenVideoIsOpen.value
    }

    fun doubleTapNextVideo(targetIsLargeVideoDisplay: Boolean) {
        var videoPlayerCameraCurrentLinkObject: CameraLinkObject? = null
        if(targetIsLargeVideoDisplay) videoPlayerCameraCurrentLinkObject = activeLargeVideoPlayerCameraLinkObject
        else videoPlayerCameraCurrentLinkObject = activeSmallVideoPlayerCameraLinkObject

        val currIndex = doubleTapCameraLinkOrder.indexOf(videoPlayerCameraCurrentLinkObject)

        if(currIndex < doubleTapCameraLinkOrder.size - 1) {
            if(targetIsLargeVideoDisplay) switchLargeVideoDisplay(doubleTapCameraLinkOrder[currIndex + 1])
            else switchSmallVideoDisplay(doubleTapCameraLinkOrder[currIndex + 1])
        }
        else {
            if(targetIsLargeVideoDisplay) switchLargeVideoDisplay(doubleTapCameraLinkOrder[0])
            else switchSmallVideoDisplay(doubleTapCameraLinkOrder[0])
        }
    }

    fun switchLargeVideoDisplay(cameraLinkObject: CameraLinkObject) {
        updateLargeVideoDisplayCameraLinkObject(cameraLinkObject)

        if(largeVideoPlayer.value != null) largeVideoPlayer.value!!.release()
        largeVideoPlayer.value = getNewCameraPlayer(cameraLinkObject.url)
        largeVideoPlayer.value!!.prepare()

        //THIS WILL FORCE A RECOMPESITION OF THE LARGE VIDEO PLAYER OTHERWISE IT WOULD NEVER UPDATE
        //I tried everything but only this solution worked
        largeVideoIsChanged.value = !largeVideoIsChanged.value

    }

    fun switchSmallVideoDisplay(cameraLinkObject: CameraLinkObject) {
        updateSmallVideoDisplayCameraLinkObject(cameraLinkObject)

        if(smallVideoPlayer.value != null) smallVideoPlayer.value!!.release()
        smallVideoPlayer.value = getNewCameraPlayer(cameraLinkObject.url)
        smallVideoPlayer.value!!.prepare()

        smallVideoIsChanged.value = !smallVideoIsChanged.value
    }

    fun navigateToPage(navigateTo: ScreenNavName) {
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