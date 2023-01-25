package com.example.vref_solutions_tablet_application.Models

import java.time.LocalDateTime

class CustomTimestamp(
    val hours: Long,
    val minutes: Long,
    val seconds: Long,
    val miliseconds: Long,
) {
    companion object {
        fun GetCurrentDateAsTimestamp(): CustomTimestamp {
            val currentDate = LocalDateTime.now()

            return CustomTimestamp(
                hours = currentDate.hour.toLong(),
                minutes = currentDate.minute.toLong(),
                seconds = currentDate.second.toLong(),
                miliseconds = 0.toLong(),
            )
        }

        fun CreateFromSeconds(totalSeconds: Long): CustomTimestamp {

            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val remainingSeconds = (totalSeconds % 3600) % 60

            return CustomTimestamp(hours = hours, minutes = minutes, seconds = remainingSeconds, 0)
        }
    }



    fun TargetIsWithinDesiredSecondsOfThisTimestamp(targetCustomTimestamp: CustomTimestamp, desiredSeconds: Int): Boolean {

        val totalSecondsThisTimestamp = this.toTotalSeconds()
        val totalSecondsTargetTimestamp = targetCustomTimestamp.toTotalSeconds()

        if(totalSecondsTargetTimestamp > totalSecondsThisTimestamp) {
            val secondsDifference = totalSecondsTargetTimestamp - totalSecondsThisTimestamp

            return secondsDifference <= desiredSeconds
        }
        else return false


    //calculate hours and minutes to seconds and compare after
//        val totalSecondsThisTimestamp = this.toTotalSeconds()
//        val totalSecondsTargetTimestamp = targetCustomTimestamp.toTotalSeconds()
//
//        //times minus eachother if the number is 0 to 14 it is within 15 seconds
//        val secondsDifference = totalSecondsThisTimestamp - totalSecondsTargetTimestamp
//
//        if(secondsDifference >= 0 && secondsDifference <= desiredSeconds) return true
//        else return false

    }

    fun IsBeforeTargetTimestamp(targetCustomTimestamp: CustomTimestamp): Boolean {
        return this.toTotalSeconds() < targetCustomTimestamp.toTotalSeconds()
    }

    fun IsAfterTargetTimestamp(targetCustomTimestamp: CustomTimestamp): Boolean {
        return this.toTotalSeconds() > targetCustomTimestamp.toTotalSeconds()
    }

    fun EqualsTargetTimestamp(targetCustomTimestamp: CustomTimestamp): Boolean {
        return this.toTotalSeconds() == targetCustomTimestamp.toTotalSeconds()
    }

    fun toTotalSeconds(): Long {
        return ((GetHours() * 3600) + (GetMinutes() * 60) + GetSeconds())
    }

    fun GetHoursAndMinutesFormat(): String {
        return "${"%02d".format(hours)}:${"%02d".format(minutes)}"
    }

    fun GetHoursMinutesAndSecondsFormat(): String {
        return "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
    }

    fun GetMinutesAndSecondsFormat(): String {
        return "${"%02d".format(minutes)}:${"%02d".format(seconds)}"
    }

    fun GetHours(): Long {
        return hours
    }

    fun GetMinutes(): Long {
        return minutes
    }

    fun GetSeconds(): Long {
        return seconds
    }

    fun GetMiliseconds(): Long {
        return miliseconds
    }
}
