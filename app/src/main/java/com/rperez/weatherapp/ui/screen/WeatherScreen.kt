package com.rperez.weatherapp.ui.screen

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.rperez.weatherapp.navigation.Screen
import com.rperez.weatherapp.ui.components.WeatherStateSuccess
import com.rperez.weatherapp.ui.components.WeatherStateSuccessLandscape
import com.rperez.weatherapp.viewmodel.WeatherState

/**
 * Basic screen to show weather and allow user to change city.
 */
@Composable
fun WeatherScreen(
    navController: NavController,
    setCityName: (String) -> Unit,
    getWeather: (String) -> Unit,
    getLocalWeather: (Context) -> Unit,
    cityName: State<String>,
    weatherState: LiveData<WeatherState>
) {
    val weatherData by weatherState.observeAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = cityName.value,
            onValueChange = { setCityName(it) },
            label = { Text(modifier = Modifier.testTag("search_label"), text = "Enter City Name") },
            modifier = Modifier
                .testTag("search_text")
                .fillMaxWidth()
                .padding(8.dp)
                .semantics {
                    contentDescription = "City name input field"
                }
        )
        Button(
            onClick = {
                getWeather.invoke(cityName.value)
            },
            modifier = Modifier
                .testTag("search_button")
                .padding(8.dp)
                .semantics {
                    contentDescription = "Search weather for the entered city"
                }
        ) {
            Text(modifier = Modifier.testTag("search_city_button_text"), text = "Search City Weather")
        }
        var context = LocalContext.current
        Button(
            onClick = {
                getLocalWeather.invoke(context)
                setCityName("Tokyo")
            },
            modifier = Modifier
                .testTag("search_local_button")
                .padding(8.dp)
                .semantics {
                    contentDescription = "Search weather for local Weather by Geo-coords"
                }
        ) {
            Text(modifier = Modifier.testTag("search_local_button_text"), text = "Search Local Weather")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        when (weatherData) {
            is WeatherState.Success -> {
                val configuration = LocalConfiguration.current
                val isLandscape =
                    configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                if (isLandscape) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        WeatherStateSuccessLandscape(weatherData)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        WeatherStateSuccess(weatherData)
                    }
                }
            }

            is WeatherState.Failure -> {
                Text(
                    modifier = Modifier
                        .semantics {
                            contentDescription =
                                "Failed to load weather data. Error: ${(weatherData as WeatherState.Failure).data?.message}"
                        }
                        .testTag("fail_text"),
                    text = "Failure: ${(weatherData as WeatherState.Failure).data?.message}",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            is WeatherState.Loading -> {
                Text(
                    modifier = Modifier
                        .semantics {
                            contentDescription = "Loading weather data"
                        }
                        .testTag("loading_text"),
                    text = "Loading...", style = MaterialTheme.typography.headlineMedium
                )
            }

            null -> {
                Text(
                    modifier = Modifier
                        .semantics {
                            contentDescription = "No weather data available"
                        }
                        .testTag("null"),
                    text = "Press Search Weather", style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            onClick = {
                navController.navigate(Screen.Temp.route)
            },
            modifier = Modifier
                .padding(8.dp)
                .testTag("zoom_button")
                .semantics {
                    contentDescription = "Navigate to temperature zoom screen"
                }
        ) {
            Text(
                modifier = Modifier.testTag("zoom_text"),
                text = "Temperature Zoom"
            )
        }
    }
}
