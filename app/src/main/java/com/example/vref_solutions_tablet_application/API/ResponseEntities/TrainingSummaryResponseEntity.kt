package com.example.vref_solutions_tablet_application.api.responseEntities

import com.example.vref_solutions_tablet_application.enums.TrainingStatus
import com.example.vref_solutions_tablet_application.models.User

data class TrainingSummaryResponseEntity (
    val id: Long?,
    val creationDateTime: String?,
    val students: List<User>?,
    val instructor: User?,
    val status: TrainingStatus?,
)