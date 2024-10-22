package com.rperez.weatherapp.ui.screen

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rperez.weatherapp.viewmodel.WeatherUiState
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.min

/**
 * Temperature Screen is only the temperature in large font
 */
@Composable
fun TemperatureScreen(modifier: Modifier, weatherState: StateFlow<WeatherUiState>) {
    var weather = weatherState.collectAsState()
    var temp = when (weather.value){
        is WeatherUiState.Error, is WeatherUiState.NoData -> {
            "N/A"
        }
        is WeatherUiState.Success -> {
            (weather.value as WeatherUiState.Success).temperature.temperature
        }
    }

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp
    val padding = 8.dp
    val availableWidth = min(screenHeightDp.value, screenWidthDp.value).dp - (padding * 2)
    val fontSize = with(LocalDensity.current) {
        (availableWidth.toPx() / 7)
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .testTag("temp_zoom_text")
                .semantics {
                    contentDescription = "Temperature in degrees Celsius is $temp"
                }
                .focusable(),
            text = "${temp}°C",
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = fontSize.sp)
        )
    }
}
