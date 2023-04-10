package com.example.vref_solutions_tablet_application.api

import com.example.vref_solutions_tablet_application.api.requestBodies.OrganizationRequestBody
import com.example.vref_solutions_tablet_application.api.responseEntities.CreateOrganizationResponseEntity
import com.example.vref_solutions_tablet_application.api.responseEntities.OrganizationResponseEntity
import com.example.vref_solutions_tablet_application.api.responseEntities.OrganizationWithUsersListResponseEntity
import retrofit2.Response
import retrofit2.http.*

interface OrganizationApi {

    @Headers("Content-Type: application/json")
    @GET("organization/{organizationId}")
    suspend fun getAllOrganisationInfo(
        @Header("Authorization") authToken: String,
        @Path("organizationId") organizationId: Long,
    ): Response<OrganizationWithUsersListResponseEntity>

    @Headers("Content-Type: application/json")
    @GET("organization")
    suspend fun getOrganisationsList(
        @Header("Authorization") authToken: String,
    ): Response<List<OrganizationResponseEntity>>

    @Headers("Content-Type: application/json")
    @PUT("organization/{organizationId}")
    suspend fun updateOrganizationName(
        @Header("Authorization") authToken: String,
        @Body body: OrganizationRequestBody,
        @Path("organizationId") organizationId: Long,
    ): Response<Unit>

    @Headers("Content-Type: application/json")
    @POST("organization")
    suspend fun createNewOrganization(
        @Header("Authorization") authToken: String,
        @Body body: OrganizationRequestBody,
    ): Response<CreateOrganizationResponseEntity>
}