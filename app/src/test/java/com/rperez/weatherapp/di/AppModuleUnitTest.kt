package com.rperez.weatherapp.di

import com.rperez.weatherapp.repository.WeatherRepository
import com.rperez.weatherapp.repository.WeatherRepositoryImpl
import com.rperez.weatherapp.viewmodel.WeatherViewModel
import org.junit.After
import org.junit.Test
import org.junit.Before
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.test.inject
import org.koin.test.KoinTest
import org.koin.test.verify.verify
import kotlin.test.assertNotNull

class AppModuleUnitTest : KoinTest {

    private val weatherRepo: WeatherRepository by inject()
    private val weatherViewModel: WeatherViewModel by inject()

    @Before
    fun setup() {
        startKoin {
            modules(appModule)
        }
    }

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun checkIfAppModuleProvidesWeatherRepositoryImpl() {
        appModule.verify()

        // Test specific injections
        assert(weatherRepo is WeatherRepositoryImpl)
        assertNotNull(weatherViewModel)
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}
