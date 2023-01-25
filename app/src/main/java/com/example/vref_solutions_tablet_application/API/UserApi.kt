package com.example.vref_solutions_tablet_application.API

import com.example.vref_solutions_tablet_application.API.RequestBodies.ActivationRequestBody
import com.example.vref_solutions_tablet_application.API.RequestBodies.LoginRequestBody
import com.example.vref_solutions_tablet_application.API.RequestBodies.UserPostRequestBody
import com.example.vref_solutions_tablet_application.API.RequestBodies.UserPutRequestBody
import com.example.vref_solutions_tablet_application.API.ResponseEntities.LoginResponseEntity
import com.example.vref_solutions_tablet_application.API.ResponseEntities.UserResponseEntity
import retrofit2.Response
import retrofit2.http.*


interface UserApi {

    @Headers("Content-Type: application/json")
    @POST("user/login")
    suspend fun login(@Body body: LoginRequestBody): Response<LoginResponseEntity>

//    @Headers("Content-Type: application/json")
//    @GET("user/{id}")
//    suspend fun getUserById(
//        @Path("id") id: String,
//        @Header("Bearer") authToken: String
//    ): Response<UserResponseEntity>

    @Headers("Content-Type: application/json")
    @GET("user")
    suspend fun getAllUsers(
        @Header("Authorization") authToken: String
    ): Response<List<UserResponseEntity>>

    @Headers("Content-Type: application/json")
    @GET("user")
    suspend fun getAllUsersBySearchfieldMatch(
        @Header("Authorization") authToken: String,
        @Query("searchField") searchField: String,
    ): Response<List<UserResponseEntity>>

    @Headers("Content-Type: application/json")
    @PUT("user/{userId}")
    suspend fun putUserByRequestBody(
        @Header("Authorization") authToken: String,
        @Body body: UserPutRequestBody,
        @Path("userId") userId: Long,
    ): Response<UserResponseEntity>

    @Headers("Content-Type: application/json")
    @POST("user")
    suspend fun postUserByRequestBody(
        @Header("Authorization") authToken: String,
        @Body body: UserPostRequestBody,
    ): Response<UserResponseEntity>

    @Headers("Content-Type: application/json")
    @PUT("user/activate")
    suspend fun activateUserByRequestBody(
        @Body body: ActivationRequestBody,
    ): Response<UserResponseEntity>

    @Headers("Content-Type: application/json")
    @GET("user/{userId}")
    suspend fun getUserById(
        @Header("Authorization") authToken: String,
        @Path("userId") userId: Long,
    ): Response<UserResponseEntity>

    @Headers("Content-Type: application/json")
    @DELETE("user/{userId}")
    suspend fun deleteUserById(
        @Header("Authorization") authToken: String,
        @Path("userId") userId: Long,
    ): Response<Unit>

}