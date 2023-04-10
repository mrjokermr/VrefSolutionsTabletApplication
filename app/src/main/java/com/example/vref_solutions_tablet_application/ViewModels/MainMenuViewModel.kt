package com.example.vref_solutions_tablet_application.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.vref_solutions_tablet_application.api.TrainingApi
import com.example.vref_solutions_tablet_application.api.UserApi
import com.example.vref_solutions_tablet_application.enums.TrainingStatus
import com.example.vref_solutions_tablet_application.handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.handlers.FeedbackEventFilterHandler
import com.example.vref_solutions_tablet_application.handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.mappers.TrainingMapper
import com.example.vref_solutions_tablet_application.mappers.UserMapper
import com.example.vref_solutions_tablet_application.models.DateSelectAndDisplayObject
import com.example.vref_solutions_tablet_application.models.TrainingEvent
import com.example.vref_solutions_tablet_application.models.TrainingSummary
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.apiretrofit.RetrofitApiHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate


class MainMenuViewModel(application: Application) : AndroidViewModel(application)  {
    lateinit var navController: NavController

    private val currentlySelectedTraining: MutableStateFlow<TrainingSummary?> = MutableStateFlow(null)
    val uiCurrentlySelectedTraining: StateFlow<TrainingSummary?> = currentlySelectedTraining

    private val allTrainingSummaries: MutableStateFlow<List<TrainingSummary>?> = MutableStateFlow(null)
    val uiAllTrainingSummaries: StateFlow<List<TrainingSummary>?> = allTrainingSummaries

    private val allTrainingEventsForSelectedTraining: MutableStateFlow<List<TrainingEvent>?> = MutableStateFlow(null)
    val uiAllTrainingEventsForSelectedTraining: StateFlow<List<TrainingEvent>?> = allTrainingEventsForSelectedTraining

    private val fromDateDisplay: MutableStateFlow<DateSelectAndDisplayObject> = MutableStateFlow(DateSelectAndDisplayObject(LocalDate.of(2022,12,1), displayText = "From"))
    val uiFromDateDisplay: StateFlow<DateSelectAndDisplayObject> = fromDateDisplay

    private val toDateDisplay: MutableStateFlow<DateSelectAndDisplayObject> = MutableStateFlow(DateSelectAndDisplayObject(LocalDate.now(), displayText = "To") )
    val uiToDateDisplay: StateFlow<DateSelectAndDisplayObject> = toDateDisplay

    private val feedbackEventFilterHandler = FeedbackEventFilterHandler()

    private val userApi: UserApi = RetrofitApiHandler.getUsersApi()
    private val trainingApi: TrainingApi = RetrofitApiHandler.getTrainingsApi()

    fun userIsInstructorOrHigher(): Boolean {
        val userInfoHandler: LoggedInUserHandler = LoggedInUserHandler(currentContext = getApplication<Application>().baseContext)

        var correctRights = false

        correctRights = userInfoHandler.userIsInstructor()


        return correctRights
    }

    fun getInstructorFeedbackFilterFlow(): StateFlow<Boolean> {
        return feedbackEventFilterHandler.removeInstructorFeedbackFlow
    }

    fun toggleManualInstructorFeedbackFilter() {
        feedbackEventFilterHandler.toggleManualInstructorFeedbackFilter()
    }

    fun toggleQuickInstructorFeedbackFilter() {
        feedbackEventFilterHandler.toggleQuickInstructorFeedbackFilter()
    }

    fun togglePrescribedEventsFeedbackFilter() {
        feedbackEventFilterHandler.togglePrescribedEventsFeedbackFilter()
    }

    fun toggleInstructorFeedbackEventFilter() {
        feedbackEventFilterHandler.toggleInstructorFeedbackEventFilter()
    }

    fun toggleAutomaticFeedbackEventFilter() {
        feedbackEventFilterHandler.toggleAutomaticFeedbackEventFilter()
    }

    fun updateCurrentEventsWithFilter(trainingId: Long, authKey: String) {
        viewModelScope.launch {
            loadAllTrainingEvents(trainingId = trainingId, authKey = authKey)
        }
    }

    fun launchLoadAndSetStudentNames(authKey: String,firstStudentId: Long, secondStudentId: Long, firstStudentNameFlow: MutableStateFlow<String>, secondStudentNameFlow: MutableStateFlow<String>) {
        viewModelScope.launch {
            loadStudentName(id = firstStudentId, authKey = authKey, studentNameFlow = firstStudentNameFlow)
            loadStudentName(id = secondStudentId, authKey = authKey, studentNameFlow = secondStudentNameFlow)
        }
    }

    suspend fun loadStudentName(id: Long, authKey: String, studentNameFlow: MutableStateFlow<String>) {
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
                    studentNameFlow.value = mappedUser.fullName()
                }
                else studentNameFlow.value = "Unknown"

            }
            else {
                //call failed
                studentNameFlow.value = "Unknown"
            }
        }
        catch(e: Throwable) {
            studentNameFlow.value = "Unknown - Error"
        }
    }

    fun launchGetAllTrainingSummaries() {
        viewModelScope.launch {
            getAllTrainingSummaries(filterByDateAfter = true)
        }
    }

    suspend fun getAllTrainingSummaries(filterByDateAfter: Boolean) {
        val userInfoHandler: LoggedInUserHandler = LoggedInUserHandler(currentContext = getApplication<Application>().baseContext)

        try {
            val result = trainingApi.getAllTrainingInfoSummaries(authToken = userInfoHandler.getAuthKey())
            val responseCode = result.raw().code
            val body = result.body()
            //Log.i("TEST",body?.size.toString())

            if(body != null && responseCode >= 200 && responseCode < 300) {
                //call was successful
                emitAndPutUnfinishedTrainingTopOfTheList(responseList = TrainingMapper.mapListTrainingSummary(body).reversed().toMutableList()) //added reversed so that the newest date trainings are ontop
                if(filterByDateAfter) {
                    displayTrainingByDate()
                }
                //allTrainingSummaries.emit(body)
            }
            else {
                if(body == null && responseCode >= 200 && responseCode < 300) allTrainingSummaries.emit(emptyList())
                //call failed
                Log.i("Error","GetAllTrainingSummaries body was null or negative response code $responseCode")
            }
        }
        catch(e: Throwable) {
            Log.i("Error",e.message.toString() + " " +  e.cause)
        }
    }

    private suspend fun emitAndPutUnfinishedTrainingTopOfTheList(responseList: MutableList<TrainingSummary>) {
        var continueTrainingSumList = mutableListOf<TrainingSummary>()

        for(trainingSum in responseList) {
            if(!trainingSum.isViewable()) {
                if(trainingSum.status == TrainingStatus.Processing) Log.i("Opentraining-Id",trainingSum.id.toString())
                continueTrainingSumList.add(trainingSum)
            }
        }

        for(trainingSum in continueTrainingSumList) {
            responseList.remove(trainingSum)
            responseList.add(0,trainingSum)
        }

        allTrainingSummaries.emit(responseList)
    }

    suspend fun displayTrainingByDate() {
        //val allTrainingSummaries: MutableStateFlow<List<TrainingSummary>?> = MutableStateFlow(null)
        val filteredTrainingSumList: MutableList<TrainingSummary> = emptyList<TrainingSummary>().toMutableList()

        if(this.allTrainingSummaries.value != null) {

            for(trainingSum in this.allTrainingSummaries.value!!) {

                val d = trainingSum.getLocalDate()
                if(d.isAfter(fromDateDisplay.value.date) && (d.isBefore(toDateDisplay.value.date) || d == toDateDisplay.value.date)) {
                    filteredTrainingSumList.add(trainingSum)
                }
            }

            allTrainingSummaries.emit(filteredTrainingSumList)
        }
    }

    fun continueTraining(trainingSumInfo: TrainingSummary, currentTrainingHandler: CurrentTrainingHandler) {
        currentTrainingHandler.setCurrentTrainingInfo(currentTraining = trainingSumInfo)

        //check if the training is not allready finished (because some weird bug beheaviour
        if(!trainingSumInfo.isViewable()) {
            navigateToPage(ScreenNavName.LiveTraining)
        }
        else {
            viewModelScope.launch {
                getAllTrainingSummaries(filterByDateAfter = false)
            }
        }

    }

    fun clearCurrentlySelectedTraining() {
        currentlySelectedTraining.value = null
        //allTrainingEventsForSelectedTraining.value = null
    }

    fun launchLoadAndDisplayTrainingEvents(selectedTrainingSum: TrainingSummary) {
        viewModelScope.launch {
            loadAndDisplayTrainingEvents(selectedTrainingSum = selectedTrainingSum)
        }
    }

    suspend fun loadAndDisplayTrainingEvents(selectedTrainingSum: TrainingSummary) {
        currentlySelectedTraining.value = selectedTrainingSum
        val currentTrainingHandler: CurrentTrainingHandler = CurrentTrainingHandler(currentContext = getApplication<Application>().baseContext)


        loadAllTrainingEvents(trainingId = selectedTrainingSum.id, authKey = currentTrainingHandler.getAuthKey())
    }

    private suspend fun loadAllTrainingEvents(trainingId: Long, authKey: String) {
        try {
            val result = trainingApi.getTrainingEvents(trainingId = trainingId, authToken = authKey)
            val responseCode = result.raw().code
            val body = result.body()


            if(body != null && responseCode >= 200 && responseCode < 300) {
                //call was succesfull
                allTrainingEventsForSelectedTraining.emit(feedbackEventFilterHandler.filterTrainingevents(TrainingMapper.mapListTrainingEvents(body).reversed())) //to make the new training come ontop
            }
            else {
                //call failed
                Log.i("Error","LoadAllTrainingEvents body was null or negative response code")
                Log.i("Error-2",responseCode.toString())
            }

        }
        catch(e: Throwable) {
            Log.i("Error",e.message.toString() + " " +  e.cause)
        }
    }

    fun navigateToPage(navigateTo: ScreenNavName) {
        navController.navigate(route = navigateTo.route) {

        }
    }
}