package com.zilin.weathercompose.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zilin.weathercompose.data.Result
import com.zilin.weathercompose.data.WeatherDailyState
import com.zilin.weathercompose.data.WeatherInfo
import com.zilin.weathercompose.data.WeatherState
import com.zilin.weathercompose.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherInfo = MutableStateFlow<WeatherInfo?>(null)
    val weatherInfo: StateFlow<WeatherInfo?> = _weatherInfo

    private val _weatherState = MutableStateFlow(WeatherState())
    val weatherState: StateFlow<WeatherState> = _weatherState

    private val _weatherDailyState = MutableStateFlow(WeatherDailyState())
    val weatherDailyState: StateFlow<WeatherDailyState> = _weatherDailyState

    private val _selectedCityState = MutableStateFlow("北京")
    val selectedCityState: StateFlow<String> = _selectedCityState

    fun selectCity(city: String) {
        viewModelScope.launch {
            _selectedCityState.update {
                city
            }
        }

    }

    fun getWeather(location: String) {
        viewModelScope.launch {
            Log.i("TAG", "getWeather location: ${location}")

            _weatherState.update {
                it.copy(loading = true)
            }
            val result = repository.getWeather2(location)
            when (result) {
                is Result.Success -> {
                    Log.i("TAG", "getWeather result: ${result.data}")
                    _weatherState.update {
                        it.copy(
                            loading = false,
                            weatherInfo = result.data
                        )
                    }
                }

                else -> {
                    _weatherState.update {
                        it.copy(
                            loading = false,
                            error = (result as Result.Error).exception.message
                        )
                    }
                }
            }
        }
    }

    fun getWeatherDaily(location: String) {
        viewModelScope.launch {
            _weatherDailyState.update {
                it.copy(
                    loading = true
                )
            }
            val result = repository.getWeatherDaily(location)
            when (result) {
                is Result.Success -> {
                    _weatherDailyState.update {
                        it.copy(
                            loading = false,
                            weatherDaily = result.data
                        )
                    }
                }

                else -> {
                    _weatherDailyState.update {
                        it.copy(
                            loading = false,
                            error = "get daily weather error"
                        )
                    }
                }
            }
        }
    }

    fun getWeather2(location: String) {
        /*viewModelScope.launch {
            val result = repository.getWeather(location)
            when(result) {
                is Result.Success -> {
                    Log.i("TAG", "getWeather: ${result.data}")
                    _weatherInfo.value = result.data
                }
                is Result.Error -> {

                }
                else -> {

                }
            }
        }*/
    }
}