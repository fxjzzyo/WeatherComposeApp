package com.example.kotlinweather2.data.daily

import com.google.gson.annotations.SerializedName

data class WeatherDailyResponse(
    @SerializedName("results")
    val weatherDailies: List<WeatherDailyInfo>
)