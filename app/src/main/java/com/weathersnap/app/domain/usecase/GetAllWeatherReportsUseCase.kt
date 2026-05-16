package com.weathersnap.app.domain.usecase

import com.weathersnap.app.data.local.entity.WeatherReportEntity
import com.weathersnap.app.data.repository.WeatherReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllWeatherReportsUseCase @Inject constructor(
    private val repository: WeatherReportRepository
) {
    operator fun invoke(): Flow<List<WeatherReportEntity>> {
        return repository.getAllReports()
    }
}
