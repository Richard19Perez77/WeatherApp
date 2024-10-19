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
    var temp: Double? = null
    var desc: String? = null
    var icon: String? = null
    when (weatherData) {
        is WeatherState.CitySuccess -> {
            temp = weatherData.data?.main?.temp?.toDouble()
            desc = weatherData.data?.weather?.firstOrNull()?.description?.replaceFirstChar {
                it.uppercase(Locale.ROOT)
            }.toString()
            icon = weatherData.data?.weather[0]?.icon
        }

        is WeatherState.LocalSuccess -> {
            temp = weatherData.data?.main?.temp?.toDouble()
            desc = weatherData.data?.weather?.firstOrNull()?.description?.replaceFirstChar {
                it.uppercase(Locale.ROOT)
            }.toString()
            icon = weatherData.data?.weather[0]?.icon
        }

        is WeatherState.Failure -> {
            null
        }

        is WeatherState.Loading -> {
            null
        }

        null -> {
            null
        }
    }
    Text(
        modifier = Modifier
            .semantics {
                contentDescription = "Current temperature in degrees Celsius is $temp°C"
            }
            .testTag("temp_text"),
        text = "Temperature: $temp°C",
        style = MaterialTheme.typography.headlineMedium
    )
    Text(
        modifier = Modifier
            .semantics {
                contentDescription = desc ?: ""
            }
            .testTag("description_text"),
        text = desc ?: "",
        style = MaterialTheme.typography.headlineLarge
    )

    val iconUrl =
        "https://openweathermap.org/img/wn/$icon@2x.png"
    WeatherIcon(iconUrl = iconUrl)
}