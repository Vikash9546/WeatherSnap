package com.weathersnap.app.ui.create_report

sealed interface CreateReportUiState {
    object Idle : CreateReportUiState
    object Saving : CreateReportUiState
    object Saved : CreateReportUiState
    data class Error(val message: String) : CreateReportUiState
}
