package com.example.vref_solutions_tablet_application.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.vref_solutions_tablet_application.api.requestBodies.ActivationRequestBody
import com.example.vref_solutions_tablet_application.api.requestBodies.LoginRequestBody
import com.example.vref_solutions_tablet_application.api.responseEntities.LoginResponseEntity
import com.example.vref_solutions_tablet_application.api.UserApi
import com.example.vref_solutions_tablet_application.handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.mappers.UserMapper
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.apiretrofit.RetrofitApiHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoginScreenViewModel(application: Application) : AndroidViewModel(application)  {
    private val _inputUsername = MutableStateFlow("")
    val inputUsername: StateFlow<String> = _inputUsername

    private val _inputPassword = MutableStateFlow("")
    val inputPassword: StateFlow<String> = _inputPassword

    private val _negativeFeedbackText = MutableStateFlow("")
    val negativeFeedbackText: StateFlow<String> = _negativeFeedbackText

    private val _inputActivationCode = MutableStateFlow("")
    val inputActivationCode: StateFlow<String> = _inputActivationCode

    private val _inputFirstNewPassword = MutableStateFlow("")
    val inputFirstNewPassword: StateFlow<String> = _inputFirstNewPassword

    private val _inputSecondNewPassword = MutableStateFlow("")
    val inputSecondNewPassword: StateFlow<String> = _inputSecondNewPassword

    private val activateAccountPopUpIsOpen = MutableStateFlow(false)
    val uiActivateAccountPopUpIsOpen: StateFlow<Boolean> = activateAccountPopUpIsOpen

    private val loginAttemptIsActive = MutableStateFlow(false)
    val uiLoginAttemptIsActive: StateFlow<Boolean> = loginAttemptIsActive

    private val passwordContainsRightCharacters = MutableStateFlow(false)
    val uiPasswordContainsRightCharacters: StateFlow<Boolean> = passwordContainsRightCharacters

    private val passwordIsLongEnough = MutableStateFlow(false)
    val uiPasswordIsLongEnough: StateFlow<Boolean> = passwordIsLongEnough

    private val userApi: UserApi = RetrofitApiHandler.getUsersApi()

    fun launchLoginAttempt(navController: NavController) {
        viewModelScope.launch {
            loginAttempt(navController)
        }
    }

    suspend fun loginAttempt(navController: NavController) {
        loginAttemptIsActive.value = true
        try {
            val result: Response<LoginResponseEntity> = userApi.login(LoginRequestBody(email = _inputUsername.value, password = _inputPassword.value))
            val loginResponseEntity = result.body()
            val responseCode = result.raw().code

            if(loginResponseEntity != null) {
                if(responseCode >= 200 && responseCode < 300) {
                    //successful login attempt
                    val loggedInUserHandler: LoggedInUserHandler = LoggedInUserHandler(getApplication<Application>().baseContext)
                    UserMapper.mapLoginResponseUser(loginResponseEntity).onSuccess {
                        if(loginResponseEntity.accessToken != null) {
                            val loggedInUser =  it //make sure there are no null errors else throw an error

                            //save the authkey and user info in encrypted global variables
                            loggedInUserHandler.setGloballyAccessableAuthKey(authKey = loginResponseEntity.accessToken)
                            loggedInUserHandler.setCurrentlyLoggedInUserData(userInfo = loggedInUser)

                            setErrorMessage("") //reset the error message to nothing
                            loginAttemptIsActive.value = false //because it succeeded
                            navController.navigate(ScreenNavName.MainMenu.route) //navigate to the main menu
                        }
                        else setErrorMessage("Something went wrong serverside, try again later.")
                    }.onFailure {
                        setErrorMessage("Something went wrong serverside, try again later.")
                    }

                }
            }
            else {
                if(responseCode >= 400 && responseCode < 500) {
                    //client error
                    setErrorMessage("Incorrect username or password, try again.")
                }
                else {
                    setErrorMessage("Server is currently unable to process your login attempt, try again later.")
                }
            }

        }
        catch(e: Throwable) {
            //failed login call
            var t = e
            loginAttemptIsActive.value = false
        }
    }

    fun launchActivateAccountAttempt() {
        viewModelScope.launch {
            activateAccountAttempt()
        }
    }

    suspend fun activateAccountAttempt() {
        try {
            if(inputContainsSpecialCharacter(_inputFirstNewPassword.value) && inputContainsLowercase(_inputFirstNewPassword.value)
                && inputContainsUppercase(_inputFirstNewPassword.value) && inputContainsNumber(_inputFirstNewPassword.value) && _inputFirstNewPassword.value.length >= 8) {
                if(_inputFirstNewPassword.value == _inputSecondNewPassword.value) {
                    //the result of the call will contain a list of all users from that organisation
                    val requestBody: ActivationRequestBody = ActivationRequestBody(
                        activationCode = _inputActivationCode.value,
                        password = _inputFirstNewPassword.value
                    )

                    val result = userApi.activateUserByRequestBody(body = requestBody)
                    val responseCode = result.raw().code
                    val body = result.body()

                    if(body != null && responseCode >= 200 && responseCode < 300) {
                        //call was succesfull
                        val mappedUser = UserMapper.map(entity = body).getOrNull()
                        if(mappedUser != null) {
                            Log.i("SUCCES","ActivateAccountAttempt was succesfull")
                            Toast.makeText(getApplication<Application>().baseContext,"Successfully activated your account, you can log in now!", Toast.LENGTH_LONG).show()
                            _inputUsername.value = mappedUser.email
                            resetInputFirstAndSecondPasswordFields()
                            activateAccountPopUpIsOpen.value = false
                        }
                    }
                    else {
                        //call failed
                        Log.i("Error",responseCode.toString())
                        Log.i("Error","ActivateAccountAttempt body was null or negative response code")
                        Toast.makeText(getApplication<Application>().baseContext,"Your activation code is not valid.", Toast.LENGTH_LONG).show()
                        resetInputFirstAndSecondPasswordFields()
                    }
                }
                else {
                    Toast.makeText(getApplication<Application>().baseContext,"Your passwords don't match.", Toast.LENGTH_LONG).show()
                }
            }
            else {
                Toast.makeText(getApplication<Application>().baseContext,"Make sure your password meets the password conditions", Toast.LENGTH_LONG).show()
            }

        }
        catch(e: Throwable) {
            Log.i("Error","Error while fetching CreateNewOrganization")
            Log.i("Error-Details",e.message.toString())
            Toast.makeText(getApplication<Application>().baseContext,"A critical error occurred, try again later.", Toast.LENGTH_LONG).show()
            resetInputFirstAndSecondPasswordFields()
        }
    }

    fun resetInputFirstAndSecondPasswordFields() {
        _inputFirstNewPassword.value = ""
        _inputSecondNewPassword.value = ""
    }

    fun toggleActivationAccountPopUp() {
        activateAccountPopUpIsOpen.value = !activateAccountPopUpIsOpen.value
    }

    fun resetSavedUserDataAndAuthKey() {
        val loggedInUserHandler = LoggedInUserHandler(currentContext = getApplication<Application>().baseContext)
        loggedInUserHandler.resetSavedSessionData()
    }

    fun setInputUsername(input: String) {
        _inputUsername.value = input
    }

    fun setInputPassword(input: String) {
        _inputPassword.value = input
    }

    fun setErrorMessage(errorMessage: String) {
        _negativeFeedbackText.value = errorMessage
        loginAttemptIsActive.value = false
    }

    fun setInputActivationCode(input: String) {
        _inputActivationCode.value = input
    }

    fun setFirstNewPassword(input: String) {
        if(input.length < 50) {
            _inputFirstNewPassword.value = input

            validatePasswordInput(input)
        }
        else Toast.makeText(getApplication<Application>().baseContext,"Password is to long", Toast.LENGTH_SHORT).show()

    }

    private fun validatePasswordInput(input: String) {

        if(inputContainsSpecialCharacter(input) && inputContainsLowercase(input)
            && inputContainsUppercase(input) && inputContainsNumber(input)) passwordContainsRightCharacters.value = true
        else passwordContainsRightCharacters.value = false

        passwordIsLongEnough.value = input.length >= 8
    }

    private fun inputContainsSpecialCharacter(input: String): Boolean {
        val specialCharacters: Pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = specialCharacters.matcher(input)

        return matcher.find()
    }

    private fun inputContainsNumber(input: String): Boolean {
        return "[0-9]".toRegex().containsMatchIn(input)
    }

    private fun inputContainsLowercase(input: String): Boolean {
        return "[a-z]".toRegex().containsMatchIn(input)
    }

    private fun inputContainsUppercase(input: String): Boolean {
        return "[A-Z]".toRegex().containsMatchIn(input)
    }

    fun setSecondNewPassword(input: String) {
        _inputSecondNewPassword.value = input
    }

    fun resetPreviousInfo() {
        val loggedInUserHandler: LoggedInUserHandler = LoggedInUserHandler(getApplication<Application>().baseContext)
        loggedInUserHandler.resetSavedSessionData()
    }



}
