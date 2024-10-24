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
 * LocationService is responsible for obtaining the device's geographic coordinates
 * for local weather data retrieval. It requests location permissions and retrieves
 * the last known location, providing the coordinates via a callback function.
 *
 * @param context The application context.
 * @param launcher An ActivityResultLauncher for handling permission requests.
 */
class LocationService(
    private val context: Context,
    private val launcher: ActivityResultLauncher<String>
) {

    /**
     * Retrieves the current latitude and longitude.
     *
     * This method checks for location permissions and, if granted, fetches the last
     * known location. If permissions are not granted, it handles the request
     * for location access.
     *
     * @param onLocationReceived Callback function to be invoked with latitude
     *                            and longitude once they are available.
     * @param onPermissionRequired Callback function to be invoked when permission
     *                             rationale should be shown to the user.
     */
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    ACCESS_FINE_LOCATION
                )
            ) {
                onPermissionRequired()
            } else {
                // Directly request the permission
                launcher.launch(ACCESS_FINE_LOCATION)
            }
        }
    }

    /**
     * Checks if the location permission has been permanently denied.
     *
     * This method is used to prevent repeated permission requests when the user
     * has permanently denied location access.
     *
     * @param activity The activity from which to check permission status.
     * @return True if permission is permanently denied; false otherwise.
     */
    fun isPermissionPermanentlyDenied(activity: Activity): Boolean {
        return !ActivityCompat.shouldShowRequestPermissionRationale(activity, ACCESS_FINE_LOCATION)
                && ContextCompat.checkSelfPermission(
            activity,
            ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    }
}
