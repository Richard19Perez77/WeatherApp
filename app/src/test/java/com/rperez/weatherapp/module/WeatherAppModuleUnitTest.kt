package com.rperez.weatherapp.module

import com.rperez.weatherapp.di.appModule
import com.rperez.weatherapp.repository.WeatherRepository
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.GlobalContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.verify.verify
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WeatherAppModuleUnitTest : KoinTest {

    @Before
    fun setup() {
        // Start Koin with your module
        startKoin {
            modules(appModule)
        }
    }

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `Koin is initialized and module is verified`() {
        // Verify Koin started
        val isKoinStarted = GlobalContext.getOrNull() != null
        assertTrue(isKoinStarted)

        // Verify WeatherRepository is available
        val weatherRepository = get<WeatherRepository>()
        assertNotNull(weatherRepository)

        // Verify that the app module is correctly set up
        appModule.verify()
    }

    @After
    fun tearDown() {
        // Stop Koin after the test
        stopKoin()
    }
}
