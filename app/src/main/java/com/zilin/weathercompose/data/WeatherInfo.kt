package com.zilin.weathercompose.data

import com.example.kotlinweather2.data.WeatherNow
import com.google.gson.annotations.SerializedName

data class WeatherInfo(
    @SerializedName("last_update")
    val lastUpdate: String = "",
    val location: Location,
    @SerializedName("now")
    val weatherNow: WeatherNow
)
