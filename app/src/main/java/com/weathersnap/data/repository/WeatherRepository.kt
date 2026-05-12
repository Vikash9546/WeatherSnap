package com.weathersnap.data.repository

import com.weathersnap.data.api.CityResult
import com.weathersnap.data.api.OpenMeteoApi
import com.weathersnap.data.api.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api: OpenMeteoApi
) {
    suspend fun searchCity(query: String): List<CityResult> = withContext(Dispatchers.IO) {
        api.searchCity(name = query).results ?: emptyList()
    }

    suspend fun getWeather(lat: Double, lon: Double): WeatherResponse = withContext(Dispatchers.IO) {
        api.getWeather(latitude = lat, longitude = lon)
    }
}
