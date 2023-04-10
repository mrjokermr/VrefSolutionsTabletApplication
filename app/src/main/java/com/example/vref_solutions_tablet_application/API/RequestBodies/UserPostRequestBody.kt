package com.example.vref_solutions_tablet_application.api.requestBodies

data class UserPostRequestBody(
    val email: String,
    val firstName: String,
    val lastName: String,
    val organization: OrganizationUserIdOnlyRequestBody
)
