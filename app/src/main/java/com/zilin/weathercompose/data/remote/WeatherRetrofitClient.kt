package com.zilin.weathercompose.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object WeatherRetrofitClient {

    // api 平台地址：https://www.seniverse.com/
    // 注册登录后可以免费申请平台上的私钥 KEY。可以把这个换成你自己的。
    private const val KEY = "SJ6CBTRvvKId2t9bV"
    private const val BASE_URL = "https://api.seniverse.com"

    private const val URL_WEATHER_TODAY = "https://api.seniverse.com/v3/weather/now.json?" +
        "language=zh-Hans&unit=c&key=$KEY"
    private const val URL_WEATHER_WITH_FUTURE = "https://api.seniverse.com/v3/weather/daily.json?" +
        "language=zh-Hans&unit=c&start=0&days=5&key=$KEY"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    val api: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}
