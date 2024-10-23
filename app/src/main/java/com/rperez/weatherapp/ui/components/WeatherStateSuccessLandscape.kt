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
import com.rperez.weatherapp.ui.components.WeatherIcon

/**
 * UI Composable for Successful update of city weather call and in landscape view.
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