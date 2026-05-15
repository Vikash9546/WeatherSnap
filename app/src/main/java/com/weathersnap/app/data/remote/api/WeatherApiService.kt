package com.weathersnap.app.data.remote.api

import com.weathersnap.app.data.remote.dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,wind_speed_10m,surface_pressure,weather_code",
        @Query("wind_speed_unit") windSpeedUnit: String = "kmh",
        @Query("forecast_days") forecastDays: Int = 1
    ): WeatherResponse
}
