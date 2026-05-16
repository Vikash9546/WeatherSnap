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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Surface
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
            .background(Color(0xFF0D1107))
            .systemBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Header
            WeatherHeader(onNavigateToSavedReports = onNavigateToSavedReports)

            Spacer(modifier = Modifier.height(20.dp))

            // Search section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF12160B))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onQueryChanged(it) },
                            modifier = Modifier.weight(1f),
                            label = { Text("City", color = Color(0xFF6B7264), fontSize = 12.sp) },
                            placeholder = { Text("Search city...", color = Color(0xFF6B7264)) },
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFC2D68C),
                                unfocusedBorderColor = Color(0xFF2A2E25),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                cursorColor = Color(0xFFC2D68C),
                                focusedTextColor = Color(0xFFE8F0D0),
                                unfocusedTextColor = Color(0xFFE8F0D0)
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = {
                                viewModel.performSearch()
                                focusManager.clearFocus()
                            })
                        )
                        Button(
                            onClick = {
                                viewModel.performSearch()
                                focusManager.clearFocus()
                            },
                            modifier = Modifier.height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC2D68C)),
                        ) {
                            Text("Search", color = Color(0xFF1B3008), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                    Text(
                        text = "Enter more than 2 letters to start city suggestions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7264),
                        modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                    )
                }
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
    if (state is SuggestionsState.Hidden) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E211A)),
        border = BorderStroke(1.dp, Color(0xFF2B2F26))
    ) {
        when (state) {
            is SuggestionsState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFFC2D68C), 
                        strokeWidth = 2.dp
                    )
                }
            }
            is SuggestionsState.Loaded -> {
                if (state.suggestions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No cities found", color = Color(0xFF6B7264))
                    }
                } else {
                    Column {
                        state.suggestions.forEachIndexed { index, suggestion ->
                            SuggestionItem(
                                suggestion = suggestion,
                                onClick = { onSuggestionSelected(suggestion) }
                            )
                            if (index < state.suggestions.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    thickness = 1.dp,
                                    color = Color(0xFF2B2F26)
                                )
                            }
                        }
                    }
                }
            }
            is SuggestionsState.Error -> {
                Text(
                    text = state.message,
                    color = Color.Red.copy(alpha = 0.7f),
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(23.dp),
            color = Color.Transparent,
            border = BorderStroke(1.dp, Color(0xFF3E4338))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = suggestion.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFB4B9AE),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun WeatherIdleState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E211A))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Decorative gradient box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF33503B), Color(0xFF1E3D34))
                        ),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Search. Capture. Save.",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFB4B9AE),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "No weather loaded",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFE8F0D0),
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Enter more than 2 letters, choose a city, then search.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6B7264)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E211A)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = weather.cityName,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFE8F0D0),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = weather.condition,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6B7264)
                    )
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFF323B06), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "${weather.temperature}°C",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFFC2D68C),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WeatherStatCard(
                    label = "Humidity",
                    value = "${weather.humidity}%",
                    color = Color(0xFF4DB6AC),
                    modifier = Modifier.weight(1f)
                )
                WeatherStatCard(
                    label = "Wind",
                    value = "${weather.windSpeed} m/s",
                    color = Color(0xFF64B5F6),
                    modifier = Modifier.weight(1f)
                )
                WeatherStatCard(
                    label = "Pressure",
                    value = "${weather.pressure}",
                    color = Color(0xFFFFB74D),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2D3228), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Report readiness",
                        color = Color(0xFF6C7164),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Camera and Room DB enabled",
                        color = Color(0xFFB9BDB0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCreateReport,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC2D68C))
            ) {
                Text("Create Report", color = Color(0xFF1B3008), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun WeatherHeader(onNavigateToSavedReports: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFFD2E69C), Color(0xFF98D1C0))
                    )
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "WeatherSnap",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF1B3008),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Live weather reports with camera evidence",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF1B3008).copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onNavigateToSavedReports,
                modifier = Modifier.height(34.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3008)),
                contentPadding = PaddingValues(horizontal = 16.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Reports", color = Color(0xFFE8F0D0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun WeatherStatCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(68.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = color,
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
