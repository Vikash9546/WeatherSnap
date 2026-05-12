package com.weathersnap.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApi {
    @GET("https://geocoding-api.open-meteo.com/v1/search")
    suspend fun searchCity(
        @Query("name") name: String,
        @Query("count") count: Int = 5,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json"
    ): GeocodingResponse

    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,surface_pressure,wind_speed_10m,weather_code"
    ): WeatherResponse
}

data class GeocodingResponse(val results: List<CityResult>? = null)
data class CityResult(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val admin1: String?
)

data class WeatherResponse(
    val current: CurrentWeather? = null
)
data class CurrentWeather(
    val temperature_2m: Double,
    val relative_humidity_2m: Double,
    val surface_pressure: Double,
    val wind_speed_10m: Double,
    val weather_code: Int
)
