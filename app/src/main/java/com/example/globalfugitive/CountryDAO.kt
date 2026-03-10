package com.example.globalfugitive

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(country: Country)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(countries: List<Country>)

    @Update
    suspend fun update(country: Country)

    @Delete
    suspend fun delete(country: Country)

    @Query("SELECT * FROM country_table ORDER BY name ASC")
    fun getAllCountries(): List<Country>

    @Query("SELECT * FROM country_table WHERE name = :name LIMIT 1")
    suspend fun getCountryByName(name: String): Country?

    @Query("SELECT * FROM country_table WHERE uid = :uid LIMIT 1")
    suspend fun getCountryById(uid: Int): Country?

    @Query("DELETE FROM country_table")
    suspend fun deleteAllCountries()

    @Query("SELECT COUNT(*) FROM country_table")
    suspend fun getCountryCount(): Int



}

//val uid: Int = 0,
//    val name: String,
//    val altName1: String,
//    val altName2: String,
//    val region: String,
//    val latitude: Double,
//    val longitude: Double,
//    val flag: String
//)