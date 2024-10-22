package com.rperez.weatherapp.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Allow DB transactions and replace new entries with same data.
 */
@Dao
interface TemperatureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemperature(temperature: TemperatureEntity)

    @Query("SELECT * FROM temperature")
    suspend fun getAllTemperatures(): List<TemperatureEntity>

    @Query("DELETE FROM temperature")
    suspend fun deleteAllTemperatures()
}
