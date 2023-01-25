package com.example.vref_solutions_tablet_application.API.ResponseEntities

import com.example.vref_solutions_tablet_application.Enums.TrainingStatus
import com.example.vref_solutions_tablet_application.Models.User

data class TrainingSummaryResponseEntity (
    val id: Long?,
    val creationDateTime: String?,
    val students: List<User>?,
    val instructor: User?,
    val status: TrainingStatus?,
)