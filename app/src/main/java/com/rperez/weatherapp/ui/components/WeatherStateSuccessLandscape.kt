package com.rperez.weatherapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.rperez.weatherapp.R

/**
 * Composable function to display the successful state of a city weather update
 * in landscape orientation.
 *
 * This function arranges the temperature, weather icon, and description
 * horizontally, suitable for landscape layouts. It supports accessibility
 * by providing content descriptions for screen readers.
 *
 * @param temp The current temperature in degrees Celsius.
 * @param desc A textual description of the current weather conditions.
 * @param icon The icon code used to fetch the appropriate weather icon from
 *             the OpenWeatherMap API.
 */
@Composable
fun WeatherStateSuccessLandscape(
    temp: Double,
    desc: String,
    icon: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var semanticString = stringResource(
            R.string.current_temperature_in_degrees_celsius_is_c,
            temp
        )
        Text(
            modifier = Modifier
                .testTag("temp_text")
                .semantics {
                    contentDescription = semanticString
                },
            text = stringResource(R.string.temperature_c, temp),
            style = MaterialTheme.typography.headlineLarge
        )

        // Construct the URL for the weather icon using the icon code
        val iconUrl =
            stringResource(R.string.https_openweathermap_org_img_wn_2x_png, icon)
        WeatherIcon(iconUrl = iconUrl)

        Text(
            modifier = Modifier
                .semantics {
                    contentDescription = desc
                }
                .testTag("description_text"),
            text = desc,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}