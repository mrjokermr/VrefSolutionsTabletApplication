package com.example.vref_solutions_tablet_application.API.RequestBodies

import com.example.vref_solutions_tablet_application.Models.CustomTimestamp

data class EventRequestBody(
    val name: String,
    val symbol: String,
    val timeStamp: CustomTimestamp,
    val message: String,
)
