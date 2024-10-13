package com.rperez.weatherapp.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun WeatherStateSuccess(weatherData: WeatherState?) {
    Text(
        modifier = Modifier
            .semantics {
                contentDescription = "Current temperature in degrees Celsius is ${(weatherData as WeatherState.Success).data?.main?.temp}°C"
            }
            .testTag("temp_text"),
        text = "Temperature: ${(weatherData as WeatherState.Success).data?.main?.temp}°C",
        style = MaterialTheme.typography.headlineMedium
    )
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
        text = weatherData.data?.weather?.firstOrNull()?.description?.replaceFirstChar {
            it.uppercase(
                Locale.ROOT
            )
        } ?: "",
        style = MaterialTheme.typography.headlineLarge
    )

    val iconUrl =
        "https://openweathermap.org/img/wn/${weatherData.data?.weather[0]?.icon}@2x.png"
    WeatherIcon(iconUrl = iconUrl)
}