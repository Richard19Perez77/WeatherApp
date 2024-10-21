package com.rperez.weatherapp.viewmodel

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rperez.weatherapp.data.local.db.TemperatureEntity
import com.rperez.weatherapp.network.model.WeatherResponse
import com.rperez.weatherapp.network.model.WeatherState
import com.rperez.weatherapp.repository.WeatherException
import com.rperez.weatherapp.repository.WeatherRepository
import com.rperez.weatherapp.service.LocationService
import com.rperez.weatherapp.network.model.WeatherState.CitySuccess
import com.rperez.weatherapp.network.model.WeatherState.Failure
import com.rperez.weatherapp.network.model.WeatherState.Loading
import com.rperez.weatherapp.network.model.WeatherState.LocalSuccess
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * View model to hold the Weather State as calls change it.
 */
class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    lateinit var launcher: ActivityResultLauncher<String>

    private val _cityName = mutableStateOf("")
    val cityName = _cityName

    private val _weatherState = MutableLiveData<WeatherState>()
    val weatherState: LiveData<WeatherState> = _weatherState

    lateinit var coords: Pair<Double, Double>

    fun setupWeatherObserver(insertTemperature: (TemperatureEntity) -> Unit, lifecycleOwner : LifecycleOwner) {
        weatherState.observe(lifecycleOwner) { observer ->
            val data = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            when (weatherState.value) {
                is CitySuccess -> {
                    var item = (weatherState.value as CitySuccess)
                    item.data?.main?.temp?.toDouble().let {
                        if (it != null) {
                            var temperatureEntity = TemperatureEntity(
                                date = data,
                                temperature = it,
                                local = false,
                                city = item.data?.name ?: "",
                                desc = item.data?.weather?.firstOrNull()?.description?.replaceFirstChar {
                                    it.uppercase(Locale.ROOT)
                                }.toString(),
                                humidity = item.data?.main?.humidity ?: Int.MIN_VALUE,
                                pressure = item.data?.main?.pressure ?: Int.MIN_VALUE,
                            )
                            insertTemperature.invoke(temperatureEntity)
                        }
                    }
                }

                is LocalSuccess -> {
                    var item = (weatherState.value as LocalSuccess)
                    item.data?.main?.temp?.toDouble().let {
                        if (it != null) {
                            var temperatureEntity = TemperatureEntity(
                                date = data,
                                temperature = it,
                                local = true,
                                city = item.data?.name ?: "",
                                desc = item.data?.weather?.firstOrNull()?.description?.replaceFirstChar {
                                    it.uppercase(Locale.ROOT)
                                }.toString(),
                                humidity = item.data?.main?.humidity ?: Int.MIN_VALUE,
                                pressure = item.data?.main?.pressure ?: Int.MIN_VALUE,
                            )
                            insertTemperature.invoke(temperatureEntity)
                        }
                    }
                }

                is Failure -> {}
                is Loading -> {}
                null -> {}
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

    fun getWeather(cityName: String) {
        if (cityName.isNotEmpty()) {

            currentJob?.cancel()

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
        }
    }


    /**
     * Use Location to get local weather, not by city
     */
    fun getLocalWeather(context: Context) {
        if (::coords.isInitialized) {

            currentJob?.cancel()

            currentJob = viewModelScope.launch {
                _weatherState.value = WeatherState.Loading
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
                    getLocalWeather(context)
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