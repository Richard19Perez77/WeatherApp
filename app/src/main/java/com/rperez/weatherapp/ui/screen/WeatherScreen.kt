package com.rperez.weatherapp.ui.screen

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rperez.weatherapp.R
import com.rperez.weatherapp.navigation.Screen
import com.rperez.weatherapp.network.ConnectivityManager
import com.rperez.weatherapp.network.model.WeatherUI
import com.rperez.weatherapp.ui.components.WeatherStateSuccess
import com.rperez.weatherapp.ui.components.WeatherStateSuccessLandscape
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

/**
 * Shows the UI for current weather, from coords or city entry. Will overwrite city with full name on successful call and show data on screen for the day.
 */
@Composable
fun WeatherScreen(
    modifier: Modifier,
    navController: NavController,
    setCityName: (String) -> Unit,
    getWeather: (String) -> Unit,
    getLocalWeather: (Context) -> Unit,
    cityName: State<String>,
    weatherUIState: StateFlow<WeatherUI>,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE


    Column(
        modifier = modifier.fillMaxWidth(),
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

    WeatherDataDisplay(weatherUIState, isLandscape)
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
                text = stringResource(R.string.search_city_weather),
                description = stringResource(R.string.search_weather_for_the_entered_city)
            )
            WeatherButton(
                onClick = { getLocalWeather(context) },
                tag = "get_local_button",
                text = stringResource(R.string.get_your_local_weather),
                description = stringResource(R.string.search_weather_for_local_weather_by_geo_coords)
            )
        }
    } else {
        WeatherButton(
            onClick = { getWeather(cityName) },
            tag = "search_button",
            text = stringResource(R.string.search_city_weather),
            description = stringResource(R.string.search_weather_for_the_entered_city)
        )
        WeatherButton(
            onClick = { getLocalWeather(context) },
            tag = "get_local_button",
            text = stringResource(R.string.get_your_local_weather),
            description = stringResource(R.string.search_weather_for_local_weather_by_geo_coords)
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
fun WeatherDataDisplay(
    weatherUIState: StateFlow<WeatherUI>,
    isLandscape: Boolean
) {
    var weatherData = remember { mutableStateOf<WeatherUI>(weatherUIState.value) }

    LaunchedEffect(weatherUIState) {
        weatherUIState.collectLatest { state ->
            weatherData.value = state
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (weatherData.value.isLoading) {
            CustomMessage(
                stringResource(R.string.loading),
                stringResource(R.string.loading_weather_data)
            )
        } else {
            if (weatherData.value.errorMessage.isNotEmpty()) {
                var hasInternet = ConnectivityManager.isInternetAvailable(LocalContext.current)
                if (hasInternet) {
                    CustomMessage(
                        stringResource(R.string.failure_message, weatherData.value.errorMessage),
                        stringResource(R.string.failed_to_load)
                    )
                } else {
                    CustomMessage(
                        stringResource(R.string.no_internet_message, weatherData.value.errorMessage),
                        stringResource(R.string.failed_from_no_internet)
                    )
                }
            } else {
                if (isLandscape) {
                    WeatherStateSuccessLandscape(
                        weatherData.value.temperature,
                        weatherData.value.description,
                        weatherData.value.icon,
                    )
                } else {
                    WeatherStateSuccess(
                        weatherData.value.temperature,
                        weatherData.value.description,
                        weatherData.value.icon,
                    )
                }
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
        var semanticString = stringResource(R.string.navigate_to_temperature_zoom_screen)
        Button(
            onClick = { navController.navigate(Screen.Temp.route) },
            modifier = Modifier
                .padding(8.dp)
                .testTag("zoom_button")
                .semantics {
                    contentDescription = semanticString
                }
        ) {
            Text(
                modifier = Modifier.testTag("zoom_text"),
                text = stringResource(R.string.temperature_zoom)
            )
        }
    }
}
