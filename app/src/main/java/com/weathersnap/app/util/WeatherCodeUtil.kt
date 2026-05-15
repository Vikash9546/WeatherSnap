package com.weathersnap.app.util

object WeatherCodeUtil {

    fun getCondition(code: Int): String = when (code) {
        0 -> "Clear Sky"
        1 -> "Mainly Clear"
        2 -> "Partly Cloudy"
        3 -> "Overcast"
        45 -> "Fog"
        48 -> "Icy Fog"
        51 -> "Light Drizzle"
        53 -> "Moderate Drizzle"
        55 -> "Dense Drizzle"
        61 -> "Slight Rain"
        63 -> "Moderate Rain"
        65 -> "Heavy Rain"
        71 -> "Slight Snow"
        73 -> "Moderate Snow"
        75 -> "Heavy Snow"
        77 -> "Snow Grains"
        80 -> "Slight Showers"
        81 -> "Moderate Showers"
        82 -> "Violent Showers"
        85 -> "Slight Snow Showers"
        86 -> "Heavy Snow Showers"
        95 -> "Thunderstorm"
        96 -> "Thunderstorm with Slight Hail"
        99 -> "Thunderstorm with Heavy Hail"
        else -> "Unknown"
    }

    fun getWeatherIcon(code: Int): String = when (code) {
        0 -> "☀️"
        1, 2 -> "🌤️"
        3 -> "☁️"
        45, 48 -> "🌫️"
        51, 53, 55 -> "🌦️"
        61, 63, 65 -> "🌧️"
        71, 73, 75, 77 -> "❄️"
        80, 81, 82 -> "⛈️"
        85, 86 -> "🌨️"
        95, 96, 99 -> "⛈️"
        else -> "🌡️"
    }
}
