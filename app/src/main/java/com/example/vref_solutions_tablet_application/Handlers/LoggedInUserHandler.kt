package com.example.vref_solutions_tablet_application.handlers

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.vref_solutions_tablet_application.models.User
import com.example.vref_solutions_tablet_application.enums.UserType
import com.example.vref_solutions_tablet_application.models.Organization
import com.example.vref_solutions_tablet_application.models.SearchQueryObject
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

    fun getSavedStudentSearchQueryInputList(): MutableList<SearchQueryObject> {
        //val typeObject: MutableList<SearchQueryObject> = emptyList<SearchQueryObject>().toMutableList()
        val typeObject = object : TypeToken<MutableList<SearchQueryObject>>() {}.type
        val stringResult = sharedPreferences.getString("SearchQueryInputList${getIdCurrentUser()}","").toString()
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

    fun setSavedStudentSearchQueryInputListAsString(updatedQueryList: MutableList<SearchQueryObject>, userId: String) {
        try {
            val value = gson.toJson(updatedQueryList,object : TypeToken<MutableList<SearchQueryObject>>() {}.type)
            Log.i("Set",value)
            sharedPreferences.edit().putString("SearchQueryInputList${userId}",value).apply()
        }
        catch(e: Throwable) {
            //minor crash if this fails so don't give onscreen feedback
        }

    }

    fun setCurrentlyLoggedInUserData(userInfo: User) {
        try {
            val value = gson.toJson(userInfo,object : TypeToken<User>() {}.type)
            sharedPreferences.edit().putString("currentlyLoggedInUserInfo",value).apply()
        }
        catch(e: Throwable) {
            Log.i("Error","Error while loading logged in user info")
        }

    }

    private fun getSavedUserInfo(): User? {
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

    fun getFirstNameCurrentUser(): String {
        val savedUserInfo: User? = getSavedUserInfo()
        if(savedUserInfo != null) return savedUserInfo.firstName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()) else it.toString() }
        else return ""
    }

    fun getLastNameCurrentUser(): String {
        val savedUserInfo: User? = getSavedUserInfo()
        if(savedUserInfo != null) return savedUserInfo.lastName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()) else it.toString() }
        else return ""
    }

    fun getUserOrganisationName(): String {
        val savedUserInfo: User? = getSavedUserInfo()
        if(savedUserInfo != null) return savedUserInfo.organization.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()) else it.toString() }
        else return ""
    }

    fun getFullNameCurrentUser(): String {
        val firstName = getFirstNameCurrentUser()
        val lastName = getLastNameCurrentUser()

        return "$firstName $lastName"
    }

    fun getEmailCurrentUser(): String {
        val savedUserInfo: User? = getSavedUserInfo()
        if(savedUserInfo != null) return savedUserInfo.email
        else return ""
    }

    fun getIdCurrentUser(): String {
        val savedUserInfo: User? = getSavedUserInfo()
        if(savedUserInfo != null) return savedUserInfo.id.toString()
        else return ""
    }

    fun getCompanyIdCurrentUser(): String {
        val savedUserInfo: User? = getSavedUserInfo()
        if(savedUserInfo != null) return savedUserInfo.organization.id.toString()
        else return ""
    }

    //Why not user UserIsOfType call from the front-end is for Readability of the code
    fun userIsInstructor(): Boolean {
        return userIsOfType(UserType.Instructor)
    }

    fun userIsAdmin(): Boolean {
        return userIsOfType(UserType.Admin)
    }

    fun userIsSuperAdmin(): Boolean {
        return userIsOfType(UserType.SuperAdmin)
    }

    private fun userIsOfType(userType: UserType): Boolean {
        val savedUserInfo: User? = getSavedUserInfo()
        if(savedUserInfo != null) return savedUserInfo.userType == userType
        else return false
    }

    fun logOut() {
        //for better code readability

        resetSavedSessionData()
    }

    fun resetSavedSessionData() {
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

        setCurrentlyLoggedInUserData(emptyUserObject)
        setGloballyAccessableAuthKey("")
    }

    fun setGloballyAccessableAuthKey(authKey: String) {
        sharedPreferences.edit().putString("authKey", "Bearer " + authKey).apply()
    }

    fun getAuthKey(): String {
        return sharedPreferences.getString("authKey", "").toString()
    }
}