package com.rperez.weatherapp.service

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

/**
 * Location service to get coords for local weather, will be getting coords or callback to activity and perform call after accepting permissions.
 */
class LocationService(private val context: Context, private val launcher: ActivityResultLauncher<String>) {

    fun getLatLon(onLocationReceived: (Double, Double) -> Unit, onPermissionRequired: () -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ContextCompat.checkSelfPermission(
                context,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        onLocationReceived(location.latitude, location.longitude)
                    }
                }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, ACCESS_FINE_LOCATION)) {
                onPermissionRequired()
            } else {
                // Directly request the permission
                launcher.launch(ACCESS_FINE_LOCATION)
            }
        }
    }

    /**
     * Check if the location permission is permanently denied. I have this in a loop to prevent it from happening but could leave it disabled.
     */
    fun isPermissionPermanentlyDenied(activity: Activity): Boolean {
        return !ActivityCompat.shouldShowRequestPermissionRationale(activity, ACCESS_FINE_LOCATION)
                && ContextCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
    }
}
