package com.zilin.weathercompose.data.fake

import com.example.kotlinweather2.data.WeatherNow
import com.example.kotlinweather2.data.daily.WeatherDaily
import com.example.kotlinweather2.data.daily.WeatherDailyInfo
import com.example.kotlinweather2.data.daily.WeatherDailyResponse
import com.zilin.weathercompose.data.Location
import com.zilin.weathercompose.data.WeatherInfo
import com.zilin.weathercompose.data.db.SelectCityItem


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



    fun getMockSelectCityList(): List<SelectCityItem> {
        return listOf(
            SelectCityItem("北京", "101010100", "北京市"),
            SelectCityItem("上海", "101020100", "上海市"),
            SelectCityItem("广州", "101280101", "广东省"),
            SelectCityItem("深圳", "101280601", "广东省"),
            SelectCityItem("天津", "101030100", "天津市"),
            SelectCityItem("重庆", "101040100", "重庆市"),
            SelectCityItem("西安", "101110101", "陕西省"),
            SelectCityItem("成都", "101270101", "四川省"),
            SelectCityItem("杭州", "101210101", "浙江省"),
            SelectCityItem("南京", "101190101", "江苏省"),
            SelectCityItem("武汉", "101200101", "湖北省"),
            SelectCityItem("郑州", "101180101", "河南省"),
            SelectCityItem("长沙", "101250101", "湖南省"),
            SelectCityItem("济南", "101120101", "山东省"),
            SelectCityItem("青岛", "101120201", "山东省"),
            SelectCityItem("沈阳", "101070101", "辽宁省"),
            SelectCityItem("大连", "101070201", "辽宁省"),
            SelectCityItem("哈尔滨", "101050101", "黑龙江省"),
            SelectCityItem("长春", "101060101", "吉林省"),
            SelectCityItem("石家庄", "101090101", "河北省"),
            SelectCityItem("太原", "101100101", "山西省"),
            SelectCityItem("合肥", "101220101", "安徽省"),
            SelectCityItem("福州", "101230101", "福建省"),
            SelectCityItem("厦门", "101230201", "福建省"),
            SelectCityItem("昆明", "101290101", "云南省"),
            SelectCityItem("贵阳", "101260101", "贵州省"),
            SelectCityItem("南宁", "101130101", "广西壮族自治区"),
            SelectCityItem("海口", "101310101", "海南省"),
            SelectCityItem("三亚", "101310201", "海南省"),
            SelectCityItem("兰州", "101160101", "甘肃省"),
            SelectCityItem("银川", "101170101", "宁夏回族自治区"),
            SelectCityItem("西宁", "101150101", "青海省"),
            SelectCityItem("乌鲁木齐", "101140101", "新疆维吾尔自治区"),
            SelectCityItem("呼和浩特", "101080101", "内蒙古自治区"),
            SelectCityItem("拉萨", "101120401", "西藏自治区"),
            SelectCityItem("苏州", "101190401", "江苏省"),
            SelectCityItem("无锡", "101190201", "江苏省"),
            SelectCityItem("宁波", "101210401", "浙江省"),
            SelectCityItem("温州", "101210701", "浙江省"),
            SelectCityItem("东莞", "101281601", "广东省"),
            SelectCityItem("佛山", "101280801", "广东省"),
            SelectCityItem("珠海", "101280701", "广东省"),
            SelectCityItem("惠州", "101281301", "广东省"),
            SelectCityItem("泉州", "101230501", "福建省"),
            SelectCityItem("烟台", "101120501", "山东省"),
            SelectCityItem("潍坊", "101120601", "山东省"),
            SelectCityItem("唐山", "101090601", "河北省")
        )
    }


}