package com.example.statusbartemp.Location

import android.location.Location
import javax.inject.Singleton

@Singleton
interface AppLocationInfa {
    suspend fun GetLocationString(latitude: Double, longitude: Double): String?

    suspend fun GetCurrentLocation(
        onLocationReceived: (Location) -> Unit,
        onException: () -> Unit,
        onPermissionsDenied: () -> Unit
    )


}
