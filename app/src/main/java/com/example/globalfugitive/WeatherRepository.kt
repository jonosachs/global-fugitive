package com.example.globalfugitive

import android.util.Log

class WeatherRepository {

    private val apiService = RetrofitObject.retrofitService

    suspend fun getResponse(location: String): WeatherData {
        val response = apiService.getWeatherData(location)
        Log.d("WeatherRepository", "Response received: $response")
        return response
    }
}