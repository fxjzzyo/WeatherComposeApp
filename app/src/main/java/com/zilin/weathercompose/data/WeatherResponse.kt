package com.example.kotlinweather2.data

import com.google.gson.annotations.SerializedName
import com.zilin.weathercompose.data.WeatherInfo

data class WeatherResponse(
    @SerializedName("results")
    val weatherInfos: List<WeatherInfo>
)