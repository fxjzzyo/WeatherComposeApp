package com.zilin.weathercompose.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kotlinweather2.data.daily.WeatherDailyInfo
import com.zilin.weathercompose.data.WeatherInfo
import com.zilin.weathercompose.data.remote.WeatherApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.zilin.weathercompose.data.Result
import java.io.IOException

class WeatherRepository(private val api: WeatherApiService) {

    private val _homeError = MutableLiveData<String?>()
    val homeError: LiveData<String?> = _homeError

    suspend fun getWeather(location: String): WeatherInfo? = withContext(Dispatchers.IO) {
        try {
            val response = api.getWeatherNow(location)
            if (response.isSuccessful) {
                _homeError.postValue(null)
                response.body()?.weatherInfos?.firstOrNull()
            } else {
                _homeError.postValue("error ${response.message()}")
                null
            }
        } catch (e: Exception) {
            _homeError.postValue("Network error: ${e.message}")
            null
        }
    }

    suspend fun getWeather2(location: String): Result<WeatherInfo>? = withContext(Dispatchers.IO) {
        try {
            val response = api.getWeatherNow(location)
            if (response.isSuccessful) {
                _homeError.postValue(null)
                val weatherInfo = response.body()?.weatherInfos?.firstOrNull()
                Result.Success(weatherInfo!!)
            } else {
                _homeError.postValue("error ${response.message()}")
                Result.Error(IOException("get weather error"))
            }
        } catch (e: Exception) {
            _homeError.postValue("Network error: ${e.message}")
            Result.Error(e)
        }
    }

    suspend fun getWeatherDaily(location: String): Result<List<WeatherDailyInfo>> = withContext(Dispatchers.IO) {
        val response = api.getWeatherDaily(location)
        if (response.isSuccessful) {
            val weatherDaily = response.body()?.weatherDailies ?: emptyList()
            Result.Success(weatherDaily)
        } else {
            Result.Error(IOException("get weather daily error"))
        }
    }
}
