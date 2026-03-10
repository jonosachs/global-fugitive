package com.example.globalfugitive

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CountryViewModel(application: Application) : AndroidViewModel(application) {

    private val countryDao = CountryDatabase.getDatabase(application).countryDao()

    fun insertCountry(country: Country) {
        viewModelScope.launch {
            countryDao.insert(country)
        }
    }

    fun getAllCountries() = countryDao.getAllCountries()

    fun deleteAllCountries() {
        viewModelScope.launch {
            countryDao.deleteAllCountries()
        }
    }

    suspend fun getCountryByName(name: String) = countryDao.getCountryByName(name)

    suspend fun getCountryById(uid: Int) = countryDao.getCountryById(uid)

    suspend fun getCountryCount() = countryDao.getCountryCount()

}
