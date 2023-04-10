package com.example.vref_solutions_tablet_application.models

import com.example.vref_solutions_tablet_application.enums.TrainingStatus

data class SavableTrainingInfo(
    val id: Long, //
    val creationDateTime: String, //
    val students: List<User>, //firstStudentId, GetSecondStudentId
    val instructor: User,
    val status: TrainingStatus
)
