package com.weathersnap.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_reports")
data class WeatherReport(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cityName: String,
    val condition: String,
    val temperature: Double,
    val humidity: Double,
    val windSpeed: Double,
    val pressure: Double,
    val imagePath: String,
    val originalSize: Long,
    val compressedSize: Long,
    val notes: String,
    val timestamp: Long = System.currentTimeMillis()
)
