package com.example.vref_solutions_tablet_application.API.RequestBodies

data class UserPostRequestBody(
    val email: String,
    val firstName: String,
    val lastName: String,
    val organization: OrganizationUserIdOnlyRequestBody
)
