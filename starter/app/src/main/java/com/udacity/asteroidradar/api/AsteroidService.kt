package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate


private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface AsteroidService{
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("start_date") startDate:String= LocalDate.now().toString(),
        @Query("end_date") endDate:String = LocalDate.now().plusDays(7).toString(),
        @Query("api_key") apiKey:String= API_KEY
    ): String

    @GET("planetary/apod")
    suspend fun getDayImage(
        @Query("api_key") apiKey: String= API_KEY
    ):String
}

object AsteroidApi{
    val retrofitService:AsteroidService by lazy {
        retrofit.create(AsteroidService::class.java)
    }
}

