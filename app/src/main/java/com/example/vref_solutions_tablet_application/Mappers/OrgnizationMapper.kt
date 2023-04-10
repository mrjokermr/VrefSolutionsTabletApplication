package com.example.vref_solutions_tablet_application.mappers

import com.example.vref_solutions_tablet_application.api.responseEntities.OrganizationResponseEntity
import com.example.vref_solutions_tablet_application.models.Organization

object OrgnizationMapper {

    fun mapList(organizationResponseEntityList: List<OrganizationResponseEntity>): List<Organization> {
        val mappedList: MutableList<Organization> = emptyList<Organization>().toMutableList()

        for(organizationEntity in organizationResponseEntityList) {
            map(organizationEntity).onSuccess {
                mappedList.add(it)
            }
        }

        return mappedList
    }

    fun map(entity: OrganizationResponseEntity): Result<Organization> = runCatching {
        Organization(
            id = entity.id!!,
            name = entity.name!!
        )
    }
}