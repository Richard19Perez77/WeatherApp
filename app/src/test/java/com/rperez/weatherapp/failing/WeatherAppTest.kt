package com.rperez.weatherapp.failing

import androidx.test.core.app.ApplicationProvider
import com.rperez.weatherapp.di.appModule
import org.junit.After
import org.junit.Test
import org.junit.Before
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.test.verify.verify
import org.koin.core.context.GlobalContext
import org.koin.test.KoinTest
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class WeatherAppTest : KoinTest{

    @Before
    fun setup() {
        startKoin {
            androidContext(ApplicationProvider.getApplicationContext())
            modules(appModule)
        }
    }

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `koin is started and modules are verified`() {
        val isKoinStarted = GlobalContext.getOrNull() != null
        assertTrue(isKoinStarted)
        appModule.verify()
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}
