package com.weathersnap.app.data.mapper

import com.weathersnap.app.data.local.entity.WeatherReportEntity
import com.weathersnap.app.domain.model.WeatherData

fun WeatherReportEntity.toWeatherData(): WeatherData = WeatherData(
    cityName = cityName,
    temperature = temperature,
    condition = condition,
    humidity = humidity,
    windSpeed = windSpeed,
    pressure = pressure,
    weatherCode = 0
)
