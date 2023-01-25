package com.example.vref_solutions_tablet_application.`API-Retrofit`

import com.example.vref_solutions_tablet_application.API.APIBaseConfig
import com.example.vref_solutions_tablet_application.API.OrganizationApi
import com.example.vref_solutions_tablet_application.API.TrainingApi
import com.example.vref_solutions_tablet_application.API.UserApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.time.Duration

class RetrofitApiHandler {
    companion object {
        //API essentials
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

        fun GetUsersApi(): UserApi {
            return retrofit.create<UserApi>()
        }

        fun GetTrainingsApi(): TrainingApi {
            return retrofit.create<TrainingApi>()
        }

        fun GetOrganizationsApi(): OrganizationApi {
            return retrofit.create<OrganizationApi>()
        }
    }
}