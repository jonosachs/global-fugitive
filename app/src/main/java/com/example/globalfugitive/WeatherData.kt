package com.example.globalfugitive


data class WeatherData(
    val location: Location,
    val current: Current
)

data class Location(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tz_id: String,
    val localtime_epoch: Long,
    val localtime: String
)

data class Current(
    val last_updated_epoch: Long,
    val last_updated: String,
    val temp_c: Double,
    val is_day: Int,
    val condition: Condition,
    val wind_kph: Double,
    val wind_dir: String,
    val precip_mm: Double,
    val humidity: Int,
    val cloud: Int,
    val feelslike_c: Double,
    val windchill_c: Double,
    val heatindex_c: Double,
    val dewpoint_c: Double,
    val gust_kph: Double
)

data class Condition(
    val text: String,
    val icon: String,
    val code: Int
)
