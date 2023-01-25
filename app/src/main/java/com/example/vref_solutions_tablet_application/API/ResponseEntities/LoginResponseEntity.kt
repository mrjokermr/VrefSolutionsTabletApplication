package com.example.vref_solutions_tablet_application.API.ResponseEntities

import com.example.vref_solutions_tablet_application.Models.User

data class LoginResponseEntity (
    val accessToken: String?,
    val tokenType: String?,
    val expiresIn: Long?,
    val user: User?
)
