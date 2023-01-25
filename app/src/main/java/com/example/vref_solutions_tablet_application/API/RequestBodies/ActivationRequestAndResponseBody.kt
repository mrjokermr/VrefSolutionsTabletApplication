package com.example.vref_solutions_tablet_application.API.RequestBodies

data class ActivationRequestBody(
    val activationCode: String,
    val password: String,
)
