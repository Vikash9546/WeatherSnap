package com.weathersnap.app.data.mapper

import com.weathersnap.app.data.remote.dto.WeatherResponse
import com.weathersnap.app.domain.model.WeatherData
import com.weathersnap.app.util.WeatherCodeUtil

fun WeatherResponse.toDomain(cityName: String): WeatherData = WeatherData(
    cityName = cityName,
    temperature = current.temperature,
    condition = WeatherCodeUtil.getCondition(current.weatherCode),
    humidity = current.humidity,
    windSpeed = current.windSpeed,
    pressure = current.pressure,
    weatherCode = current.weatherCode
)
