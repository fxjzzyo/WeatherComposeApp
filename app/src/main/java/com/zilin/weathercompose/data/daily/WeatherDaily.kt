package com.example.kotlinweather2.data.daily

data class WeatherDaily(
    val code_day: String = "",
    val code_night: String = "",
    val date: String = "",
    val high: String = "",
    val humidity: String = "",
    val low: String = "",
    val precip: String = "",
    val rainfall: String = "",
    val text_day: String = "",
    val text_night: String = "",
    val wind_direction: String = "",
    val wind_direction_degree: String = "",
    val wind_scale: String = "",
    val wind_speed: String = ""
)
