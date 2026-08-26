package com.zilin.weathercompose.data.db

// User.kt
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    val username: String,
    val password: String
)



@Entity(
    tableName = "user_city",
    primaryKeys = ["uid", "cityName"] // 同一个用户不能重复存同一个城市
)
data class UserCity(
    val uid: Long,
    val cityName: String,
    val cityCode: String, // 城市编码，请求天气用
    val province: String = ""
)


@Entity(tableName = "user_bg_config")
data class UserBgConfig(
    @PrimaryKey val uid: Long, // 和用户一一对应，一个用户一条背景记录
    val bgResName: String? = null // 选中的背景资源名，null=默认背景
)
