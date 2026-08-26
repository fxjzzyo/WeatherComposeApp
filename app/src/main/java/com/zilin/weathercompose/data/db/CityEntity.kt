package com.zilin.weathercompose.data.db


data class CityEntity(
    val cityName: String, // 城市名
    val cityCode: String, // 城市编码，请求天气用
    val province: String = ""
)

// 弹窗选择的候选城市数据
data class SelectCityItem(
    val cityName: String,
    val cityCode: String,
    val province: String
)