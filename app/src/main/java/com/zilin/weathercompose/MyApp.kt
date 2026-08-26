package com.zilin.weathercompose

import android.app.Application
import androidx.room.Room
import com.zilin.weathercompose.data.db.AppDatabase
import kotlin.jvm.java

class MyApp : Application() {
    val db by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "weather_db"
        ).build()
    }

    override fun onCreate() {
        super.onCreate()
    }
}
