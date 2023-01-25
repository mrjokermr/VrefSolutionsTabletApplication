package com.example.vref_solutions_tablet_application.API

import com.example.vref_solutions_tablet_application.API.RequestBodies.*
import com.example.vref_solutions_tablet_application.API.ResponseEntities.TrainingEventResponseEntity
import com.example.vref_solutions_tablet_application.API.ResponseEntities.TrainingResponseEntity
import com.example.vref_solutions_tablet_application.API.ResponseEntities.TrainingSummaryResponseEntity
import com.example.vref_solutions_tablet_application.Models.TrainingSummary
import retrofit2.Response
import retrofit2.http.*

interface TrainingApi {


    @Headers("Content-Type: application/json")
    @POST("training")
    suspend fun CreateTraining(
        @Body body: CreateTrainingRequestBody,
        @Header("Authorization") authToken: String,
    ): Response<TrainingResponseEntity>

    @Headers("Content-Type: application/json")
    @POST("training/{TrainingID}/start")
    suspend fun StartTrainingById(
        @Path("TrainingID") trainingId: Long,
        @Body body: CamerasRequestBody,
        @Header("Authorization") authToken: String,
    ): Response<Unit>

    @Headers("Content-Type: application/json")
    @POST("training/{TrainingID}/stop")
    suspend fun StopTrainingById(
        @Path("TrainingID") trainingId: Long,
        @Body body: StopTrainingRequestBody,
        @Header("Authorization") authToken: String,
    ): Response<Unit>

    @Headers("Content-Type: application/json")
    @GET("training")
    suspend fun GetAllTrainingInfoSummaries(
        @Header("Authorization") authToken: String,
    ): Response<List<TrainingSummaryResponseEntity>>

    @Headers("Content-Type: application/json")
    @DELETE("training/{TrainingID}")
    suspend fun DeleteTraining(
        @Path("TrainingID") trainingId: String,
        @Header("Authorization") authToken: String,
    ): Response<Unit>

    @Headers("Content-Type: application/json")
    @POST("training/{TrainingID}/event")
    suspend fun NewTrainingEvent(
        @Path("TrainingID") trainingId: String,
        @Body body: EventRequestBody,
        @Header("Authorization") authToken: String,
    ): Response<TrainingEventResponseEntity>

    @Headers("Content-Type: application/json")
    @PUT("training/{TrainingID}/event/{EventID}")
    suspend fun PutTrainingEvent(
        @Path("TrainingID") trainingId: String,
        @Path("EventID") eventId: String,
        @Body body: EventRequestBody,
        @Header("Authorization") authToken: String,
    ): Response<TrainingEventResponseEntity>

    @Headers("Content-Type: application/json")
    @DELETE("training/{TrainingID}/event/{EventID}")
    suspend fun DeleteTrainingEvent(
        @Path("TrainingID") trainingId: String,
        @Path("EventID") eventId: String,
        @Header("Authorization") authToken: String,
    ): Response<Unit>

    @Headers("Content-Type: application/json")
    @GET("training/{trainingID}/event") //note that trainingID is for the GET and TrainingID is for the POST training event this is a inconsistency from the API
    suspend fun GetTrainingEvents(
        @Header("Authorization") authToken: String,
        @Path("trainingID") trainingId: Long,
    ): Response<List<TrainingEventResponseEntity>>


//    @GET("Articles/{id}")
//    suspend fun getSingleArticle(
//        @Path("id") id: String
//    ): Response<ArticleResultEntity>
}