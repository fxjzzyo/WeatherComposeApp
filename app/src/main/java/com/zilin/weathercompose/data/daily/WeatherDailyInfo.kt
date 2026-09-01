package com.example.kotlinweather2.data.daily

import com.google.gson.annotations.SerializedName
import com.zilin.weathercompose.data.Location

data class WeatherDailyInfo(
    @SerializedName("daily")
    val weatherDaily: List<WeatherDaily>,
    val last_update: String = "",
    val location: Location
)
