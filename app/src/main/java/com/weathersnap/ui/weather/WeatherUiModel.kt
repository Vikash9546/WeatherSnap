package com.weathersnap.ui.weather

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherUiModel(
    val cityName: String,
    val condition: String,
    val temperature: Double,
    val humidity: Double,
    val windSpeed: Double,
    val pressure: Double
) : Parcelable
