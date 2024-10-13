package com.rperez.weatherapp.di

import com.rperez.weatherapp.repository.WeatherRepository
import com.rperez.weatherapp.repository.WeatherRepositoryImpl
import com.rperez.weatherapp.viewmodel.WeatherViewModel
import org.junit.Test
import org.junit.Assert.*
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class KoinApplicationUnitTest : KoinTest {

    @Test
    fun testAppModule() {
        startKoin {
            modules(appModule)
        }
        val weatherRepo: WeatherRepository = get()
        assertNotNull(weatherRepo)
        assertTrue(weatherRepo is WeatherRepositoryImpl)

        val weatherViewModel: WeatherViewModel = get()
        assertNotNull(weatherViewModel)
        stopKoin()
    }
}