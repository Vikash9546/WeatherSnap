package com.weathersnap.app.domain.model

data class GeocodingResult(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val admin1: String?
) {
    val displayName: String
        get() = buildString {
            append(name)
            admin1?.let { append(", $it") }
            country?.let { append(", $it") }
        }
}
