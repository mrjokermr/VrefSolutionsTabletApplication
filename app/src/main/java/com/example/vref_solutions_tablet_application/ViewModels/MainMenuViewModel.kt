package com.example.vref_solutions_tablet_application.ViewModels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.vref_solutions_tablet_application.API.APIBaseConfig
import com.example.vref_solutions_tablet_application.API.RequestBodies.CreateTrainingRequestBody
import com.example.vref_solutions_tablet_application.API.TrainingApi
import com.example.vref_solutions_tablet_application.API.UserApi
import com.example.vref_solutions_tablet_application.Enums.TrainingStatus
import com.example.vref_solutions_tablet_application.Handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.Handlers.FeedbackEventFilterHandler
import com.example.vref_solutions_tablet_application.Handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.Mappers.TrainingMapper
import com.example.vref_solutions_tablet_application.Mappers.UserMapper
import com.example.vref_solutions_tablet_application.Models.DateSelectAndDisplayObject
import com.example.vref_solutions_tablet_application.Models.TrainingEvent
import com.example.vref_solutions_tablet_application.Models.TrainingSummary
import com.example.vref_solutions_tablet_application.Models.User
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.`API-Retrofit`.RetrofitApiHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.days


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

    private val userApi: UserApi = RetrofitApiHandler.GetUsersApi()
    private val trainingApi: TrainingApi = RetrofitApiHandler.GetTrainingsApi()

    fun UserIsInstructorOrHigher(context: Context): Boolean {
        val userInfoHandler: LoggedInUserHandler = LoggedInUserHandler(currentContext = context)

        var correctRights = false

        correctRights = userInfoHandler.UserIsInstructor()


        return correctRights
    }

    fun GetInstructorFeedbackFilterFlow(): StateFlow<Boolean> {
        return feedbackEventFilterHandler.removeInstructorFeedbackFlow
    }

    fun ToggleManualInstructorFeedbackFilter() {
        feedbackEventFilterHandler.ToggleManualInstructorFeedbackFilter()
    }

    fun ToggleQuickInstructorFeedbackFilter() {
        feedbackEventFilterHandler.ToggleQuickInstructorFeedbackFilter()
    }

    fun TogglePrescribedEventsFeedbackFilter() {
        feedbackEventFilterHandler.TogglePrescribedEventsFeedbackFilter()
    }

    fun ToggleInstructorFeedbackEventFilter() {
        feedbackEventFilterHandler.ToggleInstructorFeedbackEventFilter()
    }

    fun ToggleAutomaticFeedbackEventFilter() {
        feedbackEventFilterHandler.ToggleAutomaticFeedbackEventFilter()
    }

    fun UpdateCurrentEventsWithFilter(trainingId: Long, authKey: String) {
        viewModelScope.launch {
            LoadAllTrainingEvents(trainingId = trainingId, authKey = authKey)
        }
    }

    suspend fun LoadStudentName(id: Long, authKey: String): String {
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
                    return mappedUser.FullName()
                }
                else return "Unknown"

            }
            else {
                //call failed
                return "Unknown"
            }
        }
        catch(e: Throwable) {
            return "Unknown - Error"
        }
    }

    suspend fun GetAllTrainingSummaries(context: Context, filterByDateAfter: Boolean) {
        val userInfoHandler: LoggedInUserHandler = LoggedInUserHandler(currentContext = context)

        try {
            val result = trainingApi.GetAllTrainingInfoSummaries(authToken = userInfoHandler.GetAuthKey())
            val responseCode = result.raw().code
            val body = result.body()
            //Log.i("TEST",body?.size.toString())

            if(body != null && responseCode >= 200 && responseCode < 300) {
                //call was successful
                EmitAndPutUnfinishedTrainingTopOfTheList(responseList = TrainingMapper.MapListTrainingSummary(body).reversed().toMutableList()) //added reversed so that the newest date trainings are ontop
                if(filterByDateAfter) {
                    DisplayTrainingByDate(context = context)
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

    private suspend fun EmitAndPutUnfinishedTrainingTopOfTheList(responseList: MutableList<TrainingSummary>) {
        var continueTrainingSumList = mutableListOf<TrainingSummary>()

        for(trainingSum in responseList) {
            if(!trainingSum.IsViewable()) {
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

    suspend fun DisplayTrainingByDate(context: Context) {
        //val allTrainingSummaries: MutableStateFlow<List<TrainingSummary>?> = MutableStateFlow(null)
        val filteredTrainingSumList: MutableList<TrainingSummary> = emptyList<TrainingSummary>().toMutableList()

        if(this.allTrainingSummaries.value != null) {

            for(trainingSum in this.allTrainingSummaries.value!!) {

                val d = trainingSum.GetLocalDate()
                if(d.isAfter(fromDateDisplay.value.date) && (d.isBefore(toDateDisplay.value.date) || d == toDateDisplay.value.date)) {
                    filteredTrainingSumList.add(trainingSum)
                }
            }

            allTrainingSummaries.emit(filteredTrainingSumList)
        }



    }

    fun ContinueTraining(trainingSumInfo: TrainingSummary, currentTrainingHandler: CurrentTrainingHandler) {
        currentTrainingHandler.SetCurrentTrainingInfo(currentTraining = trainingSumInfo)

        //check if the training is not allready finished (because some weird bug beheaviour
        if(!trainingSumInfo.IsViewable()) {
            NavigateToPage(ScreenNavName.LiveTraining)
        }
        else {
            viewModelScope.launch {
                GetAllTrainingSummaries(context = getApplication<Application>().baseContext, filterByDateAfter = false)
            }
        }

    }

    fun ClearCurrentlySelectedTraining() {
        currentlySelectedTraining.value = null
        //allTrainingEventsForSelectedTraining.value = null
    }

    suspend fun LoadAndDisplayTrainingEvents(context: Context,selectedTrainingSum: TrainingSummary) {
        currentlySelectedTraining.value = selectedTrainingSum
        val currentTrainingHandler: CurrentTrainingHandler = CurrentTrainingHandler(currentContext = context)


        LoadAllTrainingEvents(trainingId = selectedTrainingSum.id, authKey = currentTrainingHandler.GetAuthKey())
    }

    private suspend fun LoadAllTrainingEvents(trainingId: Long,authKey: String) {
        try {
            val result = trainingApi.GetTrainingEvents(trainingId = trainingId, authToken = authKey)
            val responseCode = result.raw().code
            val body = result.body()


            if(body != null && responseCode >= 200 && responseCode < 300) {
                //call was succesfull
                allTrainingEventsForSelectedTraining.emit(feedbackEventFilterHandler.FilterTrainingevents(TrainingMapper.MapListTrainingEvents(body).reversed())) //to make the new training come ontop
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

    fun NavigateToPage(navigateTo: ScreenNavName) {
        navController.navigate(route = navigateTo.route) {

        }
    }





}