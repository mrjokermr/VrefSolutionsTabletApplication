package com.example.vref_solutions_tablet_application.api.responseEntities

import com.example.vref_solutions_tablet_application.models.CustomTimestamp

data class TrainingEventResponseEntity(
    val id: Long?,
    val name: String?,
    val symbol: String?,
    val timeStamp: CustomTimestamp?,
    val message: String?,
)
