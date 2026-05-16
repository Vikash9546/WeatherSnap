package com.weathersnap.app.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.app.data.repository.GeocodingRepository
import com.weathersnap.app.data.repository.WeatherRepository
import com.weathersnap.app.domain.model.GeocodingResult
import com.weathersnap.app.domain.model.WeatherData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val geocodingRepository: GeocodingRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    private val _suggestionsState = MutableStateFlow<SuggestionsState>(SuggestionsState.Hidden)
    val suggestionsState: StateFlow<SuggestionsState> = _suggestionsState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Immutable weather snapshot for report creation
    private val _selectedWeather = MutableStateFlow<WeatherData?>(null)
    val selectedWeather: StateFlow<WeatherData?> = _selectedWeather.asStateFlow()

    private var weatherJob: Job? = null
    private var isManualSearch = true
    private var lastSelectedLocation: GeocodingResult? = null

    init {
        // Debounce search to avoid rapid API calls
        _searchQuery
            .debounce(400)
            .distinctUntilChanged()
            .filter { isManualSearch && it.length > 2 }
            .onEach { query -> fetchSuggestions(query) }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(query: String) {
        isManualSearch = true
        _searchQuery.value = query
        if (query.isEmpty()) {
            _weatherState.value = WeatherUiState.Idle
            _selectedWeather.value = null
            _suggestionsState.value = SuggestionsState.Hidden
            lastSelectedLocation = null
            weatherJob?.cancel()
        } else if (query.length <= 2) {
            _suggestionsState.value = SuggestionsState.Hidden
        }
    }

    private fun fetchSuggestions(query: String) {
        viewModelScope.launch {
            _suggestionsState.value = SuggestionsState.Loading
            val result = geocodingRepository.searchCity(query)
            result.fold(
                onSuccess = { list ->
                    _suggestionsState.value = if (list.isEmpty()) {
                        SuggestionsState.Loaded(emptyList())
                    } else {
                        SuggestionsState.Loaded(list)
                    }
                },
                onFailure = { e ->
                    _suggestionsState.value = SuggestionsState.Error(
                        e.message ?: "Failed to load suggestions"
                    )
                }
            )
        }
    }

    fun onSuggestionSelected(location: GeocodingResult) {
        isManualSearch = false
        lastSelectedLocation = location
        _suggestionsState.value = SuggestionsState.Hidden
        _searchQuery.value = location.displayName
    }

    private fun fetchWeather(location: GeocodingResult) {
        weatherJob?.cancel()
        weatherJob = viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            val result = weatherRepository.getWeather(location)
            result.fold(
                onSuccess = { weather ->
                    _selectedWeather.value = weather
                    _weatherState.value = WeatherUiState.Success(weather)
                },
                onFailure = { e ->
                    _weatherState.value = WeatherUiState.Error(
                        e.message ?: "Failed to fetch weather"
                    )
                }
            )
        }
    }

    fun retryWeather() {
        val query = _searchQuery.value
        if (query.length > 2) {
            viewModelScope.launch {
                _suggestionsState.value = SuggestionsState.Loading
                val result = geocodingRepository.searchCity(query)
                result.fold(
                    onSuccess = { list ->
                        val first = list.firstOrNull()
                        if (first != null) {
                            onSuggestionSelected(first)
                        } else {
                            _weatherState.value = WeatherUiState.Error("No results found")
                        }
                    },
                    onFailure = { e ->
                        _weatherState.value = WeatherUiState.Error(e.message ?: "Error")
                    }
                )
            }
        }
    }

    fun performSearch() {
        val query = _searchQuery.value.trim()
        if (query.isEmpty()) return

        // If we already have a selected location matching the current query, use it directly
        val cached = lastSelectedLocation
        if (cached != null && cached.displayName == query) {
            fetchWeather(cached)
            return
        }

        viewModelScope.launch {
            _suggestionsState.value = SuggestionsState.Hidden
            _weatherState.value = WeatherUiState.Loading
            
            val result = geocodingRepository.searchCity(query)
            result.fold(
                onSuccess = { list ->
                    val first = list.firstOrNull()
                    if (first != null) {
                        lastSelectedLocation = first
                        _searchQuery.value = first.displayName
                        fetchWeather(first)
                    } else {
                        _weatherState.value = WeatherUiState.Error("City '$query' not found.")
                    }
                },
                onFailure = { e ->
                    _weatherState.value = WeatherUiState.Error("Connection error. Please try again.")
                }
            )
        }
    }

    fun dismissSuggestions() {
        _suggestionsState.value = SuggestionsState.Hidden
    }
}
