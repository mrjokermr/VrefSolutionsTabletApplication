package com.example.vref_solutions_tablet_application.Models

import android.util.Log
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.vref_solutions_tablet_application.Enums.FeedbackTarget
import com.example.vref_solutions_tablet_application.Enums.IconNames
import com.example.vref_solutions_tablet_application.Handlers.IconDisplayHandler
import com.example.vref_solutions_tablet_application.Handlers.PrescribedEventsHandler
import com.example.vref_solutions_tablet_application.R

class TrainingEvent(
    val id: Long,
    val name: String,
    var symbol: String,
    val timeStamp: CustomTimestamp,
    var message: String,
) {

    companion object {
        fun GetMessageAsReadableDevidedFeedback(feedbackContainer: DevidedFeedbackContainer): String {
            var devidedableMessageString = ""

            devidedableMessageString += "[everyone] " + feedbackContainer.everyone

            if(feedbackContainer.studentOne.length > 0) devidedableMessageString += " [studentOne] " + feedbackContainer.studentOne
            if(feedbackContainer.studentOne.length > 0)  devidedableMessageString += " [studentTwo] " + feedbackContainer.studentTwo

            return devidedableMessageString
        }
    }

    var devidedFeedbackContainer = DevidedFeedbackContainer()

    init {
        SplitMessageIntoDevidedFeedbackMessage()
    }

    private fun SplitMessageIntoDevidedFeedbackMessage() {
        //Why not used JSON? because the other student implementing this feedback in his front-end didn't include this functionality in his/her app
        //so without using JSON and by using this custom method the feedback on his screen will be: [everyone] has flown nicely [studentOne] did extremely well
        //otherwise it would be a 'unreadable' and ugly string in his/her front-end with a lot of { } { } { } symbols.
        if(message.contains("[everyone]") || message.contains("[studentOne]") || message.contains("[studentTwo]")) {
            val allWords = message.split(' ')
            var currTarget: FeedbackTarget = FeedbackTarget.Everyone

            for(word in allWords) {
                if(word == "[everyone]") {
                    currTarget = FeedbackTarget.Everyone
                }
                else if(word == "[studentOne]") {
                    currTarget = FeedbackTarget.StudentOne
                }
                else if(word == "[studentTwo]") {
                    currTarget = FeedbackTarget.StudentTwo
                }
                else {
                    if(currTarget == FeedbackTarget.Everyone) devidedFeedbackContainer.everyone += " $word"
                    else if(currTarget == FeedbackTarget.StudentOne) devidedFeedbackContainer.studentOne += " $word"
                    else devidedFeedbackContainer.studentTwo += " $word"
                }
            }
        }
        else devidedFeedbackContainer.everyone = message
    }

    fun SetNewSymbolName(newSymbolName: String) {
        if(newSymbolName == "fb_manual" || newSymbolName == "fb_quick") symbol = newSymbolName
        else {
            //fb_quick training-event will be converted to fb_manual because the icon is manually changed
            if(symbol.contains("fb_manual") || symbol.contains("fb_quick")) symbol = "fb_manual_[" + newSymbolName + "]"
        }

    }

    fun GetIconPainterId(): Int {
        if(symbol.contains("fb_prescribed")) return PrescribedEventsHandler.GetDrawableIdBySymbolName(symbol)
        else return IconDisplayHandler.GetPainterIdForSymbolName(symbolName = symbol)
    }

    fun IsPrescribedEvent(): Boolean {
        return symbol.contains("fb_prescribed")
    }

    fun IsInstructorsFeedback(): Boolean {
        if(symbol.contains("fb_manual") || symbol.contains("fb_quick")) return true
        else return false
    }

    fun IsQuickInstructorFeedback(): Boolean {
        return symbol.contains("fb_quick")
    }

    fun IsManualInstructorFeedback(): Boolean {
        return symbol.contains("fb_manual")
    }

    fun IsAutomaticFeedback(): Boolean {
        if(symbol.contains("fb_auto")) return true
        else return false
    }
}
