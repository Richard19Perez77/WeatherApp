package com.rperez.weatherapp.ui.screen

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
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
    getWeather: (String) -> Unit,
    weatherState: LiveData<WeatherState>
) {
    var cityName by remember { mutableStateOf("Tokyo") }
    val weatherData by weatherState.observeAsState()

    LaunchedEffect(Unit) {
        getWeather.invoke(cityName)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = cityName,
            onValueChange = { cityName = it },
            label = { Text("Enter City Name") },
            modifier = Modifier
                .testTag("search_text")
                .fillMaxWidth()
                .padding(8.dp)
        )
        Button(
            onClick = {
                getWeather.invoke(cityName)
            },
            modifier = Modifier
                .testTag("search_button")
                .padding(8.dp)
        ) {
            Text(text = "Search Weather")
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
                val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
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
                    text = "Failure: ${(weatherData as WeatherState.Failure).data?.message}",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            is WeatherState.Loading -> {
                Text(text = "Loading...", style = MaterialTheme.typography.headlineMedium)
            }

            null -> {
                Text(text = "Empty...", style = MaterialTheme.typography.headlineMedium)
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
        ) {
            Text(text = "Temperature Zoom")
        }
    }
}
