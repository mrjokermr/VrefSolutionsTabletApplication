package com.example.vref_solutions_tablet_application.API.ResponseEntities

import com.example.vref_solutions_tablet_application.Models.User

data class OrganizationWithUsersListResponseEntity(
    val id: Long?,
    val name: String?,
    val users: List<User>?
)
