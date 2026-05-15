package com.weathersnap.app.data.repository

import com.weathersnap.app.data.mapper.toDomain
import com.weathersnap.app.data.remote.api.GeocodingApiService
import com.weathersnap.app.domain.model.GeocodingResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeocodingRepository @Inject constructor(
    private val apiService: GeocodingApiService
) {
    // In-memory cache keyed by query (lowercase, trimmed)
    private val cache = mutableMapOf<String, List<GeocodingResult>>()

    suspend fun searchCity(query: String): Result<List<GeocodingResult>> {
        val key = query.lowercase().trim()
        cache[key]?.let { return Result.success(it) }

        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchCity(query)
                val results = response.results?.map { it.toDomain() } ?: emptyList()
                cache[key] = results
                Result.success(results)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
