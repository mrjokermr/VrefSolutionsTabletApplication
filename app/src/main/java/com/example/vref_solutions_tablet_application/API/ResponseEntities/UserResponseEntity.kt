package com.example.vref_solutions_tablet_application.api.responseEntities

import com.example.vref_solutions_tablet_application.enums.UserType
import com.example.vref_solutions_tablet_application.models.Organization

data class UserResponseEntity(
    val id: Long?,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val organization: Organization?,
    val userType: UserType?
)
