package com.rperez.weatherapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room DB to hold the daily values.
 */
@Database(entities = [TemperatureEntity::class], version = 1)
abstract class TemperatureDatabase : RoomDatabase() {
    abstract fun temperatureDao(): TemperatureDao

    companion object {
        @Volatile
        private var INSTANCE: TemperatureDatabase? = null

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
