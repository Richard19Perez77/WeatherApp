package com.rperez.weatherapp.viewmodel

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.rperez.weatherapp.model.WeatherResponse
import com.rperez.weatherapp.repository.WeatherException
import com.rperez.weatherapp.repository.WeatherRepository
import com.rperez.weatherapp.viewmodel.WeatherState.Failure
import com.rperez.weatherapp.viewmodel.WeatherState.Success
import kotlinx.coroutines.launch

const val LOCATION_PERMISSION_REQUEST_CODE = 1010101

/**
 * View model to hold the Weather State as calls change it.
 */
class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _cityName = mutableStateOf("")
    val cityName = _cityName

    private val _weatherState = MutableLiveData<WeatherState>()
    val weatherState: LiveData<WeatherState> = _weatherState

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
                    _weatherState.value = Success(result.getOrNull())
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
        var lat = 0.0
        var lon = 0.0

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        lat = location.latitude
                        lon = location.longitude
                    } else {
                        return@addOnSuccessListener
                    }
                }
                .addOnFailureListener {
                    return@addOnFailureListener
                }
        } else {
            ActivityCompat.requestPermissions(
                (context as Activity),
                arrayOf(ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading
            val result = repository.getWeatherGeoData(lat, lon)
            result.onSuccess {
                _weatherState.value = Success(result.getOrNull())
            }
            result.onFailure {
                _weatherState.value = Failure(result.exceptionOrNull() as WeatherException?)
            }
        }
    }
}

/**
 * State of calls to be reflected in UI
 */
sealed class WeatherState {
    data class Success(val data: WeatherResponse?) : WeatherState()
    data class Failure(val data: WeatherException?) : WeatherState()
    object Loading : WeatherState()
}
