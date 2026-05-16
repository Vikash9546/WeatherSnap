package com.weathersnap.app.ui.create_report

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.weathersnap.app.domain.model.WeatherData
import com.weathersnap.app.ui.theme.AccentColor
import com.weathersnap.app.ui.theme.BackgroundColor
import com.weathersnap.app.ui.theme.CardBorderColor
import com.weathersnap.app.ui.theme.ErrorColor
import com.weathersnap.app.ui.theme.SecondaryTextColor
import com.weathersnap.app.ui.theme.SurfaceColor
import com.weathersnap.app.util.WeatherCodeUtil
import java.io.File

@Composable
fun CreateReportScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CreateReportViewModel = hiltViewModel(),
    weatherViewModel: com.weathersnap.app.ui.weather.WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val imagePath by viewModel.imagePath.collectAsStateWithLifecycle()
    val originalSize by viewModel.originalSize.collectAsStateWithLifecycle()
    val compressedSize by viewModel.compressedSize.collectAsStateWithLifecycle()
    val selectedWeather by weatherViewModel.selectedWeather.collectAsStateWithLifecycle()

    // Initialize ViewModel with weather snapshot
    LaunchedEffect(selectedWeather) {
        selectedWeather?.let { viewModel.initWithWeather(it) }
    }

    // Navigate after save
    LaunchedEffect(uiState) {
        if (uiState is CreateReportUiState.Saved) {
            onNavigateToReports()
        }
    }

    val weather = viewModel.getWeatherSnapshot() ?: selectedWeather

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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFB4C88E), Color(0xFFC5D9A5))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Create Report",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFF1B2A1D),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Capture, compress, annotate",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF1B2A1D).copy(alpha = 0.7f)
                            )
                        }
                        
                        Button(
                            onClick = onNavigateBack,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D332F)),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text("Back", color = Color(0xFFE8F0D0), fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weather snapshot (immutable)
            weather?.let { w ->
                WeatherSnapshotCard(weather = w)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Image preview / capture
            AnimatedContent(
                targetState = imagePath,
                transitionSpec = {
                    (fadeIn(tween(400)) + scaleIn(tween(400), initialScale = 0.9f))
                        .togetherWith(fadeOut(tween(200)) + scaleOut(tween(200), targetScale = 0.9f))
                },
                label = "ImagePreview"
            ) { path ->
                if (path != null) {
                    ImagePreviewCard(
                        imagePath = path,
                        originalSize = originalSize,
                        compressedSize = compressedSize,
                        onRetake = onNavigateToCamera
                    )
                } else {
                    ImagePlaceholderCard(onCapture = onNavigateToCamera)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notes section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D3228))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Field Notes",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFFE8F0D0),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { viewModel.onNotesChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Notes", color = Color(0xFF6B7264)) },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFC2D68C),
                            unfocusedBorderColor = Color(0xFF3E4338),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = Color(0xFFC2D68C),
                            focusedTextColor = Color(0xFFE8F0D0),
                            unfocusedTextColor = Color(0xFFE8F0D0)
                        ),
                        minLines = 4,
                        maxLines = 6
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Save button
            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    fadeIn(tween(300)).togetherWith(fadeOut(tween(200)))
                },
                label = "SaveButton"
            ) { state ->
                when (state) {
                    is CreateReportUiState.Saving -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = AccentColor, modifier = Modifier.size(32.dp))
                        }
                    }
                    is CreateReportUiState.Error -> {
                        Column {
                            Text(
                                text = state.message,
                                color = ErrorColor,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            SaveButton(enabled = imagePath != null, onClick = { viewModel.saveReport() })
                        }
                    }
                    else -> {
                        SaveButton(
                            enabled = imagePath != null,
                            onClick = { viewModel.saveReport() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun WeatherSnapshotCard(weather: WeatherData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D3228))
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
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFE8F0D0),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = weather.condition,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB4B9AE)
                    )
                }
                Text(
                    text = "${weather.temperature}°C",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFC2D68C),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBadge("Humidity", "${weather.humidity}%", Color(0xFF4DB6AC).copy(alpha = 0.1f), Color(0xFF4DB6AC), Modifier.weight(1f))
                StatBadge("Wind", "${weather.windSpeed} m/s", Color(0xFF64B5F6).copy(alpha = 0.1f), Color(0xFF64B5F6), Modifier.weight(1f))
                StatBadge("Pressure", "${weather.pressure.toInt()}", Color(0xFF81C784).copy(alpha = 0.1f), Color(0xFF81C784), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatBadge(label: String, value: String, bgColor: Color, textColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color(0xFFB4B9AE))
            Text(text = value, style = MaterialTheme.typography.labelMedium, color = textColor, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ImagePreviewCard(
    imagePath: String,
    originalSize: Long?,
    compressedSize: Long?,
    onRetake: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AccentColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0A0F05))
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(File(imagePath))
                        .crossfade(true)
                        .build(),
                    contentDescription = "Captured photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = AccentColor, modifier = Modifier.size(32.dp))
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Size metadata
            if (originalSize != null && compressedSize != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SizeChip(label = "Original", sizeBytes = originalSize)
                    SizeChip(label = "Compressed", sizeBytes = compressedSize)
                    val ratio = if (originalSize > 0) (compressedSize * 100 / originalSize) else 100
                    Text(
                        text = "Saved ${100 - ratio}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onRetake,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = SecondaryTextColor, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Retake Photo", color = SecondaryTextColor)
            }
        }
    }
}

@Composable
private fun SizeChip(label: String, sizeBytes: Long) {
    val kb = sizeBytes / 1024.0
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "%.1f KB".format(kb),
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFFE8F0D0),
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = SecondaryTextColor
        )
    }
}

@Composable
private fun ImagePlaceholderCard(onCapture: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D3228))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Decorative gradient box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF33503B), Color(0xFF1E3D34))
                        ),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Photo preview",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFB4B9AE),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCapture,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC2D68C))
            ) {
                Text("Capture Photo", color = Color(0xFF0D332F), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SaveButton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFC2D68C),
            disabledContainerColor = Color(0xFF3E4338)
        )
    ) {
        Text(
            text = "Save Report",
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) Color(0xFF0D332F) else Color(0xFF6B7264),
            fontWeight = FontWeight.Bold
        )
    }
}
