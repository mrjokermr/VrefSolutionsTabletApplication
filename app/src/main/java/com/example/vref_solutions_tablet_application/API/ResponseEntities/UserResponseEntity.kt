package com.example.vref_solutions_tablet_application.API.ResponseEntities

import com.example.vref_solutions_tablet_application.Enums.UserType
import com.example.vref_solutions_tablet_application.Models.Organization

data class UserResponseEntity(
    val id: Long?,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val organization: Organization?,
    val userType: UserType?
)
