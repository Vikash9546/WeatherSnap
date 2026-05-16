package com.weathersnap.app.domain.usecase

import com.weathersnap.app.data.repository.WeatherRepository
import com.weathersnap.app.domain.model.GeocodingResult
import com.weathersnap.app.domain.model.WeatherData
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(location: GeocodingResult): Result<WeatherData> {
        return repository.getWeather(location)
    }
}
