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
 * ViewModel for managing temperature data through the TemperatureRepository.
 * This class allows for inserting, deleting, and retrieving temperature entries
 * from the local database, including mock data for testing purposes.
 */
class TemperatureViewModel(private val repository: TemperatureRepository) : ViewModel() {

    /**
     * Inserts a new temperature record into the database.
     * @param temperature The temperature data to be inserted.
     */
    fun insertTemperature(temperature: TemperatureEntity) {
        viewModelScope.launch {
            repository.insertTemperature(temperature)
        }
    }

    /**
     * Inserts a predefined set of mock temperature records into the database.
     * Useful for testing or demonstration purposes.
     */
    fun insertMockTemperatures() {
        viewModelScope.launch {
            for (item in MockTemperatureEntities.getMockTemperatureEntities()) {
                repository.insertTemperature(item)
            }
        }
    }

    /**
     * Deletes all temperature records from the database.
     */
    fun deleteAllTemperatures() {
        viewModelScope.launch {
            repository.deleteAllTemperatures()
        }
    }

    /**
     * Retrieves all temperature records from the database as a flow.
     * This allows for observing temperature data changes in real time.
     * @return A Flow emitting a list of all temperature entries.
     */
    fun getAllTemperatures(): Flow<List<TemperatureEntity>> {
        return flow {
            val temperatures = repository.getAllTemperatures()
            emit(temperatures)
        }
    }
}
