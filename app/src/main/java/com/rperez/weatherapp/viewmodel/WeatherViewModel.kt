package com.rperez.weatherapp.viewmodel

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.content.Context
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * View model to hold the Weather State as calls change it.
 */
class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    /**
     * Launcher is used for callback to re call the get weather by coord, after user accepts permissions.
     */
    lateinit var launcher: ActivityResultLauncher<String>

    /**
     * City name is going to be from shared pref's
     */
    private val _cityName = mutableStateOf("")
    val cityName = _cityName

    /**
     * Will contain variables for the UI on get weather calls.
     */
    private val _uiState = MutableStateFlow<WeatherUI>(WeatherUI())
    val uiState: StateFlow<WeatherUI> = _uiState.asStateFlow()

    /**
     * Local instance of coords to be updated frequently.
     */
    lateinit var coords: Pair<Double, Double>

    /**
     * Allow for observer to write to room for later usage.
     */
    fun setupWeatherObserver(
        insertTemperature: (TemperatureEntity) -> Unit,
    ) {
        viewModelScope.launch {
            uiState.collectLatest {
                // check for no errors and done loading
                if (it.errorMessage.isEmpty() && it.isLoading == false) {
                    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val temperatureEntity = TemperatureEntity(
                        date = date,
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

    fun getCityName(): State<String> {
        return cityName
    }

    fun setCityName(cityName: String) {
        _cityName.value = cityName
    }

    private var currentJob: Job? = null

    /**
     * Get weather by city name. Service will return a formatted name that will be used back into the field.
     */
    fun getWeather(cityName: String) {
        if (cityName.isNotEmpty()) {
            currentJob.let { it?.cancel() }

            try {
                currentJob = viewModelScope.launch {
                    _uiState.update { currState ->
                        WeatherUI()
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
     * Use Location to get local weather, not by city
     */
    fun getLocalWeather(context: Context) {
        if (::coords.isInitialized) {
            currentJob.let { it?.cancel() }
            try {
                currentJob = viewModelScope.launch {
                    _uiState.update { currState ->
                        WeatherUI()
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
            LocationService(context, launcher).getLatLon(
                onLocationReceived = { lat, lon ->
                    coords = Pair(lat, lon)
                    getLocalWeather(context.applicationContext)
                },
                onPermissionRequired = {
                    AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.location_permission_needed))
                        .setMessage(context.getString(R.string.this_app_needs_the_location_permission_to_fetch_weather_data_for_your_current_location))
                        .setPositiveButton(context.getString(R.string.grant)) { _, _ ->
                            launcher.launch(ACCESS_FINE_LOCATION)
                        }
                        .setNegativeButton(context.getString(R.string.cancel), null)
                        .show()
                }
            )
        }
    }

    fun setRequestLocationPermissionLauncher(launcher: ActivityResultLauncher<String>) {
        this.launcher = launcher
    }

    /**
     * Cancel job on lifecycle ending composable.
     */
    override fun onCleared() {
        super.onCleared()
        currentJob?.cancel()
    }
}