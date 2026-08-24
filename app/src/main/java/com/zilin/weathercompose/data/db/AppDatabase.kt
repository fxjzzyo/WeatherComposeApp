package com.zilin.weathercompose.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CityEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao

    companion object {
        // 单例，项目中自行初始化（Application）
        @Volatile
        private var INSTANCE: AppDatabase? = null
    }
}
