package com.example.vref_solutions_tablet_application.api

import com.example.vref_solutions_tablet_application.api.requestBodies.*
import com.example.vref_solutions_tablet_application.api.responseEntities.TrainingEventResponseEntity
import com.example.vref_solutions_tablet_application.api.responseEntities.TrainingResponseEntity
import com.example.vref_solutions_tablet_application.api.responseEntities.TrainingSummaryResponseEntity
import retrofit2.Response
import retrofit2.http.*

interface TrainingApi {


    @Headers("Content-Type: application/json")
    @POST("training")
    suspend fun createTraining(
        @Body body: CreateTrainingRequestBody,
        @Header("Authorization") authToken: String,
    ): Response<TrainingResponseEntity>

    @Headers("Content-Type: application/json")
    @POST("training/{TrainingID}/start")
    suspend fun startTrainingById(
        @Path("TrainingID") trainingId: Long,
        @Body body: CamerasRequestBody,
        @Header("Authorization") authToken: String,
    ): Response<Unit>

    @Headers("Content-Type: application/json")
    @POST("training/{TrainingID}/stop")
    suspend fun stopTrainingById(
        @Path("TrainingID") trainingId: Long,
        @Body body: StopTrainingRequestBody,
        @Header("Authorization") authToken: String,
    ): Response<Unit>

    @Headers("Content-Type: application/json")
    @GET("training")
    suspend fun getAllTrainingInfoSummaries(
        @Header("Authorization") authToken: String,
    ): Response<List<TrainingSummaryResponseEntity>>

    @Headers("Content-Type: application/json")
    @DELETE("training/{TrainingID}")
    suspend fun deleteTraining(
        @Path("TrainingID") trainingId: String,
        @Header("Authorization") authToken: String,
    ): Response<Unit>

    @Headers("Content-Type: application/json")
    @POST("training/{TrainingID}/event")
    suspend fun newTrainingEvent(
        @Path("TrainingID") trainingId: String,
        @Body body: EventRequestBody,
        @Header("Authorization") authToken: String,
    ): Response<TrainingEventResponseEntity>

    @Headers("Content-Type: application/json")
    @PUT("training/{TrainingID}/event/{EventID}")
    suspend fun putTrainingEvent(
        @Path("TrainingID") trainingId: String,
        @Path("EventID") eventId: String,
        @Body body: EventRequestBody,
        @Header("Authorization") authToken: String,
    ): Response<TrainingEventResponseEntity>

    @Headers("Content-Type: application/json")
    @DELETE("training/{TrainingID}/event/{EventID}")
    suspend fun deleteTrainingEvent(
        @Path("TrainingID") trainingId: String,
        @Path("EventID") eventId: String,
        @Header("Authorization") authToken: String,
    ): Response<Unit>

    @Headers("Content-Type: application/json")
    @GET("training/{trainingID}/event") //note that trainingID is for the GET and TrainingID is for the POST training event this is a inconsistency from the API
    suspend fun getTrainingEvents(
        @Header("Authorization") authToken: String,
        @Path("trainingID") trainingId: Long,
    ): Response<List<TrainingEventResponseEntity>>


//    @GET("Articles/{id}")
//    suspend fun getSingleArticle(
//        @Path("id") id: String
//    ): Response<ArticleResultEntity>
}