package com.rperez.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.rperez.weatherapp.repository.WeatherRepository
import com.rperez.weatherapp.ui.theme.WeatherAppTheme
import com.rperez.weatherapp.viewmodel.WeatherViewModel
import com.rperez.weatherapp.viewmodel.WeatherViewModelFactory
import java.util.Locale

/**
 * MainActivity starting point of application.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(
            WeatherRepository()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }
    }
}

/**
 * Basic screen to show weather and allow user to change city.
 */
@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val apiKey = BuildConfig.API_KEY

    var cityName by remember { mutableStateOf("Tokyo") }
    val weatherData by viewModel.weatherData.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.getWeather(cityName, apiKey)
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
                viewModel.getWeather(cityName, apiKey)
            },
            modifier = Modifier
                .testTag("search_button")
                .padding(16.dp)
        ) {
            Text(text = "Search Weather")
        }

        weatherData?.let { weather ->
            Text(
                modifier = Modifier.testTag("temp_text"),
                text = "Temperature: ${weather.main.temp}°C",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                modifier = Modifier.testTag("description_text"),
                text = weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase(Locale.ROOT) } ?: "",
                style = MaterialTheme.typography.headlineLarge
            )

            val iconUrl = "https://openweathermap.org/img/wn/${weather.weather[0].icon}@2x.png"
            WeatherIcon(iconUrl = iconUrl)
        } ?: run {
            Text(text = "Loading...", style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun WeatherIcon(iconUrl: String) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .build()
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier.size(128.dp),
        contentScale = ContentScale.FillBounds
    )
}

