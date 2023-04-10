package com.example.vref_solutions_tablet_application.models

import com.example.vref_solutions_tablet_application.enums.FeedbackTarget
import com.example.vref_solutions_tablet_application.handlers.IconDisplayHandler
import com.example.vref_solutions_tablet_application.handlers.PrescribedEventsHandler

class TrainingEvent(
    val id: Long,
    val name: String,
    var symbol: String,
    val timeStamp: CustomTimestamp,
    var message: String,
) {

    companion object {
        fun getMessageAsReadableDevidedFeedback(feedbackContainer: DevidedFeedbackContainer): String {
            var devidedableMessageString = ""

            devidedableMessageString += "[everyone] " + feedbackContainer.everyone

            if(feedbackContainer.studentOne.length > 0) devidedableMessageString += " [studentOne] " + feedbackContainer.studentOne
            if(feedbackContainer.studentOne.length > 0)  devidedableMessageString += " [studentTwo] " + feedbackContainer.studentTwo

            return devidedableMessageString
        }
    }

    var devidedFeedbackContainer = DevidedFeedbackContainer()

    init {
        splitMessageIntoDevidedFeedbackMessage()
    }

    private fun splitMessageIntoDevidedFeedbackMessage() {
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

    fun setNewSymbolName(newSymbolName: String) {
        if(newSymbolName == "fb_manual" || newSymbolName == "fb_quick") symbol = newSymbolName
        else {
            //fb_quick training-event will be converted to fb_manual because the icon is manually changed
            if(symbol.contains("fb_manual") || symbol.contains("fb_quick")) symbol = "fb_manual_[" + newSymbolName + "]"
        }

    }

    fun getIconPainterId(): Int {
        if(symbol.contains("fb_prescribed")) return PrescribedEventsHandler.getDrawableIdBySymbolName(symbol)
        else return IconDisplayHandler.getPainterIdForSymbolName(symbolName = symbol)
    }

    fun isPrescribedEvent(): Boolean {
        return symbol.contains("fb_prescribed")
    }

    fun isInstructorsFeedback(): Boolean {
        if(symbol.contains("fb_manual") || symbol.contains("fb_quick")) return true
        else return false
    }

    fun isQuickInstructorFeedback(): Boolean {
        return symbol.contains("fb_quick")
    }

    fun isManualInstructorFeedback(): Boolean {
        return symbol.contains("fb_manual")
    }

    fun isAutomaticFeedback(): Boolean {
        if(symbol.contains("fb_auto")) return true
        else return false
    }
}
