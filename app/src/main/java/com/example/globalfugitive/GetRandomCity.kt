package com.example.globalfugitive

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream

fun getRandomCity (context: Context): String {
    // Read the JSON file from res/raw
    val inputStream: InputStream = context.resources.openRawResource(R.raw.cities) // Replace with your file name
    val json = inputStream.bufferedReader().use { it.readText() }

    // Parse the JSON file
    val gson = Gson()
    val cityListType = object : TypeToken<List<CityData>>() {}.type
    val cities: List<CityData> = gson.fromJson(json, cityListType)

    // Return a random city name
    return cities.random().name
}