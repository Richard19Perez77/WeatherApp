package com.rperez.weatherapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database that holds daily temperature values.
 * This database contains a single entity, [TemperatureEntity], and provides access
 * to data operations via the [TemperatureDao].
 *
 * @Database annotation defines the entities and the version of the database.
 * The version should be incremented when there are schema changes.
 */
@Database(entities = [TemperatureEntity::class], version = 1,  exportSchema = false)
abstract class TemperatureDatabase : RoomDatabase() {

    // Provides the DAO to access temperature-related data operations.
    abstract fun temperatureDao(): TemperatureDao

    companion object {

        @Volatile
        private var INSTANCE: TemperatureDatabase? = null

        /**
         * Retrieves the singleton instance of [TemperatureDatabase].
         * If the database doesn't exist, it creates one using the Room library.
         *
         * The function is synchronized to prevent multiple threads from
         * creating multiple instances.
         *
         * @param context The application context used to build the database.
         * @return The [TemperatureDatabase] instance.
         */
        fun getDatabase(context: Context): TemperatureDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TemperatureDatabase::class.java,
                    "temperature_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
