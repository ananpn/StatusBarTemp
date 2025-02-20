package com.example.statusbartemp.Location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AppLocationManager(context: Context) : AppLocationInfa {
    val context = context

    override suspend fun GetLocationString(latitude: Double, longitude: Double): String? {
        return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context)
            val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

            if ((addresses?.size ?: 0) > 0) {
                val address = addresses!![0]
                address.locality ?: address.subAdminArea ?: address.subLocality ?: address.adminArea
            } else {
                null
            }
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun GetCurrentLocation(
        onLocationReceived: (Location) -> Unit,
        onException : () -> Unit,
        onPermissionsDenied : () -> Unit,
    ) {
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val currentLocation = fusedLocationClient.getCurrentLocation(PRIORITY_BALANCED_POWER_ACCURACY, null)
            while (!currentLocation.isComplete) {
                delay(55)
            }
            onLocationReceived(currentLocation.result)
        }
        catch (e: Exception) {
            onException()
        }
    }

}
