package com.rperez.weatherapp.data.local.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Main entity for Temperature and weather values, will use key for unique entries to keep list of daily entries minimized.
 */
@Entity(
    tableName = "temperature",
    indices = [Index(value = ["date", "city", "temperature", "desc"], unique = true)]
)
data class TemperatureEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val city: String,
    val desc: String,
    val temperature: Double,
    val humidity: Int,
    val pressure: Int,
    val timeStamp: Long = System.currentTimeMillis()
)
