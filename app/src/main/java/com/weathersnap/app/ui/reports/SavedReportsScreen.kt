package com.weathersnap.app.ui.reports

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.weathersnap.app.data.local.entity.WeatherReportEntity
import com.weathersnap.app.ui.theme.AccentColor
import com.weathersnap.app.ui.theme.BackgroundColor
import com.weathersnap.app.ui.theme.CardBorderColor
import com.weathersnap.app.ui.theme.ErrorColor
import com.weathersnap.app.ui.theme.SecondaryTextColor
import com.weathersnap.app.ui.theme.SurfaceColor
import com.weathersnap.app.util.WeatherCodeUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavedReportsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SavedReportsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                                text = "Saved Reports",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFF1B2A1D),
                                fontWeight = FontWeight.Bold
                            )
                            if (uiState is SavedReportsUiState.Success) {
                                Text(
                                    text = "${(uiState as SavedReportsUiState.Success).reports.size} report stored locally",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF1B2A1D).copy(alpha = 0.7f)
                                )
                            }
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

            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    (fadeIn(tween(400)) + slideInVertically { it / 4 })
                        .togetherWith(fadeOut(tween(200)))
                },
                label = "ReportsContent"
            ) { state ->
                when (state) {
                    is SavedReportsUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = AccentColor, modifier = Modifier.size(48.dp))
                        }
                    }
                    is SavedReportsUiState.Empty -> EmptyReportsState()
                    is SavedReportsUiState.Success -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item { Spacer(modifier = Modifier.height(8.dp)) }
                            itemsIndexed(
                                items = state.reports,
                                key = { _, report -> report.id }
                            ) { index, report ->
                                ReportCard(
                                    report = report,
                                    index = index,
                                    onDelete = { viewModel.deleteReport(report) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }
                    }
                    is SavedReportsUiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Error: ${state.message}",
                                color = ErrorColor,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportCard(report: WeatherReportEntity, index: Int, onDelete: () -> Unit) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(300 + index * 50)) + slideInVertically(tween(300 + index * 50)) { it / 4 }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2D3228)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column {
                // Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(File(report.imagePath))
                            .crossfade(true)
                            .build(),
                        contentDescription = "Report photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),
                        loading = {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color(0xFFC2D68C), modifier = Modifier.size(24.dp))
                            }
                        }
                    )
                }

                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    // City & Temperature Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = report.cityName,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFFE8F0D0),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = report.condition,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFB4B9AE)
                            )
                            Text(
                                text = formatTimestamp(report.timestamp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF6B7264)
                            )
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF323B06), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "${report.temperature}°C",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFFC2D68C),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            IconButton(
                                onClick = onDelete,
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color(0xFF383C33), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete report",
                                    tint = Color(0xFFCF6679),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Size Comparison Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FileSizeCard(
                            label = "Original",
                            sizeBytes = report.originalSize,
                            backgroundColor = Color(0xFF3A382D),
                            valueColor = Color(0xFFB08C4A),
                            modifier = Modifier.weight(1f)
                        )
                        FileSizeCard(
                            label = "Compressed",
                            sizeBytes = report.compressedSize,
                            backgroundColor = Color(0xFF323831),
                            valueColor = Color(0xFF5A8B7A),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Notes/Tag
                    if (report.notes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF383C33), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = report.notes,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFB4B9AE)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun StatBadge(icon: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(BackgroundColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = icon, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFE8F0D0)
        )
    }
}

@Composable
private fun FileSizeCard(
    label: String,
    sizeBytes: Long,
    backgroundColor: Color,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFB4B9AE)
            )
            Text(
                text = "${sizeBytes / 1024} KB",
                style = MaterialTheme.typography.labelMedium,
                color = valueColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmptyReportsState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text("📋", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No reports yet",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFE8F0D0),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Search for a city, capture a photo and save your first weather report.",
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryTextColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
