package com.rperez.weatherapp.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

/**
 * Composable function to display the successful state of a city weather update.
 *
 * This function shows the current temperature, weather description, and an icon
 * representing the weather condition. It provides accessibility support through
 * semantics for screen readers.
 *
 * @param temp The current temperature in degrees Celsius.
 * @param desc A textual description of the current weather conditions.
 * @param icon The icon code used to fetch the appropriate weather icon from
 *             the OpenWeatherMap API.
 */
@Composable
fun WeatherStateSuccess(
    temp: Double,
    desc: String,
    icon: String,
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
                contentDescription = desc
            }
            .testTag("description_text"),
        text = desc,
        style = MaterialTheme.typography.headlineMedium
    )

    // Construct the URL for the weather icon using the icon code
    val iconUrl =
        "https://openweathermap.org/img/wn/$icon@2x.png"
    WeatherIcon(iconUrl = iconUrl)
}