package com.rperez.weatherapp.data.local.mock

import com.rperez.weatherapp.data.local.db.TemperatureEntity

object MockTemperatureEntities {
    fun getMockTemperatureEntities(): List<TemperatureEntity> {
        return listOf(
            TemperatureEntity(
                date = "2024-10-01",
                city = "HealthyCity",
                desc = "Clear Skies",
                temperature = 22.0,
                local = false,
                humidity = 40,
                pressure = 1013,
            ),
            TemperatureEntity(
                date = "2024-10-02",
                city = "HealthyCity",
                desc = "Clear Skies",
                temperature = 22.0,
                local = false,
                humidity = 40,
                pressure = 1013,
            ),
            TemperatureEntity(
                date = "2023-11-03",
                city = "ColdCity",
                desc = "Very Cold",
                temperature = -5.0,
                local = false,
                humidity = 20,
                pressure = 1000
            ),
            TemperatureEntity(
                date = "2021-12-02",
                city = "ModerateCity",
                desc = "Chilly with low humidity",
                temperature = 8.0,
                local = true,
                humidity = 25,
                pressure = 1005
            ),
            TemperatureEntity(
                date = "2024-10-03",
                city = "HotCity",
                desc = "Very hot and humid",
                temperature = 42.0,
                local = false,
                humidity = 75,
                pressure = 1015
            ),
            TemperatureEntity(
                date = "2024-10-04",
                city = "WarmCity",
                desc = "Warm and moderate",
                temperature = 25.0,
                local = false,
                humidity = 50,
                pressure = 1010
            ),
            TemperatureEntity(
                date = "2024-10-05",
                city = "HumidCity",
                desc = "Moderate temperature with high humidity",
                temperature = 23.0,
                local = false,
                humidity = 80,
                pressure = 900,
            ),
            TemperatureEntity(
                date = "2024-10-05",
                city = "HighPressureCity",
                desc = "Moderate temperature with high humidity",
                temperature = 23.0,
                local = false,
                humidity = 80,
                pressure = 1066,
            ),
        )
    }
}