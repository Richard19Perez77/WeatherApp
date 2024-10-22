package com.rperez.weatherapp.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.content.Context

/**
 * Connectivity manager won't be used constantly just on local weather calls.
 */
object ConnectivityManager {

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}