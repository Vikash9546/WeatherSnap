package com.weathersnap.app.data.mapper

import com.weathersnap.app.data.remote.dto.GeocodingDto
import com.weathersnap.app.domain.model.GeocodingResult

fun GeocodingDto.toDomain(): GeocodingResult = GeocodingResult(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude,
    country = country,
    admin1 = admin1
)
