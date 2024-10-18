package com.rperez.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rperez.weatherapp.data.local.db.TemperatureEntity
import com.rperez.weatherapp.data.repository.TemperatureRepository
import kotlinx.coroutines.launch

class TemperatureViewModel(private val repository: TemperatureRepository) : ViewModel() {

    fun insertTemperature(temperature: TemperatureEntity) {
        viewModelScope.launch {
            repository.insertTemperature(temperature)
        }
    }

    suspend fun getAllTemperatures() = repository.getAllTemperatures()
}
