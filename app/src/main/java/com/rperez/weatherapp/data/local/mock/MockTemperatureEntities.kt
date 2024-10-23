package com.rperez.weatherapp.data.local.mock

import com.rperez.weatherapp.data.local.db.TemperatureEntity

/**
 * As development and testing continues use mock enttities to test ui. Using mock here helps keep the device in a demo'able shape.
 */
object MockTemperatureEntities {
    fun getMockTemperatureEntities(): List<TemperatureEntity> {
        return listOf(
            TemperatureEntity(
                date = "2024-10-01",
                city = "Healthy City",
                desc = "Clear Skies",
                temperature = 22.0,
                humidity = 40,
                pressure = 1013,
            ),
            TemperatureEntity(
                date = "2024-10-02",
                city = "Healthy City",
                desc = "Clear Skies",
                temperature = 22.0,
                humidity = 40,
                pressure = 1013,
            ),
            TemperatureEntity(
                date = "2023-11-03",
                city = "Cold City",
                desc = "Very Cold",
                temperature = -5.0,
                humidity = 20,
                pressure = 1000,
            ),
            TemperatureEntity(
                date = "2021-12-02",
                city = "Moderate City",
                desc = "Chilly with low humidity",
                temperature = 8.0,
                humidity = 25,
                pressure = 1005,
            ),
            TemperatureEntity(
                date = "2024-10-03",
                city = "Hot City",
                desc = "Very hot and humid",
                temperature = 42.0,
                humidity = 75,
                pressure = 1015,
            ),
            TemperatureEntity(
                date = "2024-10-04",
                city = "Warm City",
                desc = "Warm and moderate",
                temperature = 25.0,
                humidity = 50,
                pressure = 1010,
            ),
            TemperatureEntity(
                date = "2024-10-05",
                city = "Humid City",
                desc = "Moderate temperature with high humidity",
                temperature = 23.0,
                humidity = 80,
                pressure = 900,
            ),
            TemperatureEntity(
                date = "2024-10-05",
                city = "High Pressure City",
                desc = "Moderate temperature with high humidity",
                temperature = 23.0,
                humidity = 80,
                pressure = 1066,
            ),
        )
    }
}