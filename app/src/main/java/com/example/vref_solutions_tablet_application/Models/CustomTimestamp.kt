package com.example.vref_solutions_tablet_application.models

import java.time.LocalDateTime

class CustomTimestamp(
    val hours: Long,
    val minutes: Long,
    val seconds: Long,
    val miliseconds: Long,
) {
    companion object {
        fun getCurrentDateAsTimestamp(): CustomTimestamp {
            val currentDate = LocalDateTime.now()

            return CustomTimestamp(
                hours = currentDate.hour.toLong(),
                minutes = currentDate.minute.toLong(),
                seconds = currentDate.second.toLong(),
                miliseconds = 0.toLong(),
            )
        }

        fun createFromSeconds(totalSeconds: Long): CustomTimestamp {

            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val remainingSeconds = (totalSeconds % 3600) % 60

            return CustomTimestamp(hours = hours, minutes = minutes, seconds = remainingSeconds, 0)
        }
    }



    fun targetIsWithinDesiredSecondsOfThisTimestamp(targetCustomTimestamp: CustomTimestamp, desiredSeconds: Int): Boolean {

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

    fun isBeforeTargetTimestamp(targetCustomTimestamp: CustomTimestamp): Boolean {
        return this.toTotalSeconds() < targetCustomTimestamp.toTotalSeconds()
    }

    fun isAfterTargetTimestamp(targetCustomTimestamp: CustomTimestamp): Boolean {
        return this.toTotalSeconds() > targetCustomTimestamp.toTotalSeconds()
    }

    fun equalsTargetTimestamp(targetCustomTimestamp: CustomTimestamp): Boolean {
        return this.toTotalSeconds() == targetCustomTimestamp.toTotalSeconds()
    }

    fun toTotalSeconds(): Long {
        return ((getHours() * 3600) + (getMinutes() * 60) + getSeconds())
    }

    fun getHoursAndMinutesFormat(): String {
        return "${"%02d".format(hours)}:${"%02d".format(minutes)}"
    }

    fun getHoursMinutesAndSecondsFormat(): String {
        return "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
    }

    fun getMinutesAndSecondsFormat(): String {
        return "${"%02d".format(minutes)}:${"%02d".format(seconds)}"
    }

    @JvmName("getHours1")
    fun getHours(): Long {
        return hours
    }

    @JvmName("getMinutes1")
    fun getMinutes(): Long {
        return minutes
    }

    @JvmName("getSeconds1")
    fun getSeconds(): Long {
        return seconds
    }

    @JvmName("getMiliseconds1")
    fun getMiliseconds(): Long {
        return miliseconds
    }
}
