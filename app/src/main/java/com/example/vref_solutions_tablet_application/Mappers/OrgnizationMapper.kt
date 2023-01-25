package com.example.vref_solutions_tablet_application.Mappers

import com.example.vref_solutions_tablet_application.API.ResponseEntities.OrganizationResponseEntity
import com.example.vref_solutions_tablet_application.API.ResponseEntities.TrainingSummaryResponseEntity
import com.example.vref_solutions_tablet_application.Models.Organization
import com.example.vref_solutions_tablet_application.Models.TrainingSummary

class OrgnizationMapper {

    companion object {
        fun MapList(organizationResponseEntityList: List<OrganizationResponseEntity>): List<Organization> {
            val mappedList: MutableList<Organization> = emptyList<Organization>().toMutableList()

            for(organizationEntity in organizationResponseEntityList) {
                Map(organizationEntity).onSuccess {
                    mappedList.add(it)
                }
            }

            return mappedList
        }

        fun Map(entity: OrganizationResponseEntity): Result<Organization> = runCatching {
            Organization(
                id = entity.id!!,
                name = entity.name!!
            )
        }
    }
}