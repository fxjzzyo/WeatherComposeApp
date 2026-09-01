package com.zilin.weathercompose.data

import com.example.kotlinweather2.data.daily.WeatherDailyInfo

data class WeatherState(val loading: Boolean = false, val weatherInfo: WeatherInfo? = null, val error: String? = null)

data class WeatherDailyState(
    val loading: Boolean = false,
    val weatherDaily: List<WeatherDailyInfo> = mutableListOf(),
    val error: String? = null
)
