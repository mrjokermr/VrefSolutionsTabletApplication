package com.example.vref_solutions_tablet_application.handlers

import android.util.Log
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.models.CustomTimestamp
import com.example.vref_solutions_tablet_application.models.TimelineEventsDisplayDataItem
import com.example.vref_solutions_tablet_application.models.TrainingEvent
import java.lang.Math.floor

object TimelineDisplayHandler {

//    trainingEvent --> bevat een timestamp
//
//    displayItem is een blok die x timestamp --> X seconden --> x timestamp bevat
//    die een List<trainingEvent> met trainingEvents voor die training heeft
//
//    wat nodig is een berekening voor het aantal displayItems
//    die dus ook een van x timestamp naar y timestamp heeft
//
//    en dan vervolgens door deze allemaal heen loopen om ze te displayen
//    de eerste timestamp is vanaf wanneer de training begint tot de huidige tijd
val secondsPerBlock: Int = 60 // 5 min: 300 , 10 min: 600
    val spacePerDisplayBlock: Dp = 90.dp
    val spacePerTimeLineItem: Dp = 30.dp

    fun convertTrainingEventsListToDisplayableTimelineEventsList(allTrainingEvents: List<TrainingEvent>, fromTimestamp:CustomTimestamp, untilTimestamp: CustomTimestamp): List<TimelineEventsDisplayDataItem> {
        try {
            val remainingTrainingEvents = allTrainingEvents.toMutableList()

            val currentTimelineSpanInSeconds = (untilTimestamp.toTotalSeconds() - fromTimestamp.toTotalSeconds()).toDouble()
            //number below represents how many 'secondsPerBlock' seconds blocks exist within the current training timespan
            val amountOfTimelineDisplayEventsNeeded = floor(currentTimelineSpanInSeconds / TimelineDisplayHandler.secondsPerBlock).toInt()

            val newTimelineEventsList = TimelineDisplayHandler.createListWithDesiredSecondsBlocks(amountOfListItems =  amountOfTimelineDisplayEventsNeeded, fromTimestamp = fromTimestamp)

            //with this empty new timeline events list loop over all training events to look which one needs to be added to which 15 seconds block
            for(timelineDisplayBlock in newTimelineEventsList) {

                for (trainingEvent in remainingTrainingEvents) {
                    if (timelineDisplayBlock.fromCustomTimestamp.targetIsWithinDesiredSecondsOfThisTimestamp(
                            targetCustomTimestamp = trainingEvent.timeStamp,
                            desiredSeconds = TimelineDisplayHandler.secondsPerBlock
                        )
                    ) {
                        timelineDisplayBlock.trainingEventsInThisBlock.add(0, trainingEvent)
                    }
                }

            }

            return newTimelineEventsList
        }
        catch(e: Throwable) {
            Log.i("BUG","error converting list to timelineEventsList")
            return emptyList()
        }

    }

    private fun createListWithDesiredSecondsBlocks(amountOfListItems: Int, fromTimestamp: CustomTimestamp): List<TimelineEventsDisplayDataItem> {
        var previousUsedTimestamp: CustomTimestamp? = null

        val newTimelineEventsList = mutableListOf<TimelineEventsDisplayDataItem>()

        for(i in 0..amountOfListItems) {

            if(previousUsedTimestamp == null) {
                //no first item is set so init the first item in the list
                newTimelineEventsList.add(
                    TimelineEventsDisplayDataItem(
                        fromCustomTimestamp = fromTimestamp,
                        trainingEventsInThisBlock = mutableListOf()
                    )
                )

                previousUsedTimestamp = fromTimestamp
            }
            else {
                val nextSecondsTimeStamp: CustomTimestamp = CustomTimestamp.createFromSeconds(previousUsedTimestamp.toTotalSeconds() + TimelineDisplayHandler.secondsPerBlock)

                newTimelineEventsList.add(
                    TimelineEventsDisplayDataItem(
                        fromCustomTimestamp = nextSecondsTimeStamp,
                        trainingEventsInThisBlock = mutableListOf()
                    )
                )

                //update the previous used timestamp
                previousUsedTimestamp = nextSecondsTimeStamp
            }

        }


        return newTimelineEventsList
    }

}