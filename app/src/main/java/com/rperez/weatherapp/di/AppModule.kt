package com.rperez.weatherapp.di

import com.rperez.weatherapp.data.local.db.TemperatureDatabase
import com.rperez.weatherapp.data.repository.TemperatureRepository
import com.rperez.weatherapp.repository.WeatherRepository
import com.rperez.weatherapp.repository.WeatherRepositoryImpl
import com.rperez.weatherapp.viewmodel.TemperatureViewModel
import com.rperez.weatherapp.viewmodel.WeatherViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * The injection module classes to be injected
 */
val appModule = module {
    single { TemperatureDatabase.getDatabase(get()) }
    single { get<TemperatureDatabase>().temperatureDao() }
    single { TemperatureRepository(get()) }
    single<WeatherRepository> { WeatherRepositoryImpl() }
    viewModelOf(::WeatherViewModel)
    viewModelOf(::TemperatureViewModel)
}