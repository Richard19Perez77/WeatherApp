package com.rperez.weatherapp.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "temperature")
data class TemperatureEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val temperature: Double
)