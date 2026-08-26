package com.zilin.weathercompose.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // 根据账号密码查询用户（登录校验）
    @Query("SELECT * FROM user WHERE username=:name AND password=:pwd LIMIT 1")
    suspend fun getUser(name: String, pwd: String): User?

    // 账号是否已存在（注册校验）
    @Query("SELECT COUNT(*) FROM user WHERE username=:name")
    suspend fun isUserExist(name: String): Int

    // 新增用户
    @Insert
    suspend fun insertUser(user: User)
}

@Dao
interface UserCityDao {
    // 查询当前用户的收藏城市
    @Query("SELECT * FROM user_city WHERE uid=:uid")
    fun getCityListByUid(uid: Long): Flow<List<UserCity>>

    // 添加收藏城市
    @Insert
    suspend fun insertCity(userCity: UserCity)

    @Insert
    suspend fun addFavoriteList(cities: List<UserCity>)

    // 删除收藏城市
    @Query("DELETE FROM user_city WHERE uid=:uid AND cityName=:city")
    suspend fun deleteCity(uid: Long, city: String)
}
