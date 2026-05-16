package com.weathersnap.app.ui.weather

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.weathersnap.app.domain.model.GeocodingResult
import com.weathersnap.app.domain.model.WeatherData
import com.weathersnap.app.ui.theme.AccentColor
import com.weathersnap.app.ui.theme.BackgroundColor
import com.weathersnap.app.ui.theme.CardBorderColor
import com.weathersnap.app.ui.theme.ErrorColor
import com.weathersnap.app.ui.theme.SecondaryTextColor
import com.weathersnap.app.ui.theme.SurfaceColor
import com.weathersnap.app.util.WeatherCodeUtil

@Composable
fun WeatherScreen(
    onNavigateToCreateReport: () -> Unit,
    onNavigateToSavedReports: () -> Unit,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val weatherState by viewModel.weatherState.collectAsState()
    val suggestionsState by viewModel.suggestionsState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedWeather by viewModel.selectedWeather.collectAsState()
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .systemBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WeatherSnap",
                        style = MaterialTheme.typography.headlineMedium,
                        color = AccentColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Live weather at your fingertips",
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryTextColor
                    )
                }
                TextButton(
                    onClick = onNavigateToSavedReports,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Reports",
                        tint = AccentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Reports",
                        color = AccentColor,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Search city...",
                        color = SecondaryTextColor
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = AccentColor
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = SecondaryTextColor
                        )
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentColor,
                    unfocusedBorderColor = CardBorderColor,
                    focusedContainerColor = SurfaceColor,
                    unfocusedContainerColor = SurfaceColor,
                    cursorColor = AccentColor,
                    focusedTextColor = Color(0xFFE8F0D0),
                    unfocusedTextColor = Color(0xFFE8F0D0)
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
            )

            // Helper text
            AnimatedVisibility(
                visible = searchQuery.length <= 2,
                enter = fadeIn(tween(300)),
                exit = fadeOut(tween(200))
            ) {
                Text(
                    text = "Enter more than 2 letters to start city suggestions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryTextColor,
                    modifier = Modifier.padding(start = 4.dp, top = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Animated suggestions dropdown
            AnimatedVisibility(
                visible = suggestionsState !is SuggestionsState.Hidden,
                enter = expandVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(tween(300)),
                exit = shrinkVertically(tween(200)) + fadeOut(tween(200))
            ) {
                SuggestionsDropdown(
                    state = suggestionsState,
                    onSuggestionSelected = {
                        viewModel.onSuggestionSelected(it)
                        focusManager.clearFocus()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weather content area
            AnimatedContent(
                targetState = weatherState,
                transitionSpec = {
                    (fadeIn(tween(400)) + slideInVertically { it / 4 })
                        .togetherWith(fadeOut(tween(200)))
                },
                label = "WeatherContent"
            ) { state ->
                when (state) {
                    is WeatherUiState.Idle -> WeatherIdleState()
                    is WeatherUiState.Loading -> WeatherLoadingState()
                    is WeatherUiState.Success -> WeatherSuccessState(
                        weather = state.weather,
                        onCreateReport = onNavigateToCreateReport
                    )
                    is WeatherUiState.Error -> WeatherErrorState(
                        message = state.message,
                        onRetry = { viewModel.retryWeather() }
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionsDropdown(
    state: SuggestionsState,
    onSuggestionSelected: (GeocodingResult) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        when (state) {
            is SuggestionsState.Loading -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = AccentColor,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Searching...",
                        color = SecondaryTextColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            is SuggestionsState.Loaded -> {
                if (state.suggestions.isEmpty()) {
                    Text(
                        "No cities found",
                        color = SecondaryTextColor,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(state.suggestions) { suggestion ->
                            SuggestionItem(
                                suggestion = suggestion,
                                onClick = { onSuggestionSelected(suggestion) }
                            )
                            if (suggestion != state.suggestions.last()) {
                                HorizontalDivider(color = CardBorderColor.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
            is SuggestionsState.Error -> {
                Text(
                    "Error: ${state.message}",
                    color = ErrorColor,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            else -> {}
        }
    }
}

@Composable
private fun SuggestionItem(
    suggestion: GeocodingResult,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.LocationOn,
            contentDescription = null,
            tint = AccentColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = suggestion.name,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE8F0D0),
                fontWeight = FontWeight.Medium
            )
            val subtitle = buildString {
                suggestion.admin1?.let { append(it) }
                suggestion.country?.let {
                    if (isNotEmpty()) append(", ")
                    append(it)
                }
            }
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryTextColor
                )
            }
        }
    }
}

@Composable
private fun WeatherIdleState() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 48.dp)
        ) {
            Text(
                text = "🌍",
                fontSize = 64.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Search for a city",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFE8F0D0),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Start typing to see suggestions",
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryTextColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WeatherLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = AccentColor,
                modifier = Modifier.size(48.dp),
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Fetching weather...",
                color = SecondaryTextColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun WeatherSuccessState(
    weather: WeatherData,
    onCreateReport: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Main weather card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorderColor, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // City & icon header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = weather.cityName,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFFE8F0D0),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = weather.condition,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AccentColor
                        )
                    }
                    Text(
                        text = WeatherCodeUtil.getWeatherIcon(weather.weatherCode),
                        fontSize = 48.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Temperature
                Text(
                    text = "${weather.temperature}°C",
                    style = MaterialTheme.typography.displaySmall,
                    color = AccentColor,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(color = CardBorderColor.copy(alpha = 0.5f))

                Spacer(modifier = Modifier.height(16.dp))

                // Stats grid
                Row(modifier = Modifier.fillMaxWidth()) {
                    WeatherStatItem(
                        icon = "💧",
                        label = "Humidity",
                        value = "${weather.humidity}%",
                        modifier = Modifier.weight(1f)
                    )
                    WeatherStatItem(
                        icon = "💨",
                        label = "Wind",
                        value = "${weather.windSpeed} km/h",
                        modifier = Modifier.weight(1f)
                    )
                    WeatherStatItem(
                        icon = "🔽",
                        label = "Pressure",
                        value = "${weather.pressure} hPa",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Create Report button
        Button(
            onClick = onCreateReport,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentColor,
                contentColor = BackgroundColor
            )
        ) {
            Text(
                text = "Create Weather Report",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun WeatherStatItem(
    icon: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = Color(0xFFE8F0D0),
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = SecondaryTextColor
        )
    }
}

@Composable
private fun WeatherErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ErrorColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1010))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "⚠️", fontSize = 40.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Failed to load weather",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFE8F0D0)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryTextColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorColor,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Retry", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
