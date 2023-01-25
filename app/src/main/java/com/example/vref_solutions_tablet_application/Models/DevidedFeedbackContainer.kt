package com.example.vref_solutions_tablet_application.Models

import com.example.vref_solutions_tablet_application.Enums.FeedbackTarget

class DevidedFeedbackContainer() {
    var everyone: String = ""
    var studentOne: String = ""
    var studentTwo: String = ""

    fun GetFeedbackByFeedbackTarget(target: FeedbackTarget): String {
        if(target == FeedbackTarget.Everyone) return everyone
        else if(target == FeedbackTarget.StudentOne) return studentOne
        else return studentTwo
    }
}
