package com.example.vref_solutions_tablet_application.api.responseEntities

import com.example.vref_solutions_tablet_application.models.User

data class LoginResponseEntity (
    val accessToken: String?,
    val tokenType: String?,
    val expiresIn: Long?,
    val user: User?
)
