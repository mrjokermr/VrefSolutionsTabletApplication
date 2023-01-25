package com.example.vref_solutions_tablet_application.API

import com.example.vref_solutions_tablet_application.API.RequestBodies.OrganizationRequestBody
import com.example.vref_solutions_tablet_application.API.ResponseEntities.CreateOrganizationResponseEntity
import com.example.vref_solutions_tablet_application.API.ResponseEntities.OrganizationResponseEntity
import com.example.vref_solutions_tablet_application.API.ResponseEntities.OrganizationWithUsersListResponseEntity
import com.example.vref_solutions_tablet_application.Models.Organization
import retrofit2.Response
import retrofit2.http.*

interface OrganizationApi {

    @Headers("Content-Type: application/json")
    @GET("organization/{organizationId}")
    suspend fun GetAllOrganisationInfo(
        @Header("Authorization") authToken: String,
        @Path("organizationId") organizationId: Long,
    ): Response<OrganizationWithUsersListResponseEntity>

    @Headers("Content-Type: application/json")
    @GET("organization")
    suspend fun GetOrganisationsList(
        @Header("Authorization") authToken: String,
    ): Response<List<OrganizationResponseEntity>>

    @Headers("Content-Type: application/json")
    @PUT("organization/{organizationId}")
    suspend fun UpdateOrganizationName(
        @Header("Authorization") authToken: String,
        @Body body: OrganizationRequestBody,
        @Path("organizationId") organizationId: Long,
    ): Response<Unit>

    @Headers("Content-Type: application/json")
    @POST("organization")
    suspend fun CreateNewOrganization(
        @Header("Authorization") authToken: String,
        @Body body: OrganizationRequestBody,
    ): Response<CreateOrganizationResponseEntity>
}