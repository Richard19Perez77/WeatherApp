package com.rperez.weatherapp.viewmodel

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rperez.weatherapp.data.local.db.TemperatureEntity
import com.rperez.weatherapp.model.WeatherResponse
import com.rperez.weatherapp.repository.WeatherException
import com.rperez.weatherapp.repository.WeatherRepository
import com.rperez.weatherapp.service.LocationService
import com.rperez.weatherapp.viewmodel.WeatherState.CitySuccess
import com.rperez.weatherapp.viewmodel.WeatherState.Failure
import com.rperez.weatherapp.viewmodel.WeatherState.LocalSuccess
import kotlinx.coroutines.launch
import java.time.LocalDate
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

    fun setupWeatherObserver(temperatureViewModel: TemperatureViewModel) {
        weatherState.observeForever { observer ->
            var data = "${LocalDate.now()}"
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
                            temperatureViewModel.insertTemperature(temperatureEntity)
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
                            temperatureViewModel.insertTemperature(temperatureEntity)
                        }
                    }
                }

                is WeatherState.Failure -> {}
                is WeatherState.Loading -> {}
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

    fun getWeather(cityName: String) {
        if (cityName.isNotEmpty()) {
            viewModelScope.launch {
                _weatherState.value = WeatherState.Loading
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
            viewModelScope.launch {
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
}

/**
 * State of calls to be reflected in UI
 */
sealed class WeatherState {
    data class CitySuccess(val data: WeatherResponse?) : WeatherState()
    data class LocalSuccess(val data: WeatherResponse?) : WeatherState()
    data class Failure(val data: WeatherException?) : WeatherState()
    object Loading : WeatherState()
}
