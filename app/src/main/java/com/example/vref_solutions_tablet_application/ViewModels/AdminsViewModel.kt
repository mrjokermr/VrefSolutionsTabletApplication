package com.example.vref_solutions_tablet_application.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.vref_solutions_tablet_application.api.OrganizationApi
import com.example.vref_solutions_tablet_application.api.requestBodies.OrganizationRequestBody
import com.example.vref_solutions_tablet_application.api.requestBodies.OrganizationUserIdOnlyRequestBody
import com.example.vref_solutions_tablet_application.api.requestBodies.UserPostRequestBody
import com.example.vref_solutions_tablet_application.api.requestBodies.UserPutRequestBody
import com.example.vref_solutions_tablet_application.api.UserApi
import com.example.vref_solutions_tablet_application.enums.UserType
import com.example.vref_solutions_tablet_application.handlers.LoggedInUserHandler
import com.example.vref_solutions_tablet_application.mappers.OrgnizationMapper
import com.example.vref_solutions_tablet_application.mappers.UserMapper
import com.example.vref_solutions_tablet_application.models.*
import com.example.vref_solutions_tablet_application.models.popUpModels.AreYouSureInfo
import com.example.vref_solutions_tablet_application.models.popUpModels.BasePopUpScreen
import com.example.vref_solutions_tablet_application.models.popUpModels.EditUser
import com.example.vref_solutions_tablet_application.ScreenNavName
import com.example.vref_solutions_tablet_application.apiretrofit.RetrofitApiHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminsViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var navController: NavController

    private val organizationApi: OrganizationApi = RetrofitApiHandler.getOrganizationsApi()
    private val userApi: UserApi = RetrofitApiHandler.getUsersApi()

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

    fun setAndDisplayAreYouSureInfo(_areYouSureInfo: AreYouSureInfo) {
        areYouSureInfo.value = _areYouSureInfo
        areYouSureScreenIsOpen.value = true
    }

    fun launchInitialAdminScreenSetup(currentUserIsSuperAdmin: Boolean, loggedInUserHandler: LoggedInUserHandler) {
        viewModelScope.launch {
            if(!currentUserIsSuperAdmin) {
                //because the user is not a super admin, the users list will be prefilled and can't be selected
                try {
                    getAllUsersForOrganisation(authKey = loggedInUserHandler.getAuthKey(), organisationId = loggedInUserHandler.getCompanyIdCurrentUser().toLong())
                }
                catch(e: Throwable) {
                    Toast.makeText(getApplication<Application>().baseContext,"Couldn't load the users for your organisation, try again later.", Toast.LENGTH_LONG).show()
                }
            }
            else {
                getAllOrganisationsInfo(authKey = loggedInUserHandler.getAuthKey())
            }
        }
    }

    suspend fun getAllUsersForOrganisation(authKey: String, organisationId: Long) {
        try {
            //the result of the call will contain a list of all users from that organisation
            val result = organizationApi.getAllOrganisationInfo(authToken = authKey, organizationId = organisationId)
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

    suspend fun getAllOrganisationsInfo(authKey: String) {
        try {
            //the result of the call will contain a list of all users from that organisation
            val result = organizationApi.getOrganisationsList(authToken = authKey)
            val responseCode = result.raw().code
            val body = result.body()
            if(body != null && responseCode >= 200 && responseCode < 300) {
                //call was succesfull
                Log.i("SUCCES",body.toString())
                allOrganizationsSummary.emit(OrgnizationMapper.mapList(body)) //set the mapped retrieved data to the mutablestate organisations summary list
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

    fun selectOrDeselectAndDisplayOrganisation(organization: Organization, authKey: String) {
        if(selectedOrganization.value != null) {
            if(selectedOrganization.value == organization) deselectCurrentSelectedOrganization()
            else selectOrganization(organization)
        }
        else selectOrganization(organization)

        superAdminDisplayOrganisation(authKey = authKey, organisationId = organization.id)
    }

    fun deselectCurrentSelectedOrganization() {
        selectedOrganization.value = null
        initialOrganizationName.value = ""
        _inputOrganizationName.value = ""
    }

    fun selectOrganization(organization: Organization) {
        selectedOrganization.value = organization
        initialOrganizationName.value = organization.name
        _inputOrganizationName.value = organization.name
    }

    fun superAdminDisplayOrganisation(authKey: String, organisationId: Long) {
        if(selectedOrganization.value != null) {
            viewModelScope.launch {
                getAllUsersForOrganisation(authKey = authKey, organisationId = organisationId)
            }
        }
        else viewModelScope.launch { allUsers.emit(emptyList()) }
    }

    fun cancelChangingOrganizationName() {
        _inputOrganizationName.value = initialOrganizationName.value
    }

    fun launchCreateNewOrganization(authKey: String, organizationName: String) {
        viewModelScope.launch{
            createNewOrganization(
                authKey = authKey,
                organizationName = organizationName
            )
        }
    }

    suspend fun createNewOrganization(authKey: String, organizationName: String) {
        try {
            //the result of the call will contain a list of all users from that organisation
            val requestBody = OrganizationRequestBody(name = organizationName)

            val result = organizationApi.createNewOrganization(authToken = authKey,body = requestBody)
            val responseCode = result.raw().code
            if(responseCode >= 200 && responseCode < 300) {
                //call was succesfull
                initialOrganizationName.value = requestBody.name
                Toast.makeText(getApplication<Application>().baseContext,"Succesfully created a new organization", Toast.LENGTH_LONG).show()

                //force reloading the organizations summary list
                //to force the recompesition WITH the same button state the .emit(emptyList()) is necessary aswell as changing the selectedOrganization name
                allOrganizationsSummary.emit(emptyList())
                getAllOrganisationsInfo(authKey = authKey)

                toggleDynamicPopUpScreen()
            }
            else {
                Toast.makeText(getApplication<Application>().baseContext,"Something went wrong when creating the the organisation. Organisation name may already be occupied or has a length longer than 50 characters.", Toast.LENGTH_LONG).show()
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

    fun launchCreateNewUser(authKey: String) {
        viewModelScope.launch {
            createNewUser(authKey = authKey)
        }
    }

    suspend fun createNewUser(authKey: String) {
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
                    Toast.makeText(getApplication<Application>().baseContext,"Succesfully created new user: ${_inputFirstname.value} ${_inputLastName.value}", Toast.LENGTH_LONG).show()

                    val mappedUser = UserMapper.map(entity = body).getOrNull()
                    if(mappedUser != null) {
                        allUsers.emit(allUsers.value.plus(mappedUser))
                    }

                    toggleDynamicPopUpScreen()
                }
                else {
                    //409 conflict means the users email is already occupied
                    if(responseCode == 409) {
                        Toast.makeText(getApplication<Application>().baseContext,"The users email is already in use.", Toast.LENGTH_LONG).show()
                    }
                    else Toast.makeText(getApplication<Application>().baseContext,"Something went wrong when creating the user, make sure the email is max 100 characters." +
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

    fun launchUpdateUser(authKey: String) {
        viewModelScope.launch {
            updateUser(authKey = authKey)
        }
    }

    suspend fun updateUser(authKey: String) {
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
                    Toast.makeText(getApplication<Application>().baseContext,"Succesfully updated user: ${_inputFirstname.value} ${_inputLastName.value}", Toast.LENGTH_LONG).show()

                    allUsers.emit(emptyList()) //force reload of the users list with updated value(s)
                    if(selectedOrganization.value != null) getAllUsersForOrganisation(authKey = authKey, organisationId = selectedOrganization.value!!.id)

                    popUpScreenIsOpen.value = false
                }
                else {
                    Toast.makeText(getApplication<Application>().baseContext,"Something went wrong when creating the user, make sure the email is max 100 characters." +
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

    fun launchDeleteUser(authKey: String) {
        viewModelScope.launch {
            deleteUser(authKey = authKey)

            toggleAreYouSureScreen()
            toggleDynamicPopUpScreen()
        }
    }

    suspend fun deleteUser(authKey: String) {
        try {
            //placeholder user is -1 ID so we can check by that value if the user is set
            if(userToEdit.value.id >= 0) {
                val result = userApi.deleteUserById(userId = userToEdit.value.id,authToken = authKey)
                val responseCode = result.raw().code

                if(responseCode >= 200 && responseCode < 300) {
                    //call was successful
                    val loggedInUserHandler = LoggedInUserHandler(getApplication<Application>().baseContext)

                    //user has removed him/her-self so logout
                    if(userToEdit.value.id.toString() == loggedInUserHandler.getIdCurrentUser()) {
                        loggedInUserHandler.resetSavedSessionData()
                        navigateToPage(ScreenNavName.Login)
                    }
                    else {
                        Toast.makeText(getApplication<Application>().baseContext, "Succesfully deleted the user", Toast.LENGTH_LONG).show()
                        allUsers.emit(emptyList()) //make the user list reload
                        if(selectedOrganization.value != null) getAllUsersForOrganisation(authKey = authKey, organisationId = selectedOrganization.value!!.id)
                    }

                }
                else {
                    //call failed
                    Toast.makeText(getApplication<Application>().baseContext, "Something went wrong while deleting the user. Note that you can't delete the last admin of the organisation.", Toast.LENGTH_LONG).show()
                    Log.i("Error","DeleteUser failed response code")
                }
            }

        }
        catch(e: Throwable) {
            Log.i("Error","Error DeleteUser")
            Log.i("Error-Details",e.message.toString())
        }
    }

    fun launchConfirmChangingOrganizationName(authKey: String, organizationId: Long) {
        viewModelScope.launch {
            confirmChangingOrganizationName(authKey = authKey, organizationId = organizationId)
        }
    }

    suspend fun confirmChangingOrganizationName(authKey: String, organizationId: Long) {
        try {
            //the result of the call will contain a list of all users from that organisation
            val requestBody = OrganizationRequestBody(name = _inputOrganizationName.value)

            val result = organizationApi.updateOrganizationName(authToken = authKey,body = requestBody, organizationId = organizationId)
            val responseCode = result.raw().code
            if(responseCode >= 200 && responseCode < 300) {
                //call was succesfull
                Log.i("SUCCES","ConfirmChangingOrganizationName was succesfull")
                initialOrganizationName.value = requestBody.name
                Toast.makeText(getApplication<Application>().baseContext,"Succesfully changed the organisation name", Toast.LENGTH_LONG).show()

                //force reloading the organizations summary list
                //to force the recompesition WITH the same button state the .emit(emptyList()) is necessary aswell as changing the selectedOrganization name
                if(selectedOrganization.value != null) {selectedOrganization.value!!.name = requestBody.name}
                allOrganizationsSummary.emit(emptyList())
                getAllOrganisationsInfo(authKey = authKey)

            }
            else {
                Toast.makeText(getApplication<Application>().baseContext,"Organisation name is already occupied", Toast.LENGTH_LONG).show()
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

    fun toggleAreYouSureScreen() {
        areYouSureScreenIsOpen.value = !areYouSureScreenIsOpen.value
    }

    fun setPopUpScreenType(type: BasePopUpScreen) {
        popUpScreenType.value = type
    }

    fun toggleDynamicPopUpScreen() {
        if(userToEdit.value.id >= 0) {
            _inputFirstname.value = userToEdit.value.firstName
            _inputLastName.value = userToEdit.value.lastName
            _inputEmail.value = userToEdit.value.email
            _inputUserType.value = userToEdit.value.userType.toString()
        }

        popUpScreenIsOpen.value = !popUpScreenIsOpen.value

        areYouSureScreenIsOpen.value = false
    }

    fun resetUserToEdit() {
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

    fun setFirstNameInput(value: String) {
        _inputFirstname.value = value
    }

    fun setLastNameInput(value: String) {
        _inputLastName.value = value
    }

    fun setEmailInput(value: String) {
        _inputEmail.value = value
    }

    fun setUserTypeInput(value: String) {
        _inputUserType.value = value
    }

    fun setOrganizationNameInput(value: String) {
        if(value.length < 50) _inputOrganizationName.value = value
        else {
            Toast.makeText(getApplication<Application>().baseContext, "Max character length of the organisation name has been reached.", Toast.LENGTH_LONG).show()
        }
    }

    fun navigateToPage(navigateTo: ScreenNavName) {
        navController.navigate(route = navigateTo.route) {
            popUpTo(navigateTo.route) {
                inclusive = true
            }
        }
    }
}