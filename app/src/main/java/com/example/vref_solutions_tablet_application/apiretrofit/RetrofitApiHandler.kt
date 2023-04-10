package com.example.vref_solutions_tablet_application.apiretrofit

import com.example.vref_solutions_tablet_application.api.APIBaseConfig
import com.example.vref_solutions_tablet_application.api.OrganizationApi
import com.example.vref_solutions_tablet_application.api.TrainingApi
import com.example.vref_solutions_tablet_application.api.UserApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.time.Duration

object RetrofitApiHandler {
    private val client = OkHttpClient.Builder().callTimeout(duration = Duration.ofMinutes(1)).connectTimeout(
        Duration.ofMinutes(1))
        .writeTimeout(Duration.ofMinutes(1))
        .readTimeout(Duration.ofMinutes(1))
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(APIBaseConfig.base_url)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun getUsersApi(): UserApi {
        return retrofit.create<UserApi>()
    }

    fun getTrainingsApi(): TrainingApi {
        return retrofit.create<TrainingApi>()
    }

    fun getOrganizationsApi(): OrganizationApi {
        return retrofit.create<OrganizationApi>()
    }
}