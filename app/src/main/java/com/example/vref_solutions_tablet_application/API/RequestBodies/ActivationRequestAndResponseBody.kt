package com.example.vref_solutions_tablet_application.api.requestBodies

data class ActivationRequestBody(
    val activationCode: String,
    val password: String,
)
