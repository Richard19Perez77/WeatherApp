package com.rperez.weatherapp.ui.navigation

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rperez.weatherapp.R
import com.rperez.weatherapp.network.model.WeatherUI
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.min

/**
 * Temperature Screen is only the temperature in large font
 */
@Composable
fun TemperatureScreen(
    modifier: Modifier,
    weatherUIState: StateFlow<WeatherUI>
) {
    var weatherData = remember { mutableStateOf<WeatherUI>(weatherUIState.value) }

    LaunchedEffect(weatherUIState) {
        weatherUIState.collectLatest { state ->
            weatherData.value = state
        }
    }

    var temp = weatherData.value.temperature
    var tempString = if (temp == Double.MIN_VALUE){
        stringResource(R.string.na)
    } else {
        stringResource(R.string.temp_c, temp)
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
        var semanticString = stringResource(R.string.temp_c_semantics, temp)
        Text(
            modifier = Modifier
                .testTag("temp_zoom_text")
                .semantics {
                    contentDescription = semanticString
                }
                .focusable(),
            text = tempString,
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = fontSize.sp)
        )
    }
}
