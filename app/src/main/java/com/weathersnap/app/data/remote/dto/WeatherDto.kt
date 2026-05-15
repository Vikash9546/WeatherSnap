package com.weathersnap.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("current") val current: CurrentWeatherDto,
    @SerializedName("current_units") val currentUnits: CurrentUnitsDto?
)

data class CurrentWeatherDto(
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("relative_humidity_2m") val humidity: Int,
    @SerializedName("wind_speed_10m") val windSpeed: Double,
    @SerializedName("surface_pressure") val pressure: Double,
    @SerializedName("weather_code") val weatherCode: Int
)

data class CurrentUnitsDto(
    @SerializedName("temperature_2m") val temperatureUnit: String?,
    @SerializedName("wind_speed_10m") val windSpeedUnit: String?
)
