package com.weathersnap.app.ui.create_report

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.app.data.local.entity.ReportDraftEntity
import com.weathersnap.app.data.local.entity.WeatherReportEntity
import com.weathersnap.app.data.repository.ReportDraftRepository
import com.weathersnap.app.domain.usecase.SaveWeatherReportUseCase
import com.weathersnap.app.domain.model.WeatherData
import com.weathersnap.app.ui.weather.WeatherViewModel
import com.weathersnap.app.util.ImageCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateReportViewModel @Inject constructor(
    private val saveWeatherReportUseCase: SaveWeatherReportUseCase,
    private val draftRepository: ReportDraftRepository,
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateReportUiState>(CreateReportUiState.Idle)
    val uiState: StateFlow<CreateReportUiState> = _uiState.asStateFlow()

    // Draft fields - restored from Room draft on init
    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    private val _imagePath = MutableStateFlow<String?>(null)
    val imagePath: StateFlow<String?> = _imagePath.asStateFlow()

    private val _originalSize = MutableStateFlow<Long?>(null)
    val originalSize: StateFlow<Long?> = _originalSize.asStateFlow()

    private val _compressedSize = MutableStateFlow<Long?>(null)
    val compressedSize: StateFlow<Long?> = _compressedSize.asStateFlow()

    private val _weatherSnapshot = MutableStateFlow<WeatherData?>(null)
    val weatherSnapshot: StateFlow<WeatherData?> = _weatherSnapshot.asStateFlow()

    fun initWithWeather(weather: WeatherData?) {
        if (_weatherSnapshot.value != null) return // Already initialized
        
        viewModelScope.launch {
            val draft = draftRepository.getDraft()
            if (draft != null) {
                if (weather == null || draft.cityName == weather.cityName) {
                    // Restore draft (process death recovery or same city)
                    _weatherSnapshot.value = WeatherData(
                        cityName = draft.cityName,
                        condition = draft.condition,
                        temperature = draft.temperature,
                        humidity = draft.humidity,
                        windSpeed = draft.windSpeed,
                        pressure = draft.pressure,
                        weatherCode = 0
                    )
                    _notes.value = draft.notes
                    _imagePath.value = draft.imagePath
                    _originalSize.value = draft.originalSize
                    _compressedSize.value = draft.compressedSize
                } else {
                    // Different city selected! Discard old draft and its image
                    draft.imagePath?.let { ImageCompressor.deleteSafely(it) }
                    draftRepository.clearDraft()
                    
                    // Initialize with new weather
                    _weatherSnapshot.value = weather
                    persistDraft()
                }
            } else if (weather != null) {
                // No draft, use passed weather
                _weatherSnapshot.value = weather
                persistDraft()
            }
        }
    }

    fun getWeatherSnapshot(): WeatherData? = _weatherSnapshot.value

    fun onNotesChanged(notes: String) {
        _notes.value = notes
        persistDraft()
    }

    fun onCameraResultReceived(
        imagePath: String,
        originalSize: Long,
        compressedSize: Long
    ) {
        // Clean up previous temp image if different
        val old = _imagePath.value
        if (old != null && old != imagePath) {
            ImageCompressor.deleteSafely(old)
        }
        _imagePath.value = imagePath
        _originalSize.value = originalSize
        _compressedSize.value = compressedSize
        persistDraft()
    }

    private fun persistDraft() {
        val weather = _weatherSnapshot.value ?: return
        viewModelScope.launch {
            draftRepository.saveDraft(
                ReportDraftEntity(
                    cityName = weather.cityName,
                    condition = weather.condition,
                    temperature = weather.temperature,
                    humidity = weather.humidity,
                    windSpeed = weather.windSpeed,
                    pressure = weather.pressure,
                    notes = _notes.value,
                    imagePath = _imagePath.value,
                    originalSize = _originalSize.value,
                    compressedSize = _compressedSize.value
                )
            )
        }
    }

    fun saveReport() {
        val weather = _weatherSnapshot.value ?: return
        val path = _imagePath.value ?: return

        viewModelScope.launch {
            _uiState.value = CreateReportUiState.Saving
            try {
                val entity = WeatherReportEntity(
                    cityName = weather.cityName,
                    condition = weather.condition,
                    temperature = weather.temperature,
                    humidity = weather.humidity,
                    windSpeed = weather.windSpeed,
                    pressure = weather.pressure,
                    imagePath = path,
                    originalSize = _originalSize.value ?: 0L,
                    compressedSize = _compressedSize.value ?: 0L,
                    notes = _notes.value,
                    timestamp = System.currentTimeMillis()
                )
                saveWeatherReportUseCase(entity)
                // Clear draft after successful save
                draftRepository.clearDraft()
                _uiState.value = CreateReportUiState.Saved
            } catch (e: Exception) {
                _uiState.value = CreateReportUiState.Error(e.message ?: "Failed to save report")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // If report was NOT saved and we have a draft, keep image for recovery
        // Temp file cleanup happens only when draft is explicitly cleared (after save)
    }
}
