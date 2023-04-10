package com.example.vref_solutions_tablet_application.models

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateSelectAndDisplayObject {
    //two different display objects because the display date can be 'Today' so the display day still has to be set
    var displayDate: String = ""
    var date: LocalDate
    var displayDay: String = ""
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yy")
    val displayText: String


    constructor(date: LocalDate,displayText: String) {
        this.date = date
        this.displayText = displayText
        setDateDisplay()
    }

    private fun setDateDisplay() {
        displayDate = if(date != LocalDate.now()) date.format(formatter) else "Today"
        displayDay = "%02d".format(date.dayOfMonth)
    }

    fun setDateDisplay(date: LocalDate) {
        this.date = date
        this.setDateDisplay()
    }
}