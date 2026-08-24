package com.zilin.weathercompose.data

import com.example.kotlinweather2.data.daily.WeatherDailyInfo

data class City(
    val name: String = ""
)

data class CityCollectState(
    val loading: Boolean = false,
    val cityList: List<City> = mutableListOf(),
    val error: String? = null
)