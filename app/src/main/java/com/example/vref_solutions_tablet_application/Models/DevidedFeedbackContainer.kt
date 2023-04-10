package com.example.vref_solutions_tablet_application.models

import com.example.vref_solutions_tablet_application.enums.FeedbackTarget

class DevidedFeedbackContainer() {
    var everyone: String = ""
    var studentOne: String = ""
    var studentTwo: String = ""

    fun getFeedbackByFeedbackTarget(target: FeedbackTarget): String {
        if(target == FeedbackTarget.Everyone) return everyone
        else if(target == FeedbackTarget.StudentOne) return studentOne
        else return studentTwo
    }
}
