package com.zilin.weathercompose.data.remote

import com.example.kotlinweather2.data.WeatherResponse
import com.example.kotlinweather2.data.daily.WeatherDailyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {


    /**
     * 获取当天的天气
     * url: https://api.seniverse.com/v3/weather/now.json?key=SJ6CBTRvvKId2t9bV&location=beijing&language=zh-Hans&unit=c
     */
    @GET("/v3/weather/now.json?language=zh-Hans&unit=c&key=SJ6CBTRvvKId2t9bV")
    suspend fun getWeatherNow(@Query("location") location: String): Response<WeatherResponse>


    /**
     * 获取未来的天气
     * url: https://api.seniverse.com/v3/weather/daily.json?key=SJ6CBTRvvKId2t9bV&location=beijing&language=zh-Hans&unit=c&start=0&days=5
     */
    @GET("/v3/weather/daily.json?language=zh-Hans&unit=c&key=SJ6CBTRvvKId2t9bV&start=0&days=5")
    suspend fun getWeatherDaily(@Query("location") location: String): Response<WeatherDailyResponse>


}