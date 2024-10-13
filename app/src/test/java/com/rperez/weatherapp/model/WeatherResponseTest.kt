package com.rperez.weatherapp.model

import org.junit.Test
import kotlin.test.assertEquals

class WeatherResponseTest {

    @Test
    fun `test WeatherResponse instantiation`() {
        val main = Main(temp = 22.5, humidity = 60)
        val weather = listOf(Weather(description = "clear sky", icon = "01d"))
        val weatherResponse = WeatherResponse(main = main, weather = weather)

        assertEquals(22.5, weatherResponse.main.temp)
        assertEquals(60, weatherResponse.main.humidity)
        assertEquals("clear sky", weatherResponse.weather[0].description)
        assertEquals("01d", weatherResponse.weather[0].icon)
    }

    @Test
    fun `test Weather instantiation`() {
        val weather = Weather(description = "rain", icon = "09d")

        assertEquals("rain", weather.description)
        assertEquals("09d", weather.icon)
    }

    @Test
    fun `test Main instantiation`() {
        val main = Main(temp = 15.0, humidity = 80)

        assertEquals(15.0, main.temp)
        assertEquals(80, main.humidity)
    }
}