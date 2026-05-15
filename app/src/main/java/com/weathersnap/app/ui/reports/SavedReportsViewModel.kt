package com.weathersnap.app.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.app.data.repository.WeatherReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    private val repository: WeatherReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SavedReportsUiState>(SavedReportsUiState.Loading)
    val uiState: StateFlow<SavedReportsUiState> = _uiState.asStateFlow()

    init {
        repository.getAllReports()
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
}
