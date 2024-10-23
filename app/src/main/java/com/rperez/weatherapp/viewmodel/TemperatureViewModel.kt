package com.rperez.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rperez.weatherapp.data.local.db.TemperatureEntity
import com.rperez.weatherapp.data.local.mock.MockTemperatureEntities
import com.rperez.weatherapp.data.repository.TemperatureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * View model for temperature using the temperature repository.
 */
class TemperatureViewModel(private val repository: TemperatureRepository) : ViewModel() {

    fun insertTemperature(temperature: TemperatureEntity) {
        viewModelScope.launch {
            repository.insertTemperature(temperature)
        }
    }

    fun insertMockTemperatures() {
        viewModelScope.launch {
            for (item in MockTemperatureEntities.getMockTemperatureEntities()) {
                repository.insertTemperature(item)
            }
        }
    }

    fun deleteAllTemperatures() {
        viewModelScope.launch {
            repository.deleteAllTemperatures()
        }
    }

    fun getAllTemperatures(): Flow<List<TemperatureEntity>> {
        return flow {
            val temperatures = repository.getAllTemperatures()
            emit(temperatures)
        }
    }
}
