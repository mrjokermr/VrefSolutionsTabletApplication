package com.example.vref_solutions_tablet_application.Models

import android.util.Log
import androidx.compose.ui.text.capitalize
import com.example.vref_solutions_tablet_application.Enums.TrainingStatus
import java.time.LocalDate

class TrainingSummary(
    val id: Long,
    val creationDateTime: String,
    val students: List<User>,
    val instructor: User,
    val status: TrainingStatus,
) {


    fun GetLocalDate(): LocalDate {
        var dateString = GetReadableDate()
        try {
            return LocalDate.of(dateString.split('-')[0].toInt(),dateString.split('-')[1].toInt(),dateString.split('-')[2].toInt())
        }
        catch(e: Throwable) {
            return LocalDate.now()
        }
    }

    fun IsViewable() : Boolean {
        //Checking the various states which a training can have
        //some of these states are only really necessary for the API back end and should be implemented different

        //training events are viewable when: Processing, Finished
        //training events are not viewable when: Created, Paused, Recording

        if(status == TrainingStatus.Finished) return true
        else if(status == TrainingStatus.Processing) return true
        else return false
    }

    fun GetReadableDate(): String {
        //e.x. creationDateTime = 2022-11-07T22:23:40.2401897
        val splittedDate = creationDateTime.split("T")[0].split('-')
        return "${splittedDate[0]}-${splittedDate[1]}-${splittedDate[2]}"
    }

    fun GetTrainingSummaryListDate(): String {
        val d = GetLocalDate()
        return "${d.dayOfMonth} ${d.month.toString().lowercase().replaceFirstChar{ it.uppercase() }} ${d.year}"
    }

    fun GetReadableTime(): String {
        //e.x. creationDateTime = 2022-11-07T22:23:40.2401897
        return creationDateTime.split("T")[1].split(".")[0]

    }
}
