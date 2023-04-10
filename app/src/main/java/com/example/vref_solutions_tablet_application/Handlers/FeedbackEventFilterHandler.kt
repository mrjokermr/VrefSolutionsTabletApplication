package com.example.vref_solutions_tablet_application.handlers

import com.example.vref_solutions_tablet_application.models.TrainingEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FeedbackEventFilterHandler {

    private var removeInstructorFeedbackFilterIsOn = MutableStateFlow(false)
    val removeInstructorFeedbackFlow: StateFlow<Boolean> = removeInstructorFeedbackFilterIsOn

    private var removeAutomaticFeedbackFilterIsOn = false
    private var removeQuickInstructorFeedbackFilterIsOn = false
    private var removeManualInstructorFeedbackFilterIsOn = false
    private var removePrescribedEventFeedbackFilterIsOn = false

    fun filterTrainingevents(initialList: List<TrainingEvent>): List<TrainingEvent> {
        val allFilteredTrainingEvents: MutableList<TrainingEvent> = emptyList<TrainingEvent>().toMutableList()

        for(trainingEvent in initialList) {
            //a training event symbol name will be as follows:
            // fb_manual or fb_quick are added by the instructor and fb_auto is added by the video A.I. or other A.I
            if(removeInstructorFeedbackFilterIsOn.value == false && trainingEvent.isInstructorsFeedback()) {

                if(removeQuickInstructorFeedbackFilterIsOn == false && trainingEvent.isQuickInstructorFeedback()) {
                    allFilteredTrainingEvents.add(trainingEvent)
                }
                else if(removeManualInstructorFeedbackFilterIsOn == false && trainingEvent.isManualInstructorFeedback()) {
                    allFilteredTrainingEvents.add(trainingEvent)
                }
            }

            if(!removePrescribedEventFeedbackFilterIsOn && removeInstructorFeedbackFilterIsOn.value == false  && trainingEvent.isPrescribedEvent()) {
                 allFilteredTrainingEvents.add(trainingEvent)
            }

            if(!removeAutomaticFeedbackFilterIsOn && trainingEvent.isAutomaticFeedback()) {
                allFilteredTrainingEvents.add(trainingEvent)
            }
        }

        return allFilteredTrainingEvents
    }

    fun togglePrescribedEventsFeedbackFilter() {
        removePrescribedEventFeedbackFilterIsOn = !removePrescribedEventFeedbackFilterIsOn
    }

    fun toggleManualInstructorFeedbackFilter() {
        removeManualInstructorFeedbackFilterIsOn = !removeManualInstructorFeedbackFilterIsOn
    }

    fun toggleQuickInstructorFeedbackFilter() {
        removeQuickInstructorFeedbackFilterIsOn = !removeQuickInstructorFeedbackFilterIsOn
    }

    fun allInstructorFeedbackOptionsAreRemoved(): Boolean {
        return removeManualInstructorFeedbackFilterIsOn && removeQuickInstructorFeedbackFilterIsOn && removePrescribedEventFeedbackFilterIsOn
    }

    fun toggleAutomaticFeedbackEventFilter() {
        removeAutomaticFeedbackFilterIsOn = !removeAutomaticFeedbackFilterIsOn
    }

    fun toggleInstructorFeedbackEventFilter() {
        removeInstructorFeedbackFilterIsOn.value = !removeInstructorFeedbackFilterIsOn.value

        if(removeInstructorFeedbackFilterIsOn.value) {
            removeQuickInstructorFeedbackFilterIsOn = false
            removeManualInstructorFeedbackFilterIsOn = false
            removePrescribedEventFeedbackFilterIsOn = false
        }
    }
}