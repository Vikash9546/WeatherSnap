package com.weathersnap.app.domain.usecase

import com.weathersnap.app.data.repository.GeocodingRepository
import com.weathersnap.app.domain.model.GeocodingResult
import javax.inject.Inject

class SearchCityUseCase @Inject constructor(
    private val repository: GeocodingRepository
) {
    suspend operator fun invoke(query: String): Result<List<GeocodingResult>> {
        if (query.length <= 2) return Result.success(emptyList())
        return repository.searchCity(query)
    }
}
