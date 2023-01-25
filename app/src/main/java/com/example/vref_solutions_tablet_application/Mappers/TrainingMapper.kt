package com.example.vref_solutions_tablet_application.Mappers

import com.example.vref_solutions_tablet_application.API.ResponseEntities.*
import com.example.vref_solutions_tablet_application.Enums.TrainingStatus
import com.example.vref_solutions_tablet_application.Models.*

class TrainingMapper {

    companion object {
        fun Map(entity: TrainingResponseEntity): Result<Training> = runCatching {
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

        fun MapTrainingEvent(entity: TrainingEventResponseEntity): Result<TrainingEvent> = runCatching {
            TrainingEvent(
                id = entity.id!!,
                name = entity.name!!,
                symbol = entity.symbol!!,
                timeStamp = entity.timeStamp!!,
                message = entity.message!!
            )
        }

        fun MapListTrainingEvents(trainingEventEntityList: List<TrainingEventResponseEntity>): List<TrainingEvent> {
            val mappedList: MutableList<TrainingEvent> = emptyList<TrainingEvent>().toMutableList()

            for(trainingEventEntity in trainingEventEntityList) {
                MapTrainingEvent(entity = trainingEventEntity).onSuccess {
                    mappedList.add(it)
                }
            }

            return mappedList
        }

        fun MapListTrainingSummary(trainingSummaryResponsEntityList: List<TrainingSummaryResponseEntity>): List<TrainingSummary> {
            val mappedList: MutableList<TrainingSummary> = emptyList<TrainingSummary>().toMutableList()

            for(trainingSummaryEntity in trainingSummaryResponsEntityList) {
                Map(trainingSummaryEntity).onSuccess {
                    mappedList.add(it)
                }
            }

            return mappedList
        }

        fun Map(entity: TrainingSummaryResponseEntity): Result<TrainingSummary> = runCatching {
            TrainingSummary(
                id = entity.id!!,
                creationDateTime = entity.creationDateTime!!,
                students = entity.students!!,
                instructor = entity.instructor!!,
                status = entity.status!!
            )
        }

//        fun Map(entity: UserResponseEntity): User {
//            return User(
//                id = entity.id,
//                email = entity.email,
//                firstName = entity.firstName,
//                lastName = entity.lastName,
//                organization = entity.organization,
//                userType = entity.userType
//            )
//        }
//
//
//        fun MapList(userEntityList: List<UserResponseEntity>): List<User> {
//            return userEntityList.map {
//                User(id = it.id, email = it.email, firstName = it.firstName, lastName = it.lastName, organization = it.organization, userType = it.userType)
//            }
//        }


    }
}