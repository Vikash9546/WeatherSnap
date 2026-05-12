package com.weathersnap.ui.report

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.weathersnap.ui.theme.*
import com.weathersnap.ui.weather.WeatherUiModel

@Composable
fun CreateReportScreen(
    weatherData: WeatherUiModel,
    onBack: () -> Unit,
    onNavigateToCamera: () -> Unit,
    onNavigateToSavedReports: () -> Unit,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsState()
    val imagePath by viewModel.imagePath.collectAsState()
    val origSize by viewModel.originalSize.collectAsState()
    val compSize by viewModel.compressedSize.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = Brush.verticalGradient(listOf(HeaderGradientTop, HeaderGradientBottom)))
                .padding(16.dp)
        ) {
            Column {
                Text("Create Report", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Capture, compress, annotate", fontSize = 14.sp, color = TextPrimary)
            }
            Button(
                onClick = onBack,
                modifier = Modifier.align(Alignment.TopEnd),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = BackgroundDark)
            ) {
                Text("Back", color = Color.White)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                WeatherCardReadOnly(model = weatherData)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Crossfade(targetState = imagePath, label = "ImageFade") { path ->
                        if (path == null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(4f / 3f)
                                    .background(AccentOlive.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Photo preview", color = AccentOlive, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            AsyncImage(
                                model = path,
                                contentDescription = "Captured Photo",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(4f / 3f),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    
                    AnimatedVisibility(visible = imagePath != null) {
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Original: ${origSize / 1024} KB", color = PressureAmber, fontSize = 12.sp)
                            Text("Compressed: ${compSize / 1024} KB", color = HumidityTeal, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onNavigateToCamera,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOlive)
                    ) {
                        Text(if (imagePath == null) "Capture Photo" else "Retake Photo", color = BackgroundDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Field Notes", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = viewModel::updateNotes,
                        placeholder = { Text("Notes", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentOlive,
                            unfocusedBorderColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }

        Box(modifier = Modifier.padding(16.dp)) {
            Button(
                onClick = { viewModel.saveReport(weatherData, onNavigateToSavedReports) },
                modifier = Modifier.fillMaxWidth(),
                enabled = imagePath != null,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentOlive,
                    disabledContainerColor = SurfaceDark,
                    contentColor = BackgroundDark,
                    disabledContentColor = TextSecondary
                )
            ) {
                Text("Save Report", modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
fun WeatherCardReadOnly(model: WeatherUiModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                com.weathersnap.ui.weather.WeatherMetric("Humidity", "${model.humidity}%", HumidityTeal)
                com.weathersnap.ui.weather.WeatherMetric("Wind", "${model.windSpeed} m/s", WindBlue)
                com.weathersnap.ui.weather.WeatherMetric("Pressure", "${model.pressure}", PressureAmber)
            }
        }
    }
}
