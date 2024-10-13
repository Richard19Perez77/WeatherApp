package com.rperez.weatherapp.failing

import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import org.junit.Assert.*
import com.rperez.weatherapp.di.WeatherApp
import com.rperez.weatherapp.di.appModule
import com.rperez.weatherapp.repository.WeatherRepository
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = WeatherApp::class, sdk = [28], manifest = Config.NONE)
class WeatherAppModuleUnitTest : KoinTest {

    @Before
    fun setup() {
        startKoin {
            modules(appModule) // assuming appModule is your Koin module
        }
    }

    @Test
    fun testKoinInitialization() {
        // Get the application context using ApplicationProvider
        val weatherApp = ApplicationProvider.getApplicationContext<WeatherApp>()
        assertNotNull(weatherApp)
        // Verify appModule registration
        val weatherRepository = get<WeatherRepository>()
        assertNotNull(weatherRepository)
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}