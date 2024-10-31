package com.rperez.weatherapp.ui.navigation

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
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rperez.weatherapp.R
import com.rperez.weatherapp.network.ConnectivityManager
import com.rperez.weatherapp.network.model.WeatherUI
import com.rperez.weatherapp.ui.components.WeatherStateSuccess
import com.rperez.weatherapp.ui.components.WeatherStateSuccessLandscape
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

/**
 * Composable function that displays the current weather information.
 * It allows users to enter a city name or get weather data based on their location.
 * The UI updates with weather data for the current day once a successful API call is made.
 */
@Composable
fun WeatherScreen(
    modifier: Modifier,
    navController: NavController,
    setCityName: (String) -> Unit,
    getWeather: (Context) -> Unit,
    getLocalWeather: (Context) -> Unit,
    weatherUIState: StateFlow<WeatherUI>,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val weatherUI = weatherUIState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = weatherUI.value.name,
            onValueChange = setCityName,
            label = { Text(modifier = Modifier.testTag("search_label"), text = "Enter City Name") },
            modifier = Modifier
                .testTag("search_text")
                .fillMaxWidth()
                .padding(8.dp)
                .semantics { contentDescription = "City name input field" }
        )
        WeatherButtons(isLandscape, getWeather, getLocalWeather)
    }

    WeatherDataDisplay(weatherUIState, isLandscape)
    BottomNavigationButton(navController)
}

/**
 * Composable function that renders buttons for fetching weather data.
 * It adapts the layout based on the screen orientation (landscape or portrait).
 */
@Composable
fun WeatherButtons(
    isLandscape: Boolean,
    getWeather: (Context) -> Unit,
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
                onClick = { getWeather(context) },
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
            onClick = { getWeather(context) },
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

/**
 * A reusable composable button for fetching weather data.
 * It includes semantic tags for accessibility and testing.
 */
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

/**
 * Composable function that displays weather data based on the current UI state.
 * It handles loading states, errors, and displays the appropriate weather information.
 */
@Composable
fun WeatherDataDisplay(
    weatherUIState: StateFlow<WeatherUI>,
    isLandscape: Boolean
) {
    var weatherData = remember { mutableStateOf<WeatherUI>(weatherUIState.value) }

    // Collect the latest weather UI state from the StateFlow
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

        // Display loading message if data is being fetched
        if (weatherData.value.isLoading) {
            CustomMessage(
                stringResource(R.string.loading),
                stringResource(R.string.loading_weather_data)
            )
        } else {
            // Display error messages based on the state of the weather data
            if (weatherData.value.errorMessage.isNotEmpty()) {
                var hasInternet = ConnectivityManager.isInternetAvailable(LocalContext.current)
                if (hasInternet) {
                    CustomMessage(
                        weatherData.value.errorMessage,
                        stringResource(R.string.failed_to_load)
                    )
                    CustomMessage(
                        weatherData.value.name,
                        stringResource(R.string.failed_city, weatherData.value.name),
                        tag = "custom_city_tag"
                    )
                } else {
                    CustomMessage(
                        stringResource(R.string.no_internet_message),
                        stringResource(R.string.failed_from_no_internet)
                    )
                }
            } else {
                // Display the weather information based on screen orientation
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

/**
 * Composable function that displays a custom message.
 * It includes accessibility semantics for better screen reader support.
 */
@Composable
fun CustomMessage(message: String, contentDescription: String, tag : String = "custom_text") {
    Text(
        modifier = Modifier
            .padding(16.dp, 0.dp, 16.dp, 0.dp)
            .semantics { this.contentDescription = contentDescription }
            .testTag(tag),
        text = message,
        style = MaterialTheme.typography.headlineMedium
    )
}

/**
 * Composable function for rendering a button that navigates to a different screen.
 * It includes a semantic description for accessibility.
 */
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
