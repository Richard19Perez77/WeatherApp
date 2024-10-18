package com.rperez.weatherapp.data.repository

import com.rperez.weatherapp.data.local.db.TemperatureDao
import com.rperez.weatherapp.data.local.db.TemperatureEntity

class TemperatureRepository(private val temperatureDao: TemperatureDao) {

    suspend fun insertTemperature(temperature: TemperatureEntity) {
        temperatureDao.insertTemperature(temperature)
    }

    suspend fun getAllTemperatures(): List<TemperatureEntity> {
        return temperatureDao.getAllTemperatures()
    }

    suspend fun deleteAllTemperatures() {
        temperatureDao.deleteAllTemperatures()
    }
}
