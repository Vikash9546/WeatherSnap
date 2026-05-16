package com.weathersnap.app.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.app.domain.usecase.DeleteWeatherReportUseCase
import com.weathersnap.app.domain.usecase.GetAllWeatherReportsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SavedReportsViewModel @Inject constructor(
    private val getAllWeatherReportsUseCase: GetAllWeatherReportsUseCase,
    private val deleteWeatherReportUseCase: DeleteWeatherReportUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SavedReportsUiState>(SavedReportsUiState.Loading)
    val uiState: StateFlow<SavedReportsUiState> = _uiState.asStateFlow()

    init {
        getAllWeatherReportsUseCase()
            .flowOn(Dispatchers.IO)
            .onEach { reports ->
                _uiState.value = if (reports.isEmpty()) {
                    SavedReportsUiState.Empty
                } else {
                    SavedReportsUiState.Success(reports)
                }
            }
            .catch { e ->
                _uiState.value = SavedReportsUiState.Error(e.message ?: "Unknown error")
            }
            .launchIn(viewModelScope)
    }

    fun deleteReport(report: com.weathersnap.app.data.local.entity.WeatherReportEntity) {
        viewModelScope.launch {
            try {
                deleteWeatherReportUseCase(report)
            } catch (e: Exception) {
                // Could handle delete error if needed
            }
        }
    }
}
