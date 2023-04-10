package com.example.vref_solutions_tablet_application.api.requestBodies

import com.example.vref_solutions_tablet_application.models.CustomTimestamp

data class EventRequestBody(
    val name: String,
    val symbol: String,
    val timeStamp: CustomTimestamp,
    val message: String,
)
