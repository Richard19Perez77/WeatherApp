package com.rperez.weatherapp.viewmodel

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * View model to hold the Weather State as calls change it.
 */
class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    lateinit var launcher: ActivityResultLauncher<String>

    private val viewModelState = MutableStateFlow(
        WeatherViewModelState(isLoading = true)
    )
    val uiState: StateFlow<WeatherUiState> = viewModelState
        .map(WeatherViewModelState::toUiState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    private val _weatherState = MutableStateFlow<WeatherState?>(null)
    val weatherState: StateFlow<WeatherState?> = _weatherState

    private val _cityName = mutableStateOf("")
    val cityName = _cityName

    lateinit var coords: Pair<Double, Double>
    private var currentJob: Job? = null

    fun setupWeatherObserver(
        insertTemperature: (TemperatureEntity) -> Unit,
        lifecycleOwner: LifecycleOwner
    ) {
        lifecycleOwner.lifecycleScope.launch {
            weatherState.collect { state ->
                val data = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                when (state) {
                    is CitySuccess -> {
                        val item = state
                        item.data?.main?.temp?.toDouble().let {
                            if (it != null) {
                                val temperatureEntity = TemperatureEntity(
                                    date = data,
                                    temperature = it,
                                    local = false,
                                    city = item.data?.name ?: "",
                                    desc = item.data?.weather?.firstOrNull()?.description?.replaceFirstChar {
                                        it.uppercase(Locale.ROOT)
                                    }.toString(),
                                    humidity = item.data?.main?.humidity ?: Int.MIN_VALUE,
                                    pressure = item.data?.main?.pressure ?: Int.MIN_VALUE,
                                    icon = item.data?.weather?.firstOrNull()?.icon ?: "",
                                )
                                insertTemperature(temperatureEntity)
                            }
                        }
                    }

                    is LocalSuccess -> {
                        val item = state
                        item.data?.main?.temp?.toDouble().let {
                            if (it != null) {
                                val temperatureEntity = TemperatureEntity(
                                    date = data,
                                    temperature = it,
                                    local = true,
                                    city = item.data?.name ?: "",
                                    desc = item.data?.weather?.firstOrNull()?.description?.replaceFirstChar {
                                        it.uppercase(Locale.ROOT)
                                    }.toString(),
                                    humidity = item.data?.main?.humidity ?: Int.MIN_VALUE,
                                    pressure = item.data?.main?.pressure ?: Int.MIN_VALUE,
                                    icon = item.data?.weather?.firstOrNull()?.icon ?: "",
                                )
                                insertTemperature(temperatureEntity)
                            }
                        }
                    }

                    is Failure -> {
                        // Handle failure
                    }

                    is Loading -> {
                        // Handle loading state
                    }

                    null -> {
                        // Handle null state
                    }
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

    fun getWeather(cityName: String) {
        if (cityName.isNotEmpty()) {

            currentJob?.cancel()
            viewModelState.update { it.copy(isLoading = true, cityName = cityName) }

            currentJob = viewModelScope.launch {
                val result = repository.getWeatherByCityData(cityName)
                result.onSuccess {
                    val weatherData = result.getOrNull()?.main?.temp?.let {
                        TemperatureEntity(
                            date = LocalDate.now().toString(),
                            temperature = it,
                            local = false,
                            city = result.getOrNull()?.name ?: "",
                            desc = result.getOrNull()?.weather?.firstOrNull()?.description ?: "",
                            humidity = result.getOrNull()?.main?.humidity ?: Int.MIN_VALUE,
                            pressure = result.getOrNull()?.main?.pressure ?: Int.MIN_VALUE,
                            icon = result.getOrNull()?.weather?.firstOrNull()?.icon ?: "",
                        )
                    }

                    viewModelState.update {
                        it.copy(temperatureEntity = weatherData, isLoading = false)
                    }
                }
                result.onFailure {
                    viewModelState.update { currentState ->
                        currentState.copy(
                            errorMessage = currentState.errorMessage,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    /**
     * Use Location to get local weather, not by city
     */
    fun getLocalWeather(context: Context) {
        if (::coords.isInitialized) {

            currentJob?.cancel()

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

override fun onCleared() {
    super.onCleared()
    currentJob?.cancel()
}
}

sealed interface WeatherUiState {
    val isLoading: Boolean
    val cityName: String

    data class NoData(
        override val isLoading: Boolean,
        override val cityName: String
    ) : WeatherUiState

    data class Success(
        val temperature: TemperatureEntity,
        override val isLoading: Boolean,
        override val cityName: String
    ) : WeatherUiState

    data class Error(
        val errorMessage: String,
        override val isLoading: Boolean,
        override val cityName: String
    ) : WeatherUiState
}

data class WeatherViewModelState(
    val temperatureEntity: TemperatureEntity? = null,
    val isLoading: Boolean = false,
    val cityName: String = "",
    val errorMessage: String? = null
) {
    fun toUiState(): WeatherUiState = when {
        errorMessage != null -> WeatherUiState.Error(
            errorMessage = errorMessage,
            isLoading = isLoading,
            cityName = cityName
        )

        temperatureEntity != null -> WeatherUiState.Success(
            temperature = temperatureEntity,
            isLoading = isLoading,
            cityName = cityName
        )

        else -> WeatherUiState.NoData(
            isLoading = isLoading,
            cityName = cityName
        )
    }
}
