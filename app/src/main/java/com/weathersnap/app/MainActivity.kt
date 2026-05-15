package com.weathersnap.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.weathersnap.app.navigation.WeatherSnapNavHost
import com.weathersnap.app.ui.theme.WeatherSnapTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherSnapTheme {
                WeatherSnapNavHost()
            }
        }
    }
}
