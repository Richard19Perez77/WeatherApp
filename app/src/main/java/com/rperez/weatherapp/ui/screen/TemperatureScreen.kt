package com.rperez.weatherapp.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rperez.weatherapp.viewmodel.WeatherState

/**
 * Temperature Screen is only the temperature in large font
 */
@Composable
fun TemperatureScreen(weatherState: WeatherState?) {
    Text(
        modifier = Modifier
            .fillMaxSize(),
        text = "",
        style = MaterialTheme.typography.headlineLarge
    )
}