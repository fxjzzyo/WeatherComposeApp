package com.zilin.weathercompose.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    // 获取全部收藏城市 Flow，数据库变化自动更新UI
    @Query("SELECT * FROM favorite_city")
    fun getAllFavoriteCity(): Flow<List<CityEntity>>

    // 添加收藏
    @Insert
    suspend fun addFavorite(city: CityEntity)

    //批量插入
    @Insert
    suspend fun addFavoriteList(cities: List<CityEntity>)

    // 删除收藏
    @Delete
    suspend fun deleteFavorite(city: CityEntity)

    // 判断城市是否已经收藏
    @Query("SELECT COUNT(*) FROM favorite_city WHERE cityName = :city")
    suspend fun isCityCollected(city: String): Int
}
