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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.rperez.weatherapp.ui.screen.WeatherIcon
import com.rperez.weatherapp.viewmodel.WeatherState
import java.util.Locale

/**
 * UI Composable for Successful update of city weather call
 */
@Composable
fun WeatherStateSuccessLandscape(weatherData: WeatherState?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .testTag("temp_text")
                .semantics {
                    contentDescription = "Current temperature in degrees Celsius is ${(weatherData as WeatherState.Success).data?.main?.temp}°C"
                },
            text = "Temperature: ${(weatherData as WeatherState.Success).data?.main?.temp}°C",
            style = MaterialTheme.typography.headlineMedium
        )

        val iconUrl =
            "https://openweathermap.org/img/wn/${weatherData.data?.weather[0]?.icon}@2x.png"
        WeatherIcon(iconUrl = iconUrl)

        Text(
            modifier = Modifier
                .semantics {
                    contentDescription =
                        ("" + weatherData.data?.weather?.firstOrNull()?.description?.replaceFirstChar {
                            it.uppercase(
                                Locale.ROOT
                            )
                        })
                }
                .testTag("description_text"),
            text = ("" + weatherData.data?.weather?.firstOrNull()?.description?.replaceFirstChar {
                it.uppercase(
                    Locale.ROOT
                )
            }),
            style = MaterialTheme.typography.headlineLarge
        )
    }
}