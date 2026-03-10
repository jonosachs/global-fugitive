package com.example.globalfugitive

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameViewModelFactory(
    private val countryDao: CountryDao,  // Dependency needed by GameViewModel
    private val application: Application // Application context for AndroidViewModel
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(countryDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}