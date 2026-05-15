package com.weathersnap.app.ui.reports

import com.weathersnap.app.data.local.entity.WeatherReportEntity

sealed interface SavedReportsUiState {
    object Loading : SavedReportsUiState
    object Empty : SavedReportsUiState
    data class Success(val reports: List<WeatherReportEntity>) : SavedReportsUiState
    data class Error(val message: String) : SavedReportsUiState
}
