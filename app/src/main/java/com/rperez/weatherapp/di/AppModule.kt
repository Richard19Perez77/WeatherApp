package com.rperez.weatherapp.di

import com.rperez.weatherapp.repository.WeatherRepository
import com.rperez.weatherapp.repository.WeatherRepositoryImpl
import com.rperez.weatherapp.viewmodel.WeatherViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * The injection module classes to be injected
 */
val appModule = module {
    single<WeatherRepository> { WeatherRepositoryImpl() }
    viewModelOf(::WeatherViewModel)
}
