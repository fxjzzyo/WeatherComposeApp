package com.zilin.weathercompose.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class, UserCity::class, UserBgConfig::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun userCityDao(): UserCityDao

    abstract fun userBgConfigDao(): UserBgConfigDao

    companion object {
    }
}
