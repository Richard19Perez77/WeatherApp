package com.rperez.weatherapp.di

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.rperez.weatherapp.data.local.db.TemperatureDao
import com.rperez.weatherapp.data.local.db.TemperatureDatabase
import com.rperez.weatherapp.data.repository.TemperatureRepository
import com.rperez.weatherapp.repository.WeatherRepository
import com.rperez.weatherapp.repository.WeatherRepositoryImpl
import com.rperez.weatherapp.service.LocationService
import com.rperez.weatherapp.viewmodel.TemperatureViewModel
import com.rperez.weatherapp.viewmodel.WeatherViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Dependency Injection module for the WeatherApp, using Koin for service location.
 * This module defines all the app-level dependencies to be injected across the app.
 *
 * - Database: Provides the singleton instance of [TemperatureDatabase].
 * - DAOs: Injects the [TemperatureDao] for data access.
 * - Repositories: Injects implementations of [TemperatureRepository] and [WeatherRepository].
 * - ViewModels: Injects the [WeatherViewModel] and [TemperatureViewModel] for use in UI layers.
 */
val appModule = module {

    // Provides a singleton instance of TemperatureDatabase
    single { TemperatureDatabase.getDatabase(get()) }

    // Provides a singleton instance of TemperatureDao from the database
    single { get<TemperatureDatabase>().temperatureDao() }

    // Provides a singleton instance of TemperatureRepository with TemperatureDao injected
    single { TemperatureRepository(get()) }

    // Provides an implementation of WeatherRepository as a singleton
    single<WeatherRepository> { WeatherRepositoryImpl() }

    // Provides the WeatherViewModel for injection into UI components
    viewModelOf(::WeatherViewModel)

    // Provides the TemperatureViewModel for injection into UI components
    viewModelOf(::TemperatureViewModel)
}