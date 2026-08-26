package com.zilin.weathercompose.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserBgConfigDao {
    // 获取用户背景配置
    @Query("SELECT * FROM user_bg_config WHERE uid = :uid LIMIT 1")
    fun getBgConfigByUid(uid: Long): Flow<UserBgConfig?>

    // 更新/插入用户背景
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBgConfig(config: UserBgConfig)
}
