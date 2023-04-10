package com.example.vref_solutions_tablet_application.api.responseEntities

import com.example.vref_solutions_tablet_application.enums.TrainingStatus
import com.example.vref_solutions_tablet_application.models.Altitude
import com.example.vref_solutions_tablet_application.models.TrainingEvent
import com.example.vref_solutions_tablet_application.models.User

data class TrainingResponseEntity(
    val id: Long?,
    val videos: List<String>?,
    val videoAccesURLs: List<String>?,
    val events: List<TrainingEvent>?,
    val altitudes: List<Altitude>?,
    val students: List<User>?,
    val instructor: User?,
    val creationDateTime: String?,
    val status: TrainingStatus?,
)
