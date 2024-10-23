package com.rperez.weatherapp.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.rperez.weatherapp.ui.screen.WeatherIcon

/**
 * UI Composable for Successful update of city weather call
 */
@Composable
fun WeatherStateSuccess(
    temp: Double?,
    desc: String?,
    icon: String?,
) {
    Text(
        modifier = Modifier
            .semantics {
                contentDescription = "Current temperature in degrees Celsius is $temp°C"
            }
            .testTag("temp_text"),
        text = "Temperature: $temp°C",
        style = MaterialTheme.typography.headlineLarge
    )
    Text(
        modifier = Modifier
            .semantics {
                contentDescription = desc ?: ""
            }
            .testTag("description_text"),
        text = desc ?: "",
        style = MaterialTheme.typography.headlineMedium
    )

    val iconUrl =
        "https://openweathermap.org/img/wn/$icon@2x.png"
    WeatherIcon(iconUrl = iconUrl)
}