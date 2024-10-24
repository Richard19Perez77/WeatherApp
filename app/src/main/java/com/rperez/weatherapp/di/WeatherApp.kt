package com.rperez.weatherapp.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

/**
 * Custom Application class for initializing dependency injection using Koin.
 *
 * This class ensures that Koin's dependency injection framework is started
 * when the app launches, providing application-wide access to injected classes.
 */
class WeatherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Koin for dependency injection with the application context
        startKoin {
            // Pass the Android context to Koin
            androidContext(this@WeatherApp)
            // Load the app's module(s) to manage dependencies
            modules(appModule)
        }
    }
}