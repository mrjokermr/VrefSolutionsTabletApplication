package com.example.vref_solutions_tablet_application.ViewModels

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavController
import com.example.vref_solutions_tablet_application.API.RequestBodies.ActivationRequestBody
import com.example.vref_solutions_tablet_application.API.RequestBodies.LoginRequestBody
import com.example.vref_solutions_tablet_application.API.ResponseEntities.LoginResponseEntity
import com.example.vref_solutions_tablet_application.API.UserApi
import com.example.vref_solutions_tablet_application.Handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.Mappers.UserMapper
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.`API-Retrofit`.RetrofitApiHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val userApi: UserApi = RetrofitApiHandler.GetUsersApi()

    suspend fun LoginAttempt(navController: NavController) {
        loginAttemptIsActive.value = true
        try {
            val result: Response<LoginResponseEntity> = userApi.login(LoginRequestBody(email = _inputUsername.value, password = _inputPassword.value))
            val loginResponseEntity = result.body()
            val responseCode = result.raw().code

            if(loginResponseEntity != null) {
                if(responseCode >= 200 && responseCode < 300) {
                    //successful login attempt
                    val loggedInUserHandler: LoggedInUserHandler = LoggedInUserHandler(getApplication<Application>().baseContext)
                    UserMapper.MapLoginResponseUser(loginResponseEntity).onSuccess {
                        if(loginResponseEntity.accessToken != null) {
                            val loggedInUser =  it //make sure there are no null errors else throw an error

                            //save the authkey and user info in encrypted global variables
                            loggedInUserHandler.SetGloballyAccessableAuthKey(authKey = loginResponseEntity.accessToken)
                            loggedInUserHandler.SetCurrentlyLoggedInUserData(userInfo = loggedInUser)

                            SetErrorMessage("") //reset the error message to nothing
                            loginAttemptIsActive.value = false //because it succeeded
                            navController.navigate(ScreenNavName.MainMenu.route) //navigate to the main menu
                        }
                        else SetErrorMessage("Something went wrong serverside, try again later.")
                    }.onFailure {
                        SetErrorMessage("Something went wrong serverside, try again later.")
                    }

                }
            }
            else {
                if(responseCode >= 400 && responseCode < 500) {
                    //client error
                    SetErrorMessage("Incorrect username or password, try again.")
                }
                else {
                    SetErrorMessage("Server is currently unable to process your login attempt, try again later.")
                }
            }

        }
        catch(e: Throwable) {
            //failed login call
            var t = e
            loginAttemptIsActive.value = false
        }
    }

    suspend fun ActivateAccountAttempt(context: Context) {
        try {
            if(InputContainsSpecialCharacter(_inputFirstNewPassword.value) && InputContainsLowercase(_inputFirstNewPassword.value)
                && InputContainsUppercase(_inputFirstNewPassword.value) && InputContainsNumber(_inputFirstNewPassword.value) && _inputFirstNewPassword.value.length >= 8) {
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
                        val mappedUser = UserMapper.Map(entity = body).getOrNull()
                        if(mappedUser != null) {
                            Log.i("SUCCES","ActivateAccountAttempt was succesfull")
                            Toast.makeText(context,"Successfully activated your account, you can log in now!", Toast.LENGTH_LONG).show()
                            _inputUsername.value = mappedUser.email
                            ResetInputFirstAndSecondPasswordFields()
                            activateAccountPopUpIsOpen.value = false
                        }
                    }
                    else {
                        //call failed
                        Log.i("Error",responseCode.toString())
                        Log.i("Error","ActivateAccountAttempt body was null or negative response code")
                        Toast.makeText(context,"Your activation code is not valid.", Toast.LENGTH_LONG).show()
                        ResetInputFirstAndSecondPasswordFields()
                    }
                }
                else {
                    Toast.makeText(context,"Your passwords don't match.", Toast.LENGTH_LONG).show()
                }
            }
            else {
                Toast.makeText(context,"Make sure your password meets the password conditions", Toast.LENGTH_LONG).show()
            }

        }
        catch(e: Throwable) {
            Log.i("Error","Error while fetching CreateNewOrganization")
            Log.i("Error-Details",e.message.toString())
            Toast.makeText(context,"A critical error occurred, try again later.", Toast.LENGTH_LONG).show()
            ResetInputFirstAndSecondPasswordFields()
        }
    }

    fun ResetInputFirstAndSecondPasswordFields() {
        _inputFirstNewPassword.value = ""
        _inputSecondNewPassword.value = ""
    }

    fun ToggleActivationAccountPopUp() {
        activateAccountPopUpIsOpen.value = !activateAccountPopUpIsOpen.value
    }

    fun ResetSavedUserDataAndAuthKey(context: Context) {
        val loggedInUserHandler = LoggedInUserHandler(currentContext = context)
        loggedInUserHandler.ResetSavedSessionData()
    }

    fun SetInputUsername(input: String) {
        _inputUsername.value = input
    }

    fun SetInputPassword(input: String) {
        _inputPassword.value = input
    }

    fun SetErrorMessage(errorMessage: String) {
        _negativeFeedbackText.value = errorMessage
        loginAttemptIsActive.value = false
    }

    fun SetInputActivationCode(input: String) {
        _inputActivationCode.value = input
    }

    fun SetFirstNewPassword(input: String) {
        if(input.length < 50) {
            _inputFirstNewPassword.value = input

            ValidatePasswordInput(input)
        }
        else Toast.makeText(getApplication<Application>().baseContext,"Password is to long", Toast.LENGTH_SHORT).show()

    }

    private fun ValidatePasswordInput(input: String) {

        if(InputContainsSpecialCharacter(input) && InputContainsLowercase(input)
            && InputContainsUppercase(input) && InputContainsNumber(input)) passwordContainsRightCharacters.value = true
        else passwordContainsRightCharacters.value = false

        passwordIsLongEnough.value = input.length >= 8
    }

    private fun InputContainsSpecialCharacter(input: String): Boolean {
        val specialCharacters: Pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = specialCharacters.matcher(input)

        return matcher.find()
    }

    private fun InputContainsNumber(input: String): Boolean {
        return "[0-9]".toRegex().containsMatchIn(input)
    }

    private fun InputContainsLowercase(input: String): Boolean {
        return "[a-z]".toRegex().containsMatchIn(input)
    }

    private fun InputContainsUppercase(input: String): Boolean {
        return "[A-Z]".toRegex().containsMatchIn(input)
    }

    fun SetSecondNewPassword(input: String) {
        _inputSecondNewPassword.value = input
    }

    fun ResetPreviousInfo(context: Context) {
        val loggedInUserHandler: LoggedInUserHandler = LoggedInUserHandler(context)
        loggedInUserHandler.ResetSavedSessionData()
    }



}
