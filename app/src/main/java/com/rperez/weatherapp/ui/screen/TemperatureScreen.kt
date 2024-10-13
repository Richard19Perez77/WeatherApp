package com.rperez.weatherapp.ui.screen

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import com.rperez.weatherapp.viewmodel.WeatherState
import kotlin.math.min

/**
 * Temperature Screen is only the temperature in large font
 */
@Composable
fun TemperatureScreen(weatherState: LiveData<WeatherState>) {
    var tempState = weatherState.observeAsState()
    val temp = (tempState.value as? WeatherState.Success)?.data?.main?.temp?.toString() ?: "N/A"

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp
    val padding = 8.dp
    val availableWidth = min(screenHeightDp.value, screenWidthDp.value).dp - (padding * 2)
    val fontSize = with(LocalDensity.current) {
        (availableWidth.toPx() / 7)
    }

    Box(
        modifier = Modifier
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
