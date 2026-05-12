package com.weathersnap.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.data.db.WeatherReport
import com.weathersnap.data.repository.ReportRepository
import com.weathersnap.ui.weather.WeatherUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    private val _imagePath = MutableStateFlow<String?>(null)
    val imagePath: StateFlow<String?> = _imagePath.asStateFlow()

    private val _originalSize = MutableStateFlow<Long>(0L)
    val originalSize: StateFlow<Long> = _originalSize.asStateFlow()

    private val _compressedSize = MutableStateFlow<Long>(0L)
    val compressedSize: StateFlow<Long> = _compressedSize.asStateFlow()

    fun updateNotes(newNotes: String) {
        _notes.value = newNotes
    }

    fun setPhotoData(path: String, origSize: Long, compSize: Long) {
        _imagePath.value = path
        _originalSize.value = origSize
        _compressedSize.value = compSize
    }

    fun saveReport(weather: WeatherUiModel, onSuccess: () -> Unit) {
        val path = _imagePath.value ?: return
        viewModelScope.launch {
            val report = WeatherReport(
                cityName = weather.cityName,
                condition = weather.condition,
                temperature = weather.temperature,
                humidity = weather.humidity,
                windSpeed = weather.windSpeed,
                pressure = weather.pressure,
                imagePath = path,
                originalSize = _originalSize.value,
                compressedSize = _compressedSize.value,
                notes = _notes.value
            )
            repository.saveReport(report)
            onSuccess()
        }
    }
}
