package com.example.vref_solutions_tablet_application.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.vref_solutions_tablet_application.api.requestBodies.CamerasRequestBody
import com.example.vref_solutions_tablet_application.api.requestBodies.CreateTrainingRequestBody
import com.example.vref_solutions_tablet_application.api.TrainingApi
import com.example.vref_solutions_tablet_application.api.UserApi
import com.example.vref_solutions_tablet_application.handlers.CurrentTrainingHandler
import com.example.vref_solutions_tablet_application.handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.mappers.TrainingMapper
import com.example.vref_solutions_tablet_application.models.User
import com.example.vref_solutions_tablet_application.mappers.UserMapper
import com.example.vref_solutions_tablet_application.models.SearchQueryObject
import com.example.vref_solutions_tablet_application.models.Training
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.apiretrofit.RetrofitApiHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewTrainingViewModel(application: Application) : AndroidViewModel(application) {

    private val _inputSearchStudent = MutableStateFlow("")
    val inputSearchStudent: StateFlow<String> = _inputSearchStudent

    lateinit var navController: NavController

    private val userApi: UserApi = RetrofitApiHandler.getUsersApi()
    private val trainingApi: TrainingApi = RetrofitApiHandler.getTrainingsApi()

    private val popUpScreenIsOpen = MutableStateFlow(true)
    val uiPopUpScreenIsOpen: StateFlow<Boolean> = popUpScreenIsOpen

    private val selectedStudentFirst: MutableStateFlow<User?> = MutableStateFlow(null)
    val uiSelectedStudentFirst: StateFlow<User?> = selectedStudentFirst

    private val selectedStudentSecond: MutableStateFlow<User?> = MutableStateFlow(null)
    val uiSelectedStudentSecond: StateFlow<User?> = selectedStudentSecond

    private var selectedStudentCacheFirst: User? = null
    private var selectedSTudentCacheSecond: User? = null

    private var startTrainingAttempts = 0

    private val allStudents: MutableStateFlow<List<User>?> = MutableStateFlow(null)
    val uiAllStudents: StateFlow<List<User>?> = allStudents

    private val currentlySavedInput: MutableList<String> = emptyList<String>().toMutableList()
    //display list with filtered results
    private val searchQuerySuggestionsList: MutableStateFlow<MutableList<SearchQueryObject>> = MutableStateFlow(emptyList<SearchQueryObject>().toMutableList())
    val uiSearchQuerySuggestionsList: StateFlow<MutableList<SearchQueryObject>> = searchQuerySuggestionsList
    //actual list containing all suggestion items
    private var nonFilteredsearchQuerySuggestionsList: MutableStateFlow<MutableList<SearchQueryObject>> = MutableStateFlow(emptyList<SearchQueryObject>().toMutableList())

    fun navigateToPage(navigateTo: ScreenNavName) {
        navController.navigate(route = navigateTo.route) {
            popUpTo(navigateTo.route) {
                inclusive = true
            }
        }
    }

    fun launchLoadSearchQuerySuggestions(loggedInUserHandler: LoggedInUserHandler) {
        viewModelScope.launch {
            loadSearchQuerySuggestions(loggedInUserHandler = loggedInUserHandler)
        }
    }

    suspend fun loadSearchQuerySuggestions(loggedInUserHandler: LoggedInUserHandler) {
        //had to get the list twice, otherwise some .map and .filter would happen with both lists
        searchQuerySuggestionsList.emit(rankAndFilterQuerySuggestionsList(loggedInUserHandler.getSavedStudentSearchQueryInputList()))
        nonFilteredsearchQuerySuggestionsList.emit(loggedInUserHandler.getSavedStudentSearchQueryInputList())
        //Log.i("LoadedSuggestions",searchQuerySuggestionsList.value.toString())
    }

    private fun rankAndFilterQuerySuggestionsList(initialList: MutableList<SearchQueryObject>): MutableList<SearchQueryObject> {
        initialList.sortByDescending { it.amountOfUsage }

        var filteredResultList: MutableList<SearchQueryObject> = initialList

        //only get the top five search query objects
        if(filteredResultList.size > 5) {
            do {
                filteredResultList.removeLast()
            }
            while(filteredResultList.size > 5)
        }

        return filteredResultList
    }

    fun cacheAndSaveUserInput(input: String, loggedInUserHandler: LoggedInUserHandler) {
        val currentQuerySuggestionsInfo: MutableList<SearchQueryObject> = nonFilteredsearchQuerySuggestionsList.value

        var queryObjectIsInList = false
        if(currentlySavedInput.contains(input) == false && input.length >= 3) {
            currentlySavedInput.add(input)
            //input is not yet cached and saved, this currentlySavedInput prevents when input stays the same it will be increased in usage

            loop@ for(queryObject in currentQuerySuggestionsInfo) {
                if(queryObject.query == input) {
                    queryObject.amountOfUsage += 1
                    queryObjectIsInList = true
                    break@loop
                }
            }

            if(!queryObjectIsInList) {
                currentQuerySuggestionsInfo.add(SearchQueryObject(
                    query = input,
                    amountOfUsage = 1
                ))
            }

            loggedInUserHandler.setSavedStudentSearchQueryInputListAsString(updatedQueryList = currentQuerySuggestionsInfo, userId = loggedInUserHandler.getIdCurrentUser())
        }
    }

    fun launchStartNewTraining() {
        viewModelScope.launch {
            startNewTraining()
        }
    }

    suspend fun startNewTraining() {
        //code
        //check if the students have been set
        if(selectedStudentFirst.value != null && selectedStudentSecond.value != null) {
            //students have been set so continue starting the training
            initNewTraining(loggedInUserHandler = LoggedInUserHandler(currentContext = getApplication<Application>().baseContext), currentTrainingHandler = CurrentTrainingHandler(currentContext = getApplication<Application>().baseContext))

        }
        else {
            //display a toast message saying you need to have 2 students selected
            Toast.makeText(getApplication<Application>().baseContext, "You have to select at least two students", Toast.LENGTH_LONG).show()
        }

    }

    suspend fun initNewTraining(loggedInUserHandler: LoggedInUserHandler, currentTrainingHandler: CurrentTrainingHandler) {
        try {
            var requestBody: CreateTrainingRequestBody = CreateTrainingRequestBody(
                students = getSelectedUsersIds(),
                instructorId = loggedInUserHandler.getIdCurrentUser().toLong()
            )

            val authKey = loggedInUserHandler.getAuthKey()


            val result = trainingApi.createTraining(body = requestBody, authToken = authKey)
            val responseCode = result.raw().code
            val body = result.body()

            if(body != null && responseCode >= 200 && responseCode < 300) {
                //call was succesfull
                //set the current training info
                var mappedTrainingResult = TrainingMapper.map(entity = body)
                if(mappedTrainingResult.isSuccess) {
                    val mappedTraining = mappedTrainingResult.getOrNull()
                    if(mappedTraining != null) {
                        currentTrainingHandler.setCurrentTrainingInfo(currentTraining = mappedTraining)

                        //set the training status to "recording" which equals that the training is started
                        startTraining(previousResponseBody = mappedTraining, authToken = authKey)
                    }
                    else {
                        Log.i("Error","mappedTrainingResult is null")
                    }
                }
                else {
                    Log.i("Error","mappedTrainingResult has error " + body.toString())
                }

            }
            else {
                //call failed
                Log.i("Error","InitNewTraining body was null or negative response code")
            }
        }
        catch(e: Throwable) {
            Log.i("Error",e.message.toString() + " " +  e.cause)
        }
    }

    suspend fun startTraining(previousResponseBody: Training, authToken: String) {
        //no try and catch necessary since this function is called from within initnewtraining
        //this result will not contain a body, the response code should be 204 No Content

        val result = trainingApi.startTrainingById(trainingId = previousResponseBody.id, authToken = authToken, body = CamerasRequestBody())
        val responseCode = result.raw().code
        Log.i("Starttraining","id " + previousResponseBody.id.toString())
        if(responseCode >= 200 && responseCode < 300) {
            navigateToPage(ScreenNavName.LiveTraining)
        }
        else {
            Log.i("Starttraining","FAILED")
            Log.i("Starttraining-Code", responseCode.toString())
            Log.i("Starttraining",result.raw().message)

            startTrainingAttempts++

            //try to start the training three times else message the user that it is not possible
            if(startTrainingAttempts <= 3) startTraining(previousResponseBody = previousResponseBody, authToken = authToken)
            else {
                if((responseCode >= 500 && responseCode < 600) || responseCode == 403) {
                    //serverside error so log out because service is unavailable
                    //API does not support refresher token...
                    val userInfoHandler: LoggedInUserHandler = LoggedInUserHandler(currentContext = getApplication<Application>().baseContext)
                    userInfoHandler.logOut()
                    navigateToPage(ScreenNavName.Login)
                }
            }

        }
    }

    private fun getSelectedUsersIds(): List<Long> {
        return listOf(selectedStudentFirst.value!!.id, selectedStudentSecond.value!!.id)
    }

    fun setSelectedStudent(student: User, loggedInUserHandler: LoggedInUserHandler) {
        if(selectedStudentFirst.value == null && selectedStudentSecond.value != student) selectedStudentFirst.value = student

        //second check is preventing that a user can be set as a student twice
        if(selectedStudentSecond.value == null && selectedStudentFirst.value != student) selectedStudentSecond.value = student


        //try to cache & save current user input for suggestion fields:
        if(_inputSearchStudent.value.isEmpty() == false && _inputSearchStudent.value.length >= 3)
            cacheAndSaveUserInput(_inputSearchStudent.value.lowercase(), loggedInUserHandler = loggedInUserHandler)

    }

    fun studentIsSelected(student: User): Boolean
    {
        var returnValue: Boolean = false
        if(selectedStudentFirst.value != null) {
            returnValue = student.id == selectedStudentFirst.value!!.id
        }

        if(selectedStudentSecond.value != null && returnValue.equals(false)) {
            returnValue = student.id == selectedStudentSecond.value!!.id
        }

        return returnValue
    }

    fun removeSelectedStudent(student: User) {
        if(selectedStudentFirst.value == student) selectedStudentFirst.value = null

        if(selectedStudentSecond.value == student) selectedStudentSecond.value = null
    }

//    private suspend fun MakeStudentListRefresh() {
//        var rightList = allStudents.value
//        allStudents.emit(rightList.toList())
//    }


    suspend fun getAllUsersBySearchFieldQuery(input: String, authToken: String) {
        try {
            val result = userApi.getAllUsersBySearchfieldMatch(authToken = authToken, searchField = input)
            val responseCode = result.raw().code
            val body = result.body()
            Log.i("response code", responseCode.toString())
            if(body != null && responseCode >= 200 && responseCode < 300) {
                //call was succesfull
                allStudents.emit(UserMapper.mapList(body))
            }
            else {
                //call failed
                Log.i("Error","GetAllUsersBySearchFieldQuery body was null or negative response code")
            }
        }
        catch(e: Throwable) {
            Log.i("Error",e.message.toString() + " " +  e.cause)
        }
    }

    fun launchGetAllStudents(authKey: String) {
        viewModelScope.launch {
            getAllStudents(authKey)
        }
    }

    suspend fun getAllStudents(authKey: String) {
        try {
            val result = userApi.getAllUsers(authToken = authKey)
            val responseCode = result.raw().code
            val body = result.body()
            if(body != null && responseCode >= 200 && responseCode < 300) {
                //call was succesfull
                allStudents.emit(UserMapper.mapList(body)) //set the mapped retrieved data to the mutablestate students list
            }
            else {
                //call failed
                Log.i("Error","Getallstudents body was null")
            }
        }
        catch(e: Throwable) {
            Log.i("Error","Error while fetching getallstudents")
        }
    }

    fun togglePopUpScreen() {
        popUpScreenIsOpen.value = !popUpScreenIsOpen.value
    }

    fun confirmAddingStudents() {
        selectedStudentCacheFirst = selectedStudentFirst.value
        selectedSTudentCacheSecond = selectedStudentSecond.value


        togglePopUpScreen()
    }

    fun cancelAddingStudents() {
        //action was cancelled
        selectedStudentFirst.value = selectedStudentCacheFirst
        selectedStudentSecond.value = selectedSTudentCacheSecond

        togglePopUpScreen()
    }

    fun setInputAndUpdateStudentsList(input: String, authToken: String) {
        _inputSearchStudent.value = input

        viewModelScope.launch {
            getAllUsersBySearchFieldQuery(input = input, authToken = authToken)
        }
    }
}