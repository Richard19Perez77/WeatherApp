package com.rperez.weatherapp.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TemperatureDao {

    @Insert
    suspend fun insertTemperature(temperature: TemperatureEntity)

    @Query("SELECT * FROM temperature")
    suspend fun getAllTemperatures(): List<TemperatureEntity>

    @Query("DELETE FROM temperature")
    suspend fun deleteAllTemperatures()
}
