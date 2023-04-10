package com.example.vref_solutions_tablet_application.models

import com.example.vref_solutions_tablet_application.enums.TrainingStatus

data class Training (
    val id: Long,
    val videos: List<String>,
    val videoAccesURLs: List<String>?,
    val events: List<TrainingEvent>,
    val altitudes: List<Altitude>,
    val students: List<User>,
    val instructor: User,
    val creationDateTime: String,
    val status: TrainingStatus,
)




//class TrainingSummary(
//    val id: Long,
//    val creationDateTime: String,
//    val students: List<User>,
//    val instructor: User,
//    val status: TrainingStatus,
//)