package com.rperez.weatherapp.ui.screen

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.*
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
import com.rperez.weatherapp.network.ConnectivityManager
import com.rperez.weatherapp.ui.components.WeatherStateSuccess
import com.rperez.weatherapp.ui.components.WeatherStateSuccessLandscape
import com.rperez.weatherapp.viewmodel.WeatherState

@Composable
fun WeatherScreen(
    navController: NavController,
    setCityName: (String) -> Unit,
    getWeather: (String) -> Unit,
    getLocalWeather: (Context) -> Unit,
    cityName: State<String>,
    weatherState: LiveData<WeatherState>
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val weatherData by weatherState.observeAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = cityName.value,
            onValueChange = setCityName,
            label = { Text(modifier = Modifier.testTag("search_label"), text = "Enter City Name") },
            modifier = Modifier
                .testTag("search_text")
                .fillMaxWidth()
                .padding(8.dp)
                .semantics { contentDescription = "City name input field" }
        )
        WeatherButtons(isLandscape, cityName.value, getWeather, getLocalWeather)
    }

    WeatherDataDisplay(weatherData, isLandscape)

    BottomNavigationButton(navController)
}

@Composable
fun WeatherButtons(
    isLandscape: Boolean,
    cityName: String,
    getWeather: (String) -> Unit,
    getLocalWeather: (Context) -> Unit,
) {
    var context = LocalContext.current
    if (isLandscape) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeatherButton(
                onClick = { getWeather(cityName) },
                tag = "search_button",
                text = "Search City Weather",
                description = "Search weather for the entered city"
            )
            WeatherButton(
                onClick = { getLocalWeather(context) },
                tag = "search_local_button",
                text = "Search Local Weather",
                description = "Search weather for local Weather by Geo-coords"
            )
        }
    } else {
        WeatherButton(
            onClick = { getWeather(cityName) },
            tag = "search_button",
            text = "Search City Weather",
            description = "Search weather for the entered city"
        )
        WeatherButton(
            onClick = { getLocalWeather(context) },
            tag = "search_local_button",
            text = "Search Local Weather",
            description = "Search weather for local Weather by Geo-coords"
        )
    }
}

@Composable
fun WeatherButton(onClick: () -> Unit, tag: String, text: String, description: String) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .testTag(tag)
            .padding(8.dp)
            .semantics { contentDescription = description }
    ) {
        Text(modifier = Modifier.testTag("${tag}_text"), text = text)
    }
}

@Composable
fun WeatherDataDisplay(weatherData: WeatherState?, isLandscape: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (weatherData) {
            is WeatherState.Success -> {
                if (isLandscape) {
                    WeatherStateSuccessLandscape(weatherData)
                } else {
                    WeatherStateSuccess(weatherData)
                }
            }

            is WeatherState.Failure -> {
                var hasInternet = ConnectivityManager.isInternetAvailable(LocalContext.current)
                if (hasInternet) {
                    CustomMessage(
                        "Failure: ${weatherData.data?.message}",
                        "Failed to load weather data."
                    )
                } else {
                    CustomMessage(
                        "No Internet: ${weatherData.data?.message}",
                        "Failed from no internet."
                    )
                }
            }

            is WeatherState.Loading -> {
                CustomMessage("Loading...", "Loading weather data")
            }

            null -> {
                CustomMessage("Press Search Weather", "No weather data available")
            }
        }
    }
}

@Composable
fun CustomMessage(message: String, contentDescription: String) {
    Text(
        modifier = Modifier
            .semantics { this.contentDescription = contentDescription }
            .testTag("custom_text"),
        text = message,
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
fun BottomNavigationButton(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            onClick = { navController.navigate(Screen.Temp.route) },
            modifier = Modifier
                .padding(8.dp)
                .testTag("zoom_button")
                .semantics { contentDescription = "Navigate to temperature zoom screen" }
        ) {
            Text(modifier = Modifier.testTag("zoom_text"), text = "Temperature Zoom")
        }
    }
}
