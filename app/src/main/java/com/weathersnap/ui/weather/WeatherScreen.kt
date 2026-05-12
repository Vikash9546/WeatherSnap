package com.weathersnap.ui.weather

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.weathersnap.ui.theme.*

@Composable
fun WeatherScreen(
    onNavigateToReports: () -> Unit,
    onNavigateToCreateReport: (WeatherUiModel) -> Unit,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        HeaderCard(onNavigateToReports)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SearchSection(
            searchQuery = searchQuery,
            onQueryChange = viewModel::onSearchQueryChanged,
            onSearchClick = viewModel::searchManually,
            suggestions = suggestions,
            onSuggestionSelect = viewModel::selectCity
        )

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedContent(
            targetState = uiState,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "WeatherStateAnimation"
        ) { state ->
            when (state) {
                is WeatherUiState.Idle -> {
                    // Empty space
                }
                is WeatherUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AccentOlive)
                    }
                }
                is WeatherUiState.Success -> {
                    WeatherCard(
                        model = state.data,
                        onCreateReportClick = { onNavigateToCreateReport(state.data) }
                    )
                }
                is WeatherUiState.Empty -> {
                    Text(
                        text = "No results found.",
                        color = TextSecondary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                is WeatherUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = viewModel::searchManually,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentOlive)
                        ) {
                            Text("Retry", color = BackgroundDark)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderCard(onNavigateToReports: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(HeaderGradientTop, HeaderGradientBottom)
                )
            )
            .padding(16.dp)
    ) {
        Column {
            Text("WeatherSnap", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Live weather reports with camera evidence", fontSize = 14.sp, color = TextPrimary)
        }
        
        Button(
            onClick = onNavigateToReports,
            modifier = Modifier.align(Alignment.TopEnd),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = BackgroundDark)
        ) {
            Text("Reports", color = Color.White)
        }
    }
}

@Composable
fun SearchSection(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    suggestions: List<com.weathersnap.data.api.CityResult>,
    onSuggestionSelect: (com.weathersnap.data.api.CityResult) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onQueryChange,
                    label = { Text("City", color = TextSecondary) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentOlive,
                        unfocusedBorderColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onSearchClick,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOlive)
                ) {
                    Text("Search", color = BackgroundDark)
                }
            }
            Text(
                "Enter more than 2 letters to start city suggestions.",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            AnimatedVisibility(
                visible = suggestions.isNotEmpty(),
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .padding(top = 8.dp)
                ) {
                    items(suggestions) { city ->
                        Text(
                            text = "${city.name}${if (city.country != null) ", ${city.country}" else ""}",
                            color = TextPrimary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSuggestionSelect(city) }
                                .padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherCard(model: WeatherUiModel, onCreateReportClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(model.cityName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AccentOlive,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "${model.temperature}°C",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = BackgroundDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Text(model.condition, fontSize = 16.sp, color = TextSecondary, modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                WeatherMetric("Humidity", "${model.humidity}%", HumidityTeal)
                WeatherMetric("Wind", "${model.windSpeed} m/s", WindBlue)
                WeatherMetric("Pressure", "${model.pressure}", PressureAmber)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Report readiness", color = TextSecondary, fontSize = 12.sp)
                Text("Camera and Room DB enabled", color = TextSecondary, fontSize = 12.sp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onCreateReportClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = AccentOlive)
            ) {
                Text("Create Report", color = BackgroundDark)
            }
        }
    }
}

@Composable
fun WeatherMetric(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextSecondary, fontSize = 12.sp)
        Text(value, color = valueColor, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
    }
}
