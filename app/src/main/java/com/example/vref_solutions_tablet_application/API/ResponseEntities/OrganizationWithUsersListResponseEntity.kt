package com.example.vref_solutions_tablet_application.api.responseEntities

import com.example.vref_solutions_tablet_application.models.User

data class OrganizationWithUsersListResponseEntity(
    val id: Long?,
    val name: String?,
    val users: List<User>?
)
