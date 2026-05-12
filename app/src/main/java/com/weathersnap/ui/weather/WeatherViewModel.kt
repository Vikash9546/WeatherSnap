package com.weathersnap.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.data.api.CityResult
import com.weathersnap.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface WeatherUiState {
    object Idle : WeatherUiState
    object Loading : WeatherUiState
    data class Success(val data: WeatherUiModel) : WeatherUiState
    object Empty : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _suggestions = MutableStateFlow<List<CityResult>>(emptyList())
    val suggestions: StateFlow<List<CityResult>> = _suggestions.asStateFlow()

    private val suggestionsCache = mutableMapOf<String, List<CityResult>>()

    init {
        observeSearchQuery()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        _searchQuery
            .debounce(500)
            .onEach { query ->
                if (query.length > 2) {
                    fetchSuggestions(query)
                } else {
                    _suggestions.value = emptyList()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun fetchSuggestions(query: String) {
        if (suggestionsCache.containsKey(query)) {
            _suggestions.value = suggestionsCache[query]!!
            return
        }

        viewModelScope.launch {
            try {
                val results = repository.searchCity(query)
                suggestionsCache[query] = results
                _suggestions.value = results
            } catch (e: Exception) {
                // Ignore suggestion errors or handle gracefully
                _suggestions.value = emptyList()
            }
        }
    }

    fun selectCity(city: CityResult) {
        _searchQuery.value = city.name
        _suggestions.value = emptyList()
        fetchWeather(city.name, city.latitude, city.longitude)
    }

    fun searchManually() {
        val query = _searchQuery.value
        if (query.length > 2) {
            viewModelScope.launch {
                _uiState.value = WeatherUiState.Loading
                try {
                    val results = repository.searchCity(query)
                    if (results.isNotEmpty()) {
                        val city = results.first()
                        fetchWeather(city.name, city.latitude, city.longitude)
                    } else {
                        _uiState.value = WeatherUiState.Empty
                    }
                } catch (e: Exception) {
                    _uiState.value = WeatherUiState.Error("Failed to search city.")
                }
            }
        }
    }

    fun fetchWeather(cityName: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val response = repository.getWeather(lat, lon)
                if (response.current != null) {
                    val condition = mapWeatherCodeToCondition(response.current.weather_code)
                    val model = WeatherUiModel(
                        cityName = cityName,
                        condition = condition,
                        temperature = response.current.temperature_2m,
                        humidity = response.current.relative_humidity_2m,
                        windSpeed = response.current.wind_speed_10m,
                        pressure = response.current.surface_pressure
                    )
                    _uiState.value = WeatherUiState.Success(model)
                } else {
                    _uiState.value = WeatherUiState.Error("Weather data unavailable.")
                }
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error("Failed to fetch weather: ${e.message}")
            }
        }
    }

    private fun mapWeatherCodeToCondition(code: Int): String {
        return when (code) {
            0 -> "Clear sky"
            1, 2, 3 -> "Mainly clear, partly cloudy, and overcast"
            45, 48 -> "Fog and depositing rime fog"
            51, 53, 55 -> "Drizzle: Light, moderate, and dense intensity"
            56, 57 -> "Freezing Drizzle: Light and dense intensity"
            61, 63, 65 -> "Rain: Slight, moderate and heavy intensity"
            66, 67 -> "Freezing Rain: Light and heavy intensity"
            71, 73, 75 -> "Snow fall: Slight, moderate, and heavy intensity"
            77 -> "Snow grains"
            80, 81, 82 -> "Rain showers: Slight, moderate, and violent"
            85, 86 -> "Snow showers slight and heavy"
            95 -> "Thunderstorm: Slight or moderate"
            96, 99 -> "Thunderstorm with slight and heavy hail"
            else -> "Unknown condition"
        }
    }
}
