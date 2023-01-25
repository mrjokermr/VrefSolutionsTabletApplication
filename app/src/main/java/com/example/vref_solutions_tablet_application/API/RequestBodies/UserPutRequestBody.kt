package com.example.vref_solutions_tablet_application.API.RequestBodies

import com.example.vref_solutions_tablet_application.Enums.UserType

data class UserPutRequestBody(
    val email: String,
    val firstName: String,
    val lastName: String,
    val userType: UserType,
    val organization: OrganizationUserIdOnlyRequestBody
)