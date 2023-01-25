package com.example.vref_solutions_tablet_application.API.ResponseEntities

import com.example.vref_solutions_tablet_application.Enums.TrainingStatus
import com.example.vref_solutions_tablet_application.Models.Altitude
import com.example.vref_solutions_tablet_application.Models.TrainingEvent
import com.example.vref_solutions_tablet_application.Models.User

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
