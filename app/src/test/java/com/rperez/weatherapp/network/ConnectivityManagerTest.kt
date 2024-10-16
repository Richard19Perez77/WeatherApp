package com.rperez.weatherapp.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * Test class to show if internet is available or not.
 */
class ConnectivityManagerTest {

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var connectivityManager: ConnectivityManager

    @Mock
    private lateinit var networkCapabilities: NetworkCapabilities

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
    }

    @Test
    fun testIsInternetAvailable_ReturnsTrue_WhenInternetIsAvailable() {
        // Arrange
        val activeNetwork = mock(android.net.Network::class.java)
        `when`(connectivityManager.activeNetwork).thenReturn(activeNetwork)
        `when`(connectivityManager.getNetworkCapabilities(activeNetwork)).thenReturn(networkCapabilities)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)

        // Act
        val result = com.rperez.weatherapp.network.ConnectivityManager.isInternetAvailable(context)

        // Assert
        assertTrue(result)
    }

    @Test
    fun testIsInternetAvailable_ReturnsFalse_WhenInternetIsNotAvailable() {
        // Arrange
        val activeNetwork = mock(android.net.Network::class.java)
        `when`(connectivityManager.activeNetwork).thenReturn(activeNetwork)
        `when`(connectivityManager.getNetworkCapabilities(activeNetwork)).thenReturn(networkCapabilities)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(false)

        // Act
        val result = com.rperez.weatherapp.network.ConnectivityManager.isInternetAvailable(context)

        // Assert
        assertFalse(result)
    }

    @Test
    fun testIsInternetAvailable_ReturnsFalse_WhenNoActiveNetwork() {
        // Arrange
        `when`(connectivityManager.activeNetwork).thenReturn(null)

        // Act
        val result = com.rperez.weatherapp.network.ConnectivityManager.isInternetAvailable(context)

        // Assert
        assertFalse(result)
    }
}
