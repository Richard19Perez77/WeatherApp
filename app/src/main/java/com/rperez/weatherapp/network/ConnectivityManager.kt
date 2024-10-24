package com.rperez.weatherapp.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.content.Context

/**
 * Utility object to check for network connectivity.
 *
 * This object provides a function to check whether an active internet connection
 * is available. It's mainly used for verifying connectivity before making local
 * weather API calls. This avoids constant network checks.
 */
object ConnectivityManager {

    /**
     * Checks if the device has an active internet connection.
     *
     * @param context The context used to access the system service.
     * @return True if the device is connected to the internet, false otherwise.
     */
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}