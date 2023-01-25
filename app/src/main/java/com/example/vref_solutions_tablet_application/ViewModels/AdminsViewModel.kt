package com.example.vref_solutions_tablet_application.ViewModels

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.vref_solutions_tablet_application.API.APIBaseConfig
import com.example.vref_solutions_tablet_application.API.OrganizationApi
import com.example.vref_solutions_tablet_application.API.RequestBodies.OrganizationRequestBody
import com.example.vref_solutions_tablet_application.API.RequestBodies.OrganizationUserIdOnlyRequestBody
import com.example.vref_solutions_tablet_application.API.RequestBodies.UserPostRequestBody
import com.example.vref_solutions_tablet_application.API.RequestBodies.UserPutRequestBody
import com.example.vref_solutions_tablet_application.API.UserApi
import com.example.vref_solutions_tablet_application.Enums.UserType
import com.example.vref_solutions_tablet_application.Handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.Mappers.OrgnizationMapper
import com.example.vref_solutions_tablet_application.Mappers.UserMapper
import com.example.vref_solutions_tablet_application.Models.*
import com.example.vref_solutions_tablet_application.Models.PopUpModels.AreYouSureInfo
import com.example.vref_solutions_tablet_application.Models.PopUpModels.BasePopUpScreen
import com.example.vref_solutions_tablet_application.Models.PopUpModels.EditUser
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
import kotlin.coroutines.coroutineContext

class AdminsViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var navController: NavController
    lateinit var context: Context

    private val organizationApi: OrganizationApi = RetrofitApiHandler.GetOrganizationsApi()
    private val userApi: UserApi = RetrofitApiHandler.GetUsersApi()

    //dummy info to prevent null checks
    var userToEdit: MutableState<User> = mutableStateOf(
        User(
            id = -1,
            email = "placeholder@email.com",
            firstName = "placeholder",
            lastName = "placeholder",
            organization = Organization(id = -1, name = "placeholder"),
            userType = UserType.Student,
        )
    )

    private val popUpScreenIsOpen = MutableStateFlow(false)
    val uiPopUpScreenIsOpen: StateFlow<Boolean> = popUpScreenIsOpen

    private val popUpScreenType = MutableStateFlow<BasePopUpScreen>(EditUser())
    val uiPopUpScreenType: StateFlow<BasePopUpScreen> = popUpScreenType

    private val areYouSureScreenIsOpen = MutableStateFlow(false)
    val uiAreYouSureScreenIsOpen: StateFlow<Boolean> = areYouSureScreenIsOpen

    private val areYouSureInfo = MutableStateFlow<AreYouSureInfo?>(null)
    val uiAreYouSureInfo: StateFlow<AreYouSureInfo?> = areYouSureInfo

    private val _inputFirstname = MutableStateFlow("")
    val inputFirstname: StateFlow<String> = _inputFirstname

    private val _inputLastName = MutableStateFlow("")
    val inputLastName: StateFlow<String> = _inputLastName

    private val _inputEmail = MutableStateFlow("")
    val inputEmail: StateFlow<String> = _inputEmail

    private val _inputUserType = MutableStateFlow("")
    val inputUserType: StateFlow<String> = _inputUserType

    private val _inputOrganizationName = MutableStateFlow("")
    val inputOrganizationName: StateFlow<String> = _inputOrganizationName

    private val initialOrganizationName = MutableStateFlow("")
    val uiInitialOrganizationName: StateFlow<String> = initialOrganizationName

    private val allUsers: MutableStateFlow<List<User>> = MutableStateFlow(emptyList())
    val uiAllUsers: StateFlow<List<User>> = allUsers

    private val allOrganizationsSummary: MutableStateFlow<List<Organization>> = MutableStateFlow(emptyList())
    val uiAllOrganizationsSummary: StateFlow<List<Organization>> = allOrganizationsSummary

    private val selectedOrganization: MutableStateFlow<Organization?> = MutableStateFlow(null)
    val uiSelectedOrganization: StateFlow<Organization?> = selectedOrganization

    fun SetAndDisplayAreYouSureInfo(_areYouSureInfo: AreYouSureInfo) {
        areYouSureInfo.value = _areYouSureInfo
        areYouSureScreenIsOpen.value = true
    }

    suspend fun GetAllUsersForOrganisation(authKey: String, organisationId: Long) {
        try {
            //the result of the call will contain a list of all users from that organisation
            val result = organizationApi.GetAllOrganisationInfo(authToken = authKey, organizationId = organisationId)
            val responseCode = result.raw().code
            val body = result.body()
            if(body != null && responseCode >= 200 && responseCode < 300) {
                //call was succesfull
                val organizationName = if(body.name != null) body.name else "Error"

                _inputOrganizationName.value = organizationName
                initialOrganizationName.value = organizationName

                selectedOrganization.value = Organization(
                    id = organisationId,
                    name = organizationName
                )

                if(body.users != null) {
                    allUsers.emit(body.users.sortedByDescending { it.userType }) //set the mapped retrieved data to the mutablestate users list
                }
            }
            else {
                //call failed
                Log.i("Error","GetAllUsersForOrganisation body was null")
            }
        }
        catch(e: Throwable) {
            Log.i("Error","Error while fetching GetAllUsersForOrganisation")
            Log.i("Error-Details",e.message.toString())
        }
    }

    suspend fun GetAllOrganisationsInfo(authKey: String) {
        try {
            //the result of the call will contain a list of all users from that organisation
            val result = organizationApi.GetOrganisationsList(authToken = authKey)
            val responseCode = result.raw().code
            val body = result.body()
            if(body != null && responseCode >= 200 && responseCode < 300) {
                //call was succesfull
                Log.i("SUCCES",body.toString())
                allOrganizationsSummary.emit(OrgnizationMapper.MapList(body)) //set the mapped retrieved data to the mutablestate organisations summary list
            }
            else {
                //call failed
                Log.i("Error","GetAllOrganisationsInfo body was null")
            }
        }
        catch(e: Throwable) {
            Log.i("Error","Error while fetching GetAllOrganisationsInfo")
            Log.i("Error-Details",e.message.toString())
        }
    }

    fun SelectOrDeselectAndDisplayOrganisation(organization: Organization, authKey: String) {
        if(selectedOrganization.value != null) {
            if(selectedOrganization.value == organization) DeselectCurrentSelectedOrganization()
            else SelectOrganization(organization)
        }
        else SelectOrganization(organization)

        SuperAdminDisplayOrganisation(authKey = authKey, organisationId = organization.id)
    }

    fun DeselectCurrentSelectedOrganization() {
        selectedOrganization.value = null
        initialOrganizationName.value = ""
        _inputOrganizationName.value = ""
    }

    fun SelectOrganization(organization: Organization) {
        selectedOrganization.value = organization
        initialOrganizationName.value = organization.name
        _inputOrganizationName.value = organization.name
    }

    fun SuperAdminDisplayOrganisation(authKey: String, organisationId: Long) {
        if(selectedOrganization.value != null) {
            viewModelScope.launch {
                GetAllUsersForOrganisation(authKey = authKey, organisationId = organisationId)
            }
        }
        else viewModelScope.launch { allUsers.emit(emptyList()) }
    }

    fun CancelChangingOrganizationName() {
        _inputOrganizationName.value = initialOrganizationName.value
    }

    suspend fun CreateNewOrganization(context: Context,authKey: String, organizationName: String) {
        try {
            //the result of the call will contain a list of all users from that organisation
            val requestBody = OrganizationRequestBody(name = organizationName)

            val result = organizationApi.CreateNewOrganization(authToken = authKey,body = requestBody)
            val responseCode = result.raw().code
            if(responseCode >= 200 && responseCode < 300) {
                //call was succesfull
                initialOrganizationName.value = requestBody.name
                Toast.makeText(context,"Succesfully created a new organization", Toast.LENGTH_LONG).show()

                //force reloading the organizations summary list
                //to force the recompesition WITH the same button state the .emit(emptyList()) is necessary aswell as changing the selectedOrganization name
                allOrganizationsSummary.emit(emptyList())
                GetAllOrganisationsInfo(authKey = authKey)

                ToggleDynamicPopUpScreen()
            }
            else {
                Toast.makeText(context,"Something went wrong when creating the the organisation. Organisation name may already be occupied or has a length longer than 50 characters.", Toast.LENGTH_LONG).show()
                //call failed
                Log.i("Error",responseCode.toString())
                Log.i("Error","CreateNewOrganization body was null")
            }
        }
        catch(e: Throwable) {
            Log.i("Error","Error while fetching CreateNewOrganization")
            Log.i("Error-Details",e.message.toString())
        }
    }

    suspend fun CreateNewUser(context: Context, authKey: String) {
        try {
            if(selectedOrganization.value != null) {
                //the result of the call will contain a list of all users from that organisation
                val requestBody = UserPostRequestBody(
                    email= _inputEmail.value,
                    firstName= _inputFirstname.value,
                    lastName= _inputLastName.value,
                    organization = OrganizationUserIdOnlyRequestBody(id = selectedOrganization.value!!.id)
                )

                val result = userApi.postUserByRequestBody(authToken = authKey,body = requestBody,)
                val responseCode = result.raw().code
                val body = result.body()

                if(body != null && responseCode >= 200 && responseCode < 300) {
                    //call was succesfull
                    Log.i("SUCCES","CreateNewUser was succesfull")
                    Toast.makeText(context,"Succesfully created new user: ${_inputFirstname.value} ${_inputLastName.value}", Toast.LENGTH_LONG).show()

                    val mappedUser = UserMapper.Map(entity = body).getOrNull()
                    if(mappedUser != null) {
                        allUsers.emit(allUsers.value.plus(mappedUser))
                    }

                    ToggleDynamicPopUpScreen()
                }
                else {
                    //409 conflict means the users email is already occupied
                    if(responseCode == 409) {
                        Toast.makeText(context,"The users email is already in use.", Toast.LENGTH_LONG).show()
                    }
                    else Toast.makeText(context,"Something went wrong when creating the user, make sure the email is max 100 characters." +
                            " Also the firstname and lastname can't be longer than 50 characters.", Toast.LENGTH_LONG).show()
                    //call failed
                    Log.i("Error","CreateNewUser body was null or negative response code")
                }
            }

        }
        catch(e: Throwable) {
            Log.i("Error","Error while fetching CreateNewOrganization")
            Log.i("Error-Details",e.message.toString())
        }
    }

    suspend fun UpdateUser(context: Context,authKey: String) {
        try {
            if(userToEdit.value.id >= 0) {
                //the result of the call will contain a list of all users from that organisation
                val requestBody = UserPutRequestBody(
                    email= _inputEmail.value,
                    firstName= _inputFirstname.value,
                    lastName= _inputLastName.value,
                    userType= UserType.valueOf(_inputUserType.value),
                    organization = OrganizationUserIdOnlyRequestBody(id = selectedOrganization.value!!.id)
                )

                val result = userApi.putUserByRequestBody(authToken = authKey,body = requestBody,userId = userToEdit.value.id)
                val responseCode = result.raw().code
                val body = result.body()

                if(body != null && responseCode >= 200 && responseCode < 300) {
                    //call was succesfull
                    Log.i("SUCCES","UpdateUser was succesfull")
                    Toast.makeText(context,"Succesfully updated user: ${_inputFirstname.value} ${_inputLastName.value}", Toast.LENGTH_LONG).show()

                    allUsers.emit(emptyList()) //force reload of the users list with updated value(s)
                    if(selectedOrganization.value != null) GetAllUsersForOrganisation(authKey = authKey, organisationId = selectedOrganization.value!!.id)

                    popUpScreenIsOpen.value = false
                }
                else {
                    Toast.makeText(context,"Something went wrong when creating the user, make sure the email is max 100 characters." +
                            " Also the firstname and lastname can't be longer than 50 characters.", Toast.LENGTH_LONG).show()
                    //call failed
                    Log.i("Error",responseCode.toString())
                    Log.i("Error","UpdateUser body was null or negative response code")
                }
            }

        }
        catch(e: Throwable) {
            Log.i("Error","Error while fetching CreateNewOrganization")
            Log.i("Error-Details",e.message.toString())
        }
    }

    suspend fun DeleteUser(context: Context, authKey: String) {
        try {
            //placeholder user is -1 ID so we can check by that value if the user is set
            if(userToEdit.value.id >= 0) {
                val result = userApi.deleteUserById(userId = userToEdit.value.id,authToken = authKey)
                val responseCode = result.raw().code

                if(responseCode >= 200 && responseCode < 300) {
                    //call was successful
                    val loggedInUserHandler = LoggedInUserHandler(context)

                    //user has removed him/her-self so logout
                    if(userToEdit.value.id.toString() == loggedInUserHandler.GetIdCurrentUser()) {
                        loggedInUserHandler.ResetSavedSessionData()
                        NavigateToPage(ScreenNavName.Login)
                    }
                    else {
                        Toast.makeText(context, "Succesfully deleted the user", Toast.LENGTH_LONG).show()
                        allUsers.emit(emptyList()) //make the user list reload
                        if(selectedOrganization.value != null) GetAllUsersForOrganisation(authKey = authKey, organisationId = selectedOrganization.value!!.id)
                    }

                }
                else {
                    //call failed
                    Toast.makeText(context, "Something went wrong while deleting the user. Note that you can't delete the last admin of the organisation.", Toast.LENGTH_LONG).show()
                    Log.i("Error","DeleteUser failed response code")
                }
            }

        }
        catch(e: Throwable) {
            Log.i("Error","Error DeleteUser")
            Log.i("Error-Details",e.message.toString())
        }
    }

    suspend fun ConfirmChangingOrganizationName(context: Context, authKey: String, organizationId: Long) {
        try {
            //the result of the call will contain a list of all users from that organisation
            val requestBody = OrganizationRequestBody(name = _inputOrganizationName.value)

            val result = organizationApi.UpdateOrganizationName(authToken = authKey,body = requestBody, organizationId = organizationId)
            val responseCode = result.raw().code
            if(responseCode >= 200 && responseCode < 300) {
                //call was succesfull
                Log.i("SUCCES","ConfirmChangingOrganizationName was succesfull")
                initialOrganizationName.value = requestBody.name
                Toast.makeText(context,"Succesfully changed the organisation name", Toast.LENGTH_LONG).show()

                //force reloading the organizations summary list
                //to force the recompesition WITH the same button state the .emit(emptyList()) is necessary aswell as changing the selectedOrganization name
                if(selectedOrganization.value != null) {selectedOrganization.value!!.name = requestBody.name}
                allOrganizationsSummary.emit(emptyList())
                GetAllOrganisationsInfo(authKey = authKey)

            }
            else {
                Toast.makeText(context,"Organisation name is already occupied", Toast.LENGTH_LONG).show()
                //call failed
                Log.i("Error","ConfirmChangingOrganizationName body was null")
            }
        }
        catch(e: Throwable) {
            Log.i("Error","Error while fetching ConfirmChangingOrganizationName")
            Log.i("Error-Details",e.message.toString())
        }
    }

//    fun SetEditInitialDisplay(user: User) {
//        _inputFirstname.value = "abc"
//        _inputLastName.value = "ddd"
//    }

//    fun SetEditUserAndOpenPopUpScreen(user: User) {
//        this.userToEdit.value = user
//        ToggleDynamicPopUpScreen()
//    }

    fun ToggleAreYouSureScreen() {
        areYouSureScreenIsOpen.value = !areYouSureScreenIsOpen.value
    }

    fun SetPopUpScreenType(type: BasePopUpScreen) {
        popUpScreenType.value = type
    }

    fun ToggleDynamicPopUpScreen() {
        if(userToEdit.value.id >= 0) {
            _inputFirstname.value = userToEdit.value.firstName
            _inputLastName.value = userToEdit.value.lastName
            _inputEmail.value = userToEdit.value.email
            _inputUserType.value = userToEdit.value.userType.toString()
        }

        popUpScreenIsOpen.value = !popUpScreenIsOpen.value

        areYouSureScreenIsOpen.value = false
    }

    fun ResetUserToEdit() {
        //code is based of the id, if id is less then 0, value is equal to null
        userToEdit.value = User(
            id = -1,
            email = "placeholder@email.com",
            firstName = "placeholder",
            lastName = "placeholder",
            organization = Organization(id = -1, name = "placeholder"),
            userType = UserType.Student,
        )

        _inputUserType.value = "Student"
        _inputEmail.value = ""
        _inputFirstname.value = ""
        _inputLastName.value = ""

    }

    fun SetFirstNameInput(value: String) {
        _inputFirstname.value = value
    }

    fun SetLastNameInput(value: String) {
        _inputLastName.value = value
    }

    fun SetEmailInput(value: String) {
        _inputEmail.value = value
    }

    fun SetUserTypeInput(value: String) {
        _inputUserType.value = value
    }

    fun SetOrganizationNameInput(value: String, context: Context) {
        if(value.length < 50) _inputOrganizationName.value = value
        else {
            Toast.makeText(context, "Max character length of the organisation name has been reached.", Toast.LENGTH_LONG).show()
        }


    }

    fun NavigateToPage(navigateTo: ScreenNavName) {
        navController.navigate(route = navigateTo.route) {
            popUpTo(navigateTo.route) {
                inclusive = true
            }
        }
    }
}