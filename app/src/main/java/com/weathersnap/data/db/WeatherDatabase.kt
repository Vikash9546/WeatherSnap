package com.weathersnap.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WeatherReport::class], version = 1, exportSchema = true)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}
