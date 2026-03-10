package com.example.globalfugitive

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RetrofitViewModel: ViewModel() {

    private val repository = WeatherRepository()
    val retrofitResponse: MutableState<WeatherData?> = mutableStateOf(null)

    fun getResponse(location: String) {
        viewModelScope.launch {
            try {
                Log.d("RetrofitViewModel", "Fetching weather data for: $location")
                val responseReturned = repository.getResponse(location)
                retrofitResponse.value = responseReturned
                println("Got response $responseReturned")
            } catch (e: HttpException) {
                Log.e("Error", "HTTP exception: ${e.code()}")
            } catch (e: IOException) {
                Log.e("Error", "Network error: ${e.localizedMessage}")
            } catch (e: Exception) {
                Log.e("Error", "Unknown error: ${e.localizedMessage}")
            }
        }
    }

}