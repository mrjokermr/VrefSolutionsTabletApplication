package com.example.vref_solutions_tablet_application.Handlers

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.vref_solutions_tablet_application.Models.User
import com.example.vref_solutions_tablet_application.Enums.UserType
import com.example.vref_solutions_tablet_application.Models.Organization
import com.example.vref_solutions_tablet_application.Models.SavableTrainingInfo
import com.example.vref_solutions_tablet_application.Models.SearchQueryObject
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.util.*

class LoggedInUserHandler() {

    private val gson = Gson() // for json convertions

    //save data with encryption essentials
    private lateinit var masterKeyAlias: String

    // Initialize/open an instance of EncryptedSharedPreferences on below line.
    private lateinit var sharedPreferences: SharedPreferences


    constructor(currentContext: Context) : this() {
        masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        sharedPreferences = EncryptedSharedPreferences.create(
            // passing a file name to share a preferences
            "preferences",
            masterKeyAlias,
            currentContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun GetSavedStudentSearchQueryInputList(): MutableList<SearchQueryObject> {
        //val typeObject: MutableList<SearchQueryObject> = emptyList<SearchQueryObject>().toMutableList()
        val typeObject = object : TypeToken<MutableList<SearchQueryObject>>() {}.type
        val stringResult = sharedPreferences.getString("SearchQueryInputList${GetIdCurrentUser()}","").toString()
        if(stringResult.length > 0) {
            try {
                var test: MutableList<SearchQueryObject> = gson.fromJson(stringResult, typeObject)
                Log.i("Get", test.toString())
                return gson.fromJson(stringResult, typeObject)
            }
            catch(e: Throwable) {
                Log.i("GetSavedStudentSearchQueryInputList-Error",e.message.toString())
                return emptyList<SearchQueryObject>().toMutableList()
            }

        }
        else return emptyList<SearchQueryObject>().toMutableList()

    }

    fun SetSavedStudentSearchQueryInputListAsString(updatedQueryList: MutableList<SearchQueryObject>, userId: String) {
        try {
            val value = gson.toJson(updatedQueryList,object : TypeToken<MutableList<SearchQueryObject>>() {}.type)
            Log.i("Set",value)
            sharedPreferences.edit().putString("SearchQueryInputList${userId}",value).apply()
        }
        catch(e: Throwable) {
            //minor crash if this fails so don't give onscreen feedback
        }

    }

    fun SetCurrentlyLoggedInUserData(userInfo: User) {
        try {
            val value = gson.toJson(userInfo,object : TypeToken<User>() {}.type)
            sharedPreferences.edit().putString("currentlyLoggedInUserInfo",value).apply()
        }
        catch(e: Throwable) {
            Log.i("Error","Error while loading logged in user info")
        }

    }

    private fun GetSavedUserInfo(): User? {
        //val typeObject: MutableList<SearchQueryObject> = emptyList<SearchQueryObject>().toMutableList()
        try {
            val typeObject = object : TypeToken<User>() {}.type
            val stringResult = sharedPreferences.getString("currentlyLoggedInUserInfo","").toString()
            if(stringResult.length > 0) {

                return gson.fromJson(stringResult, typeObject)
            }
            else return null
        }
        catch(e: Throwable) {
            return null
        }

    }

    fun GetFirstNameCurrentUser(): String {
        val savedUserInfo: User? = GetSavedUserInfo()
        if(savedUserInfo != null) return savedUserInfo.firstName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()) else it.toString() }
        else return ""
    }

    fun GetLastNameCurrentUser(): String {
        val savedUserInfo: User? = GetSavedUserInfo()
        if(savedUserInfo != null) return savedUserInfo.lastName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()) else it.toString() }
        else return ""
    }

    fun GetUserOrganisationName(): String {
        val savedUserInfo: User? = GetSavedUserInfo()
        if(savedUserInfo != null) return savedUserInfo.organization.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()) else it.toString() }
        else return ""
    }

    fun GetFullNameCurrentUser(): String {
        val firstName = GetFirstNameCurrentUser()
        val lastName = GetLastNameCurrentUser()

        return "$firstName $lastName"
    }

    fun GetEmailCurrentUser(): String {
        val savedUserInfo: User? = GetSavedUserInfo()
        if(savedUserInfo != null) return savedUserInfo.email
        else return ""
    }

    fun GetIdCurrentUser(): String {
        val savedUserInfo: User? = GetSavedUserInfo()
        if(savedUserInfo != null) return savedUserInfo.id.toString()
        else return ""
    }

    fun GetCompanyIdCurrentUser(): String {
        val savedUserInfo: User? = GetSavedUserInfo()
        if(savedUserInfo != null) return savedUserInfo.organization.id.toString()
        else return ""
    }

    //Why not user UserIsOfType call from the front-end is for Readability of the code
    fun UserIsInstructor(): Boolean {
        return UserIsOfType(UserType.Instructor)
    }

    fun UserIsAdmin(): Boolean {
        return UserIsOfType(UserType.Admin)
    }

    fun UserIsSuperAdmin(): Boolean {
        return UserIsOfType(UserType.SuperAdmin)
    }

    private fun UserIsOfType(userType: UserType): Boolean {
        val savedUserInfo: User? = GetSavedUserInfo()
        if(savedUserInfo != null) return savedUserInfo.userType == userType
        else return false
    }

    fun LogOut() {
        //for better code readability

        ResetSavedSessionData()
    }

    fun ResetSavedSessionData() {
        val emptyUserObject: User = User(
            id = -1,
            email = "",
            firstName = "",
            lastName = "",
            organization = Organization(
                id = -1,
                name = ""
            ),
            userType = UserType.Student
        )

        SetCurrentlyLoggedInUserData(emptyUserObject)
        SetGloballyAccessableAuthKey("")
    }

    fun SetGloballyAccessableAuthKey(authKey: String) {
        sharedPreferences.edit().putString("authKey", "Bearer " + authKey).apply()
    }

    fun GetAuthKey(): String {
        return sharedPreferences.getString("authKey", "").toString()
    }
}