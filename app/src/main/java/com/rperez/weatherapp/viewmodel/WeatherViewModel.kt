package com.rperez.weatherapp.viewmodel

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rperez.weatherapp.R
import com.rperez.weatherapp.data.local.db.TemperatureEntity
import com.rperez.weatherapp.network.model.WeatherUI
import com.rperez.weatherapp.repository.WeatherRepository
import com.rperez.weatherapp.service.LocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * ViewModel that manages the weather data state and handles fetching weather information
 * either by city name or by user's current location.
 */
class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    /**
     * Launcher to request location permission. Callback will trigger fetching weather by coordinates
     * once permission is granted.
     */
    lateinit var launcher: ActivityResultLauncher<String>

    /**
     * Holds the name of the city, typically fetched from shared preferences.
     */
    private val _cityName = mutableStateOf("")
    val cityName = _cityName

    /**
     * StateFlow to store the current UI state (weather data, loading, error states).
     */
    private val _uiState = MutableStateFlow<WeatherUI>(WeatherUI())
    val uiState: StateFlow<WeatherUI> = _uiState.asStateFlow()

    /**
     * Stores the user's current coordinates (latitude, longitude).
     */
    lateinit var coords: Pair<Double, Double>

    /**
     * Observes changes in weather data and stores it into the local database (Room)
     * once data is successfully loaded.
     */
    fun setupWeatherObserver(
        insertTemperature: (TemperatureEntity) -> Unit,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                uiState.collectLatest {

                    // Insert temperature data into the local database if no errors and loading is complete.
                    if (it.errorMessage.isEmpty() && it.isLoading == false) {
                        val temperatureEntity = TemperatureEntity(
                            date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                            temperature = it.temperature,
                            city = it.name,
                            desc = it.description,
                            humidity = it.humidity,
                            pressure = it.airPressure,
                        )
                        insertTemperature.invoke(temperatureEntity)
                    }
                }
            }
        }
    }

    /**
     * Returns the current city name as a State.
     */
    fun getCityName(): State<String> {
        return cityName
    }

    /**
     * Sets the city name.
     */
    fun setCityName(cityName: String) {
        _cityName.value = cityName
    }

    private var currentJob: Job? = null

    /**
     * Fetches weather information based on the given city name. Resets the UI state and updates it
     * upon success or failure.
     */
    fun getWeather(cityName: String) {
        if (cityName.isNotEmpty()) {

            // Cancel any ongoing job to avoid conflicts
            currentJob.let { it?.cancel() }

            try {
                currentJob = viewModelScope.launch {
                    _uiState.update { currState ->
                        WeatherUI() // Reset UI state to initial loading state
                    }
                    val result = repository.getWeatherByCityData(cityName)
                    result.onSuccess {
                        var res = result.getOrNull()
                        if (res != null) {
                            setCityName(res.name)
                            _uiState.update { newState ->
                                WeatherUI(
                                    isLoading = false,
                                    temperature = res.main.temp,
                                    humidity = res.main.humidity,
                                    airPressure = res.main.pressure,
                                    name = res.name,
                                    description = res.weather.firstOrNull()?.description ?: "",
                                    icon = res.weather.firstOrNull()?.icon ?: "",
                                    errorMessage = "",
                                )
                            }
                        }
                    }
                    result.onFailure {
                        var res = result.exceptionOrNull()
                        if (res != null) {
                            _uiState.update { newState ->
                                WeatherUI(
                                    isLoading = false,
                                    errorMessage = res.message.toString()
                                )
                            }
                        }
                    }
                }
            } finally {
                currentJob = null
            }
        }
    }

    /**
     * Fetches local weather using the user's current GPS coordinates. If not available,
     * requests location permission and fetches weather data once permission is granted.
     */
    fun getLocalWeather(context: Context) {
        if (::coords.isInitialized) {

            currentJob.let { it?.cancel() }// Cancel any ongoing job to avoid conflicts

            try {
                currentJob = viewModelScope.launch {
                    _uiState.update { currState ->
                        WeatherUI() // Reset UI state to initial loading state
                    }

                    val result = repository.getWeatherGeoData(coords.first, coords.second)

                    result.onSuccess {
                        var res = result.getOrNull()
                        if (res != null) {
                            setCityName(res.name)
                            _uiState.update { newState ->
                                WeatherUI(
                                    isLoading = false,
                                    temperature = res.main.temp,
                                    humidity = res.main.humidity,
                                    airPressure = res.main.pressure,
                                    name = res.name,
                                    description = res.weather.firstOrNull()?.description ?: "",
                                    icon = res.weather.firstOrNull()?.icon ?: "",
                                    errorMessage = "",
                                )
                            }
                        }
                    }
                    result.onFailure {
                        var res = result.exceptionOrNull()
                        if (res != null) {
                            _uiState.update {
                                WeatherUI(
                                    isLoading = false,
                                    errorMessage = res.message.toString(),
                                )
                            }
                        }
                    }
                }
            } finally {
                currentJob = null
            }
        } else {

            // Request location updates and permissions if coordinates are not initialized
            LocationService(context, launcher).getLatLon(
                onLocationReceived = { lat, lon ->
                    coords = Pair(lat, lon)
                    getLocalWeather(context.applicationContext) // Retry fetching weather with updated coordinates
                },
                onPermissionRequired = {
                    AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.location_permission_needed))
                        .setMessage(context.getString(R.string.this_app_needs_the_location_permission_to_fetch_weather_data_for_your_current_location))
                        .setPositiveButton(context.getString(R.string.grant)) { _, _ ->
                            launcher.launch(ACCESS_FINE_LOCATION)
                        }
                        .setNegativeButton(
                            context.getString(R.string.cancel),
                            DialogInterface.OnClickListener { _, _ ->

                                // Fallback to fetching weather for a default city (e.g., Paris) if permission is denied
                                getWeather("Paris")
                            }
                        ).show()
                }
            )
        }
    }

    /**
     * Sets the ActivityResultLauncher used for requesting location permissions.
     */
    fun setRequestLocationPermissionLauncher(launcher: ActivityResultLauncher<String>) {
        this.launcher = launcher
    }

    /**
     * Cancel any ongoing job when the ViewModel is cleared.
     */
    override fun onCleared() {
        super.onCleared()
        currentJob?.cancel()
    }
}