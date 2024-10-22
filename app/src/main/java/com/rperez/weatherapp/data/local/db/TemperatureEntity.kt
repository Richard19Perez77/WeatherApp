package com.rperez.weatherapp.data.local.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

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
    val local: Boolean,
    val humidity: Int,
    val pressure: Int,
)
