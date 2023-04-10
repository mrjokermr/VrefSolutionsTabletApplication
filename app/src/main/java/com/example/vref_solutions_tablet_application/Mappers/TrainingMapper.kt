package com.example.vref_solutions_tablet_application.mappers

import com.example.vref_solutions_tablet_application.api.responseEntities.*
import com.example.vref_solutions_tablet_application.models.*

object TrainingMapper {

    fun map(entity: TrainingResponseEntity): Result<Training> = runCatching {
        Training(
            id = entity.id!!,
            videos = entity.videos!!,
            videoAccesURLs = entity.videoAccesURLs,
            events = entity.events!!,
            altitudes = entity.altitudes!!,
            students = entity.students!!,
            instructor = entity.instructor!!,
            creationDateTime = entity.creationDateTime!!,
            status = entity.status!!,
        )
    }

    fun mapTrainingEvent(entity: TrainingEventResponseEntity): Result<TrainingEvent> = runCatching {
        TrainingEvent(
            id = entity.id!!,
            name = entity.name!!,
            symbol = entity.symbol!!,
            timeStamp = entity.timeStamp!!,
            message = entity.message!!
        )
    }

    fun mapListTrainingEvents(trainingEventEntityList: List<TrainingEventResponseEntity>): List<TrainingEvent> {
        val mappedList: MutableList<TrainingEvent> = emptyList<TrainingEvent>().toMutableList()

        for(trainingEventEntity in trainingEventEntityList) {
            mapTrainingEvent(entity = trainingEventEntity).onSuccess {
                mappedList.add(it)
            }
        }

        return mappedList
    }

    fun mapListTrainingSummary(trainingSummaryResponsEntityList: List<TrainingSummaryResponseEntity>): List<TrainingSummary> {
        val mappedList: MutableList<TrainingSummary> = emptyList<TrainingSummary>().toMutableList()

        for(trainingSummaryEntity in trainingSummaryResponsEntityList) {
            map(trainingSummaryEntity).onSuccess {
                mappedList.add(it)
            }
        }

        return mappedList
    }

    fun map(entity: TrainingSummaryResponseEntity): Result<TrainingSummary> = runCatching {
        TrainingSummary(
            id = entity.id!!,
            creationDateTime = entity.creationDateTime!!,
            students = entity.students!!,
            instructor = entity.instructor!!,
            status = entity.status!!
        )
    }
}