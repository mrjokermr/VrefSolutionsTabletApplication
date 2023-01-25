package com.example.vref_solutions_tablet_application.API.ResponseEntities

import com.example.vref_solutions_tablet_application.Models.CustomTimestamp

data class TrainingEventResponseEntity(
    val id: Long?,
    val name: String?,
    val symbol: String?,
    val timeStamp: CustomTimestamp?,
    val message: String?,
)
