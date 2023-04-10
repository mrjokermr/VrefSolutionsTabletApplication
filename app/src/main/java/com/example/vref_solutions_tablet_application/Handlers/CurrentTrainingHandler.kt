package com.example.vref_solutions_tablet_application.handlers

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.vref_solutions_tablet_application.models.*
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CurrentTrainingHandler() {

    //save data with encryption essentials
    private lateinit var masterKeyAlias: String

    // Initialize/open an instance of EncryptedSharedPreferences on below line.
    private lateinit var sharedPreferences: SharedPreferences

    private val gson = Gson() // for json convertions


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



    fun setCurrentTrainingInfo(currentTraining: Training) {
        mapTrainingToSavableTrainingInfo(currentTraining).onSuccess {
            //it = SavableTrainingInfo
            try {
                val value = gson.toJson(it,object : TypeToken<SavableTrainingInfo>() {}.type)
                sharedPreferences.edit().putString("currentTrainingInfo",value).apply()
            }
            catch(e: Throwable) {

            }
        }

    }

    fun setCurrentTrainingInfo(currentTraining: TrainingSummary) {
        mapTrainingToSavableTrainingInfo(currentTraining).onSuccess {
            //it = SavableTrainingInfo
            try {
                val value = gson.toJson(it,object : TypeToken<SavableTrainingInfo>() {}.type)
                Log.i("saved value",value.toString())
                sharedPreferences.edit().putString("currentTrainingInfo",value).apply()
            }
            catch(e: Throwable) {

            }
        }

    }

    //Two types of Mappers because a training can be continued from TrainingSummary data class or a new training can be started from the StartNewTraining screen which
    //works with a Training class
    private fun mapTrainingToSavableTrainingInfo(currentTraining: Training): Result<SavableTrainingInfo> = runCatching {
        SavableTrainingInfo(
            id = currentTraining.id!!,
            creationDateTime = currentTraining.creationDateTime!!,
            students = currentTraining.students!!,
            instructor = currentTraining.instructor!!,
            status = currentTraining.status!!,
        )
    }

    private fun mapTrainingToSavableTrainingInfo(currentTraining: TrainingSummary): Result<SavableTrainingInfo> = runCatching {
        SavableTrainingInfo(
            id = currentTraining.id!!,
            creationDateTime = currentTraining.creationDateTime!!,
            students = currentTraining.students!!,
            instructor = currentTraining.instructor!!,
            status = currentTraining.status!!,
        )
    }

    private fun getSavedTrainingInfo(): SavableTrainingInfo? {
        //val typeObject: MutableList<SearchQueryObject> = emptyList<SearchQueryObject>().toMutableList()
        try {
            val typeObject = object : TypeToken<SavableTrainingInfo>() {}.type
            val stringResult = sharedPreferences.getString("currentTrainingInfo","").toString()
            if(stringResult.length > 0) {

                return gson.fromJson(stringResult, typeObject)
            }
            else return null
        }
        catch(e: Throwable) {
            return null
        }

    }

    fun getCurrentTrainingId(): String {
        val savedTrainingInfo: SavableTrainingInfo? = getSavedTrainingInfo()
        if(savedTrainingInfo != null) return savedTrainingInfo.id.toString()
        else return ""
    }

    fun getFirstStudentId(): String {
        val savedTrainingInfo: SavableTrainingInfo? = getSavedTrainingInfo()
        if(savedTrainingInfo != null) {
            if(savedTrainingInfo.students.size > 0) return savedTrainingInfo.students.first().id.toString()
        }

        return "-1" //failed to find the first user id so return -1
    }

    fun getSecondStudentId(): String {
        val savedTrainingInfo: SavableTrainingInfo? = getSavedTrainingInfo()
        if(savedTrainingInfo != null) {
            if(savedTrainingInfo.students.size > 1) return savedTrainingInfo.students[1].id.toString()
        }

        return "-1" //failed to find the second user id so return -1
    }

    fun getCurrentTrainingCreationDate(topBarFormat: Boolean): String {
        try {
            val savedTrainingInfo: SavableTrainingInfo? = getSavedTrainingInfo()
            if(savedTrainingInfo != null) {
                if(topBarFormat) return savedTrainingInfo.creationDateTime.split('T')[0].replace('-','/')
                else return savedTrainingInfo.creationDateTime.split('T')[0]
            }
            else return "00-00-00"
        }
        catch(e: Throwable) {
            return "00-00-00"
        }
    }

    fun getCurrentTrainingDateTimeAsCustomTimestamp(): CustomTimestamp {
        try {
            val savedTrainingInfo: SavableTrainingInfo? = getSavedTrainingInfo()
            if(savedTrainingInfo != null) {
                // full date time example: 2023-01-10T09:27:26.2396399+00:00
                val extractedTimeStamp = savedTrainingInfo.creationDateTime.split('T')[1]
                //09:27:26.2396399+00:00 is extracted time stamp example

                //plus one because of timezone
                val hours = extractedTimeStamp.split(':')[0].toLong() + 1
                val minutes = extractedTimeStamp.split(':')[1].toLong()
                val seconds = extractedTimeStamp.split(':')[2].split('.')[0].toLong()

                return CustomTimestamp(hours,minutes,seconds,0)
            }
        }
        catch(e: Throwable) {

        }

        return CustomTimestamp(0,0,0,0) //if everything failed return this timestamp

    }

    fun getCurrentTrainingDateAsDateTimeObject(): LocalDateTime {
        try {
            val savedTrainingInfo: SavableTrainingInfo? = getSavedTrainingInfo()
            if(savedTrainingInfo != null) {
                // full date time example: 2023-01-10T09:27:26.2396399+00:00
                val extractedDate = savedTrainingInfo.creationDateTime.split('T')[0]
                val extractedTimeStamp = savedTrainingInfo.creationDateTime.split('T')[1]
                //2023-01-10
                val dateString = extractedDate.split('-')[2] + " " + extractedDate.split('-')[1] + " " + extractedDate.split('-')[0]
                val date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd MM yyyy"))
                val timeString = extractedTimeStamp.split(':')[0] + ":00:00"
                val time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm:ss"))

                return LocalDateTime.of(date, time)
            }
        }
        catch(e: Throwable) {

        }

        return LocalDateTime.now() //if everything failed return this
    }

    fun getCurrentTrainingDateTime(): String {
        val savedTrainingInfo: SavableTrainingInfo? = getSavedTrainingInfo()
        if(savedTrainingInfo != null) {
            //extract the readable time from the string. output will be e.x. 09:30
            var fullTime = savedTrainingInfo.creationDateTime.split('T')[1]
            return fullTime.split(':')[0] + ":" +  fullTime.split(':')[1]
        }
        else return ""
    }

    fun getAuthKey(): String {
        return sharedPreferences.getString("authKey", "").toString()
    }
}