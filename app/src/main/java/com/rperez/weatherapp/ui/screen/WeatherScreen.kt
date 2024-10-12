package com.rperez.weatherapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.rperez.weatherapp.viewmodel.WeatherState
import java.util.Locale

/**
 * Basic screen to show weather and allow user to change city.
 */
@Composable
fun WeatherScreen(
    getWeather: (String) -> Unit,
    weatherState: LiveData<WeatherState>
) {
    var cityName by remember { mutableStateOf("Tokyo") }
    val weatherData by weatherState.observeAsState()

    LaunchedEffect(Unit) {
        getWeather.invoke(cityName)
    }

    TextField(
        value = cityName,
        onValueChange = { cityName = it },
        label = { Text("Enter City Name") },
        modifier = Modifier
            .testTag("search_text")
            .fillMaxWidth()
            .padding(16.dp)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                getWeather.invoke(cityName)
            },
            modifier = Modifier
                .testTag("search_button")
                .padding(16.dp)
        ) {
            Text(text = "Search Weather")
        }

        when (weatherData) {
            is WeatherState.Success -> {
                Text(
                    modifier = Modifier.testTag("temp_text"),
                    text = "Temperature: ${(weatherData as WeatherState.Success).data?.main?.temp}°C",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    modifier = Modifier.testTag("description_text"),
                    text = (weatherData as WeatherState.Success).data?.weather?.firstOrNull()?.description?.replaceFirstChar { it.uppercase(Locale.ROOT) } ?: "",
                    style = MaterialTheme.typography.headlineLarge
                )

                val iconUrl = "https://openweathermap.org/img/wn/${(weatherData as WeatherState.Success).data?.weather[0]?.icon}@2x.png"
                WeatherIcon(iconUrl = iconUrl)
            }

            is WeatherState.Failure -> {
                Text(text = "Failure: ${(weatherData as WeatherState.Failure).data?.message}", style = MaterialTheme.typography.headlineMedium)
            }

            is WeatherState.Loading -> {
                Text(text = "Loading...", style = MaterialTheme.typography.headlineMedium)
            }

            null -> {
                Text(text = "Empty...", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}
