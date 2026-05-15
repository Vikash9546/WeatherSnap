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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = AccentColor)
                }
                Column {
                    Text(
                        text = "Saved Reports",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFE8F0D0),
                        fontWeight = FontWeight.Bold
                    )
                    if (uiState is SavedReportsUiState.Success) {
                        Text(
                            text = "${(uiState as SavedReportsUiState.Success).reports.size} report(s)",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryTextColor
                        )
                    }
                }
            }

            HorizontalDivider(color = CardBorderColor.copy(alpha = 0.5f))

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
                .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column {
                // Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(Color(0xFF0A0F05))
                ) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(File(report.imagePath))
                            .crossfade(true)
                            .build(),
                        contentDescription = "Report photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = AccentColor, modifier = Modifier.size(24.dp))
                            }
                        },
                        error = {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("📷", fontSize = 32.sp)
                            }
                        }
                    )

                    // Timestamp badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = formatTimestamp(report.timestamp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    // City & condition
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
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
                                color = AccentColor
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${report.temperature}°C",
                                style = MaterialTheme.typography.headlineSmall,
                                color = AccentColor,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Report",
                                    tint = ErrorColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatBadge("💧", "${report.humidity}%", modifier = Modifier.weight(1f))
                        StatBadge("💨", "${report.windSpeed} km/h", modifier = Modifier.weight(1f))
                        StatBadge("🔽", "${report.pressure} hPa", modifier = Modifier.weight(1f))
                    }

                    // Notes
                    if (report.notes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = CardBorderColor.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "📝 ${report.notes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryTextColor
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = CardBorderColor.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Image sizes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FileSizeInfo(label = "Original", sizeBytes = report.originalSize)
                        FileSizeInfo(label = "Compressed", sizeBytes = report.compressedSize)
                        val ratio = if (report.originalSize > 0)
                            (report.compressedSize * 100 / report.originalSize) else 100
                        Text(
                            text = "Saved ${100 - ratio}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentColor,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
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
private fun FileSizeInfo(label: String, sizeBytes: Long) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "%.1f KB".format(sizeBytes / 1024.0),
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
