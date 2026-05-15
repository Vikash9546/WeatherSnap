package com.weathersnap.app.data.repository

import com.weathersnap.app.data.mapper.toDomain
import com.weathersnap.app.data.remote.api.WeatherApiService
import com.weathersnap.app.domain.model.GeocodingResult
import com.weathersnap.app.domain.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService
) {
    suspend fun getWeather(location: GeocodingResult): Result<WeatherData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getWeather(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
                Result.success(response.toDomain(location.name))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
