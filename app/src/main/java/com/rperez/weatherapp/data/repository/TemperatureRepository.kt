package com.rperez.weatherapp.data.repository

import com.rperez.weatherapp.data.local.db.TemperatureDao
import com.rperez.weatherapp.data.local.db.TemperatureEntity

/**
 * Repository for managing temperature data operations.
 * This class acts as a single source of truth for temperature-related data
 * and abstracts the data source (in this case, the database via TemperatureDao).
 * It will be injected into the app module and used by the TemperatureViewModel.
 */
class TemperatureRepository(private val temperatureDao: TemperatureDao) {

    /**
     * Inserts a temperature record into the database.
     * @param temperature The temperature entity to be inserted.
     */
    fun insertTemperature(temperature: TemperatureEntity) {
        temperatureDao.insertTemperature(temperature)
    }

    /**
     * Retrieves all temperature records from the database.
     * @return A list of TemperatureEntity objects representing all stored temperatures.
     */
    fun getAllTemperatures(): List<TemperatureEntity> {
        return temperatureDao.getAllTemperatures()
    }

    /**
     * Deletes all temperature records from the database.
     */
    fun deleteAllTemperatures() {
        temperatureDao.deleteAllTemperatures()
    }
}
