package com.example.vref_solutions_tablet_application.Handlers

import com.example.vref_solutions_tablet_application.Models.TrainingEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FeedbackEventFilterHandler {

    private var removeInstructorFeedbackFilterIsOn = MutableStateFlow(false)
    val removeInstructorFeedbackFlow: StateFlow<Boolean> = removeInstructorFeedbackFilterIsOn

    private var removeAutomaticFeedbackFilterIsOn = false
    private var removeQuickInstructorFeedbackFilterIsOn = false
    private var removeManualInstructorFeedbackFilterIsOn = false
    private var removePrescribedEventFeedbackFilterIsOn = false

    fun FilterTrainingevents(initialList: List<TrainingEvent>): List<TrainingEvent> {
        val allFilteredTrainingEvents: MutableList<TrainingEvent> = emptyList<TrainingEvent>().toMutableList()

        for(trainingEvent in initialList) {
            //a training event symbol name will be as follows:
            // fb_manual or fb_quick are added by the instructor and fb_auto is added by the video A.I. or other A.I
            if(removeInstructorFeedbackFilterIsOn.value == false && trainingEvent.IsInstructorsFeedback()) {

                if(removeQuickInstructorFeedbackFilterIsOn == false && trainingEvent.IsQuickInstructorFeedback()) {
                    allFilteredTrainingEvents.add(trainingEvent)
                }
                else if(removeManualInstructorFeedbackFilterIsOn == false && trainingEvent.IsManualInstructorFeedback()) {
                    allFilteredTrainingEvents.add(trainingEvent)
                }
            }

            if(!removePrescribedEventFeedbackFilterIsOn && removeInstructorFeedbackFilterIsOn.value == false  && trainingEvent.IsPrescribedEvent()) {
                 allFilteredTrainingEvents.add(trainingEvent)
            }

            if(!removeAutomaticFeedbackFilterIsOn && trainingEvent.IsAutomaticFeedback()) {
                allFilteredTrainingEvents.add(trainingEvent)
            }
        }

        return allFilteredTrainingEvents
    }

    fun TogglePrescribedEventsFeedbackFilter() {
        removePrescribedEventFeedbackFilterIsOn = !removePrescribedEventFeedbackFilterIsOn
    }

    fun ToggleManualInstructorFeedbackFilter() {
        removeManualInstructorFeedbackFilterIsOn = !removeManualInstructorFeedbackFilterIsOn
    }

    fun ToggleQuickInstructorFeedbackFilter() {
        removeQuickInstructorFeedbackFilterIsOn = !removeQuickInstructorFeedbackFilterIsOn
    }

    fun AllInstructorFeedbackOptionsAreRemoved(): Boolean {
        return removeManualInstructorFeedbackFilterIsOn && removeQuickInstructorFeedbackFilterIsOn && removePrescribedEventFeedbackFilterIsOn
    }

    fun ToggleAutomaticFeedbackEventFilter() {
        removeAutomaticFeedbackFilterIsOn = !removeAutomaticFeedbackFilterIsOn
    }

    fun ToggleInstructorFeedbackEventFilter() {
        removeInstructorFeedbackFilterIsOn.value = !removeInstructorFeedbackFilterIsOn.value

        if(removeInstructorFeedbackFilterIsOn.value) {
            removeQuickInstructorFeedbackFilterIsOn = false
            removeManualInstructorFeedbackFilterIsOn = false
            removePrescribedEventFeedbackFilterIsOn = false
        }
    }
}