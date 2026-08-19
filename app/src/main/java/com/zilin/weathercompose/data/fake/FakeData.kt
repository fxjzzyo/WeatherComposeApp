package com.zilin.weathercompose.data.fake

import com.example.kotlinweather2.data.WeatherNow
import com.example.kotlinweather2.data.daily.WeatherDaily
import com.example.kotlinweather2.data.daily.WeatherDailyInfo
import com.example.kotlinweather2.data.daily.WeatherDailyResponse
import com.zilin.weathercompose.data.Location
import com.zilin.weathercompose.data.WeatherInfo


object FakeData {

    val location = Location(
        country = "中国",
        path = "北京，中国"
    )

    val weatherNow = WeatherNow(
        text = "晴天",
        temperature = "23℃",
        wind_direction = "南风"
    )

    val weatherInfo = WeatherInfo(
        weatherNow = weatherNow,
        location = location
    )

    val dailyWeatherList = listOf<WeatherDaily>(
        WeatherDaily(
            date = "2026.8.17",
            text_day = "晴",
            text_night = "阴",
            wind_direction = "北风",
            wind_scale = "3",
            low = "14",
            high = "25"
        ),
        WeatherDaily(
            date = "2026.8.18",
            text_day = "雨",
            text_night = "晴",
            wind_direction = "西风",
            wind_scale = "5",
            low = "10",
            high = "23"
        ),
        WeatherDaily(
            date = "2026.8.19",
            text_day = "多云",
            text_night = "小雨",
            wind_direction = "东风",
            wind_scale = "4",
            low = "4",
            high = "19"
        ),
        WeatherDaily(
            date = "2026.8.20",
            text_day = "晴",
            text_night = "晴转多云",
            wind_direction = "南风",
            wind_scale = "3",
            low = "18",
            high = "29"
        )
    )

    val weatherDailyInfo = WeatherDailyInfo(
        weatherDaily = dailyWeatherList,
        last_update = "2026.8.17",
        location = location
    )

    val weatherDailyResponse = WeatherDailyResponse(
        weatherDailies = listOf(weatherDailyInfo)
    )

    val cities = listOf(
        "北京", "上海", "广州", "深圳", "天津", "重庆",
        "西安", "成都", "杭州", "南京", "武汉", "郑州",
        "长沙", "济南", "青岛", "沈阳", "大连", "哈尔滨",
        "长春", "石家庄", "太原", "合肥", "福州", "厦门",
        "昆明", "贵阳", "南宁", "海口", "三亚", "兰州",
        "银川", "西宁", "乌鲁木齐", "呼和浩特", "拉萨",
        "苏州", "无锡", "宁波", "温州", "东莞", "佛山",
        "珠海", "惠州", "泉州", "烟台", "潍坊", "唐山"
    )
}