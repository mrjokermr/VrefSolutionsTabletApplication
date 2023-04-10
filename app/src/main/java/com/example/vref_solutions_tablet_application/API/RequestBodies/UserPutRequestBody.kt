package com.example.vref_solutions_tablet_application.api.requestBodies

import com.example.vref_solutions_tablet_application.enums.UserType

data class UserPutRequestBody(
    val email: String,
    val firstName: String,
    val lastName: String,
    val userType: UserType,
    val organization: OrganizationUserIdOnlyRequestBody
)