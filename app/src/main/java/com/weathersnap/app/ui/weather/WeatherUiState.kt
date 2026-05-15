package com.weathersnap.app.ui.weather

import com.weathersnap.app.domain.model.GeocodingResult
import com.weathersnap.app.domain.model.WeatherData

sealed interface WeatherUiState {
    object Idle : WeatherUiState
    object Loading : WeatherUiState
    data class Success(val weather: WeatherData) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

sealed interface SuggestionsState {
    object Hidden : SuggestionsState
    object Loading : SuggestionsState
    data class Loaded(val suggestions: List<GeocodingResult>) : SuggestionsState
    data class Error(val message: String) : SuggestionsState
}
