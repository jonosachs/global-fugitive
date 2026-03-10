package com.example.globalfugitive

import android.content.Context
import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader

class CountryDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        println("CountryDatabaseCallback called....")
        // Check if the database has been seeded
        val sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val isDatabaseSeeded = sharedPreferences.getBoolean("is_database_seeded", false)

        if (!isDatabaseSeeded) {
            // Seed the database only if it hasn't been seeded yet
            Log.d("CountryDatabase", "Database created, seeding data...")
            seedDatabase(context)

            // Mark the database as seeded in SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putBoolean("is_database_seeded", true)
            editor.apply()
        } else println("Database has been seeded already....")
    }

    private fun seedDatabase(context: Context) {
        val countryDao = CountryDatabase.getDatabase(context).countryDao()

        CoroutineScope(Dispatchers.IO).launch {
            // Seed data from JSON or other source
            val countries = loadCountriesFromJson(context)  // Load data from JSON or hardcoded list
            countryDao.insertAll(countries)  // Insert the country data into the database

        }
    }

    private fun loadCountriesFromJson(context: Context): List<Country> {
        // Parse JSON or other source
        val inputStream = context.assets.open("countries.json")
        val reader = InputStreamReader(inputStream)
        val countryListType = object : TypeToken<List<Country>>() {}.type
        return Gson().fromJson(reader, countryListType)
    }
}



