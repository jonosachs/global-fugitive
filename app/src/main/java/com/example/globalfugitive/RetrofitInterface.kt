package com.example.globalfugitive

import com.example.globalfugitive.BuildConfig.WEATHER_API_KEY
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitInterface {

        @GET("current.json")
        suspend fun getWeatherData(
            @Query("q") location: String,
            @Query("key") apiKey: String = WEATHER_API_KEY,
            @Query("aqi") aqi: String = "no"
        ): WeatherData

}