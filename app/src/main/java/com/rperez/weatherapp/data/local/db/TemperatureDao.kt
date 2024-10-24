package com.rperez.weatherapp.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object (DAO) for managing temperature data in the local database.
 * This interface defines methods for inserting, retrieving, and deleting temperature entries.
 */
@Dao
interface TemperatureDao {

    /**
     * Inserts a new temperature entry into the database.
     * If an entry with the same primary key already exists, it will be replaced.
     *
     * @param temperature The temperature entity to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemperature(temperature: TemperatureEntity)

    /**
     * Retrieves all temperature entries from the database.
     *
     * @return A list of all temperature entities stored in the database.
     */
    @Query("SELECT * FROM temperature")
    suspend fun getAllTemperatures(): List<TemperatureEntity>


    /**
     * Deletes all temperature entries from the database.
     * This method clears the entire temperature table.
     */
    @Query("DELETE FROM temperature")
    suspend fun deleteAllTemperatures()
}
