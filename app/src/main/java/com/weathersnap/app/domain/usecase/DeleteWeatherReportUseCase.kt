package com.weathersnap.app.domain.usecase

import com.weathersnap.app.data.local.entity.WeatherReportEntity
import com.weathersnap.app.data.repository.WeatherReportRepository
import javax.inject.Inject

class DeleteWeatherReportUseCase @Inject constructor(
    private val repository: WeatherReportRepository
) {
    suspend operator fun invoke(report: WeatherReportEntity) {
        repository.deleteReport(report)
    }
}
