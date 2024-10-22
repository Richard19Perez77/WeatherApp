package com.rperez.weatherapp.viewmodel

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rperez.weatherapp.data.local.db.TemperatureEntity
import com.rperez.weatherapp.network.model.WeatherState
import com.rperez.weatherapp.network.model.WeatherState.CitySuccess
import com.rperez.weatherapp.network.model.WeatherState.Failure
import com.rperez.weatherapp.network.model.WeatherState.Loading
import com.rperez.weatherapp.network.model.WeatherState.LocalSuccess
import com.rperez.weatherapp.repository.WeatherException
import com.rperez.weatherapp.repository.WeatherRepository
import com.rperez.weatherapp.service.LocationService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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
    private val _weatherState = MutableStateFlow<WeatherState>(Loading)
    val weatherState: StateFlow<WeatherState> = _weatherState

    /**
     * Local instance of coords to be updated frequently.
     */
    lateinit var coords: Pair<Double, Double>

    fun setupWeatherObserver(
        insertTemperature: (TemperatureEntity) -> Unit,
    ) {
        viewModelScope.launch {
            weatherState.collectLatest { observer ->
                val data = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                when (observer) {
                    is CitySuccess -> {
                        observer.data?.main?.temp?.toDouble()?.let {
                            val temperatureEntity = TemperatureEntity(
                                date = data,
                                temperature = it,
                                local = false,
                                city = observer.data.name,
                                desc = observer.data.weather.firstOrNull()?.description
                                    ?.replaceFirstChar { it.uppercase(Locale.ROOT) } ?: "",
                                humidity = observer.data.main.humidity,
                                pressure = observer.data.main.pressure,
                            )
                            insertTemperature.invoke(temperatureEntity)
                        }
                    }

                    is LocalSuccess -> {
                        observer.data?.main?.temp?.toDouble()?.let {
                            val temperatureEntity = TemperatureEntity(
                                date = data,
                                temperature = it,
                                local = true,
                                city = observer.data.name,
                                desc = observer.data.weather.firstOrNull()?.description
                                    ?.replaceFirstChar { it.uppercase(Locale.ROOT) } ?: "",
                                humidity = observer.data.main.humidity,
                                pressure = observer.data.main.pressure,
                            )
                            insertTemperature.invoke(temperatureEntity)
                        }
                    }

                    is Failure -> {}
                    is Loading -> {}
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

            currentJob.let {
                it?.cancel()
            }

            try {
                currentJob = viewModelScope.launch {
                    _weatherState.value = Loading
                    val result = repository.getWeatherByCityData(cityName)
                    result.onSuccess {
                        _weatherState.value = CitySuccess(result.getOrNull())
                        _cityName.value = (_weatherState.value as CitySuccess).data?.name ?: ""
                    }
                    result.onFailure {
                        _weatherState.value = Failure(result.exceptionOrNull() as WeatherException?)
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

            currentJob.let {
                it?.cancel()
            }

            try {
                currentJob = viewModelScope.launch {
                    _weatherState.value = Loading
                    val result = repository.getWeatherGeoData(coords.first, coords.second)
                    result.onSuccess {
                        _weatherState.value = LocalSuccess(result.getOrNull())
                        _cityName.value = (_weatherState.value as LocalSuccess).data?.name ?: ""
                    }
                    result.onFailure {
                        _weatherState.value = Failure(result.exceptionOrNull() as WeatherException?)
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
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the location permission to fetch weather data for your current location.")
                        .setPositiveButton("Grant") { _, _ ->
                            launcher.launch(ACCESS_FINE_LOCATION)
                        }
                        .setNegativeButton("Cancel", null)
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