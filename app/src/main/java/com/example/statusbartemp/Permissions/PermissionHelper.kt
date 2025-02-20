package com.example.statusbartemp.Permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.statusbartemp.LogicAndData.Constants.Companion.backGroundLocationPermissions
import com.example.statusbartemp.LogicAndData.Constants.Companion.basicPermissions
import com.example.statusbartemp.LogicAndData.Constants.Companion.locationPermissions
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class PermissionHelper(context: Context, activity : Activity)  {
    val context = context
    val activity = activity
    private val rqCode: Int = Random.nextInt(100, 500)

    fun requestPermissions(requireLocation : Boolean) {
        val permissions = when(requireLocation){
            false -> basicPermissions
            true -> basicPermissions+locationPermissions
        }
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (activity != null) {
                    ActivityCompat.requestPermissions(
                        activity,
                        permissions,
                        rqCode
                    )
                }
                break
            }
        }
    }

    suspend fun checkAndRequestPermissions(requireLocation : Boolean)  {
        var deniedPermissions: String = ""
        val permissions = when(requireLocation){
            false -> basicPermissions
            true -> basicPermissions+locationPermissions+backGroundLocationPermissions
        }
        for (permission in permissions){
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                deniedPermissions += permission
        }}
        if (deniedPermissions != ""){
            requestPermissions(requireLocation)
        }
    }

    suspend fun checkPermissions(requireLocation : Boolean) : Boolean {
        var deniedPermissions: String = ""
        for (permission in basicPermissions){
            if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_DENIED) {
                deniedPermissions += permission
            }
        }
        if (requireLocation) {
            for (permission in locationPermissions) {
                if (ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_DENIED) {
                    deniedPermissions += permission
                }
            }
            for (permission in backGroundLocationPermissions) {
                if (ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_DENIED) {
                    deniedPermissions += permission
                }
            }
        }
        return deniedPermissions == ""
    }

    suspend fun checkNormalPermissions(requireLocation: Boolean) : Boolean {
        var deniedPermissions: String = ""
        val permissions = when(requireLocation){
            false -> basicPermissions
            true -> basicPermissions+locationPermissions
        }
        for (permission in permissions){
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                deniedPermissions += permission
            }
        }
        return deniedPermissions == ""
    }

    suspend fun checkAndRequestBackGroundLocationPermission(){
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_DENIED) {
            requestBackGroundLocationPermission()
        }
    }

    fun requestBackGroundLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (activity != null) {
                ActivityCompat.requestPermissions(
                    activity,
                    backGroundLocationPermissions,
                    rqCode
                )
            }
        }
    }
}


suspend fun initializePermissionHelper(
    context : Context,
    activity : Activity,
    requireLocation : Boolean
) {
    val permissionHelper = PermissionHelper(context, activity)
    permissionHelper.checkAndRequestPermissions(requireLocation)
}

suspend fun obtainBackGroundPermissions(
    context : Context,
    activity : Activity
) {
    val permissionHelper = PermissionHelper(context, activity)
    permissionHelper.checkAndRequestBackGroundLocationPermission()
}

suspend fun checkPermissions(
    context : Context,
    activity : Activity,
    requireLocation : Boolean
) : Boolean {
    val permissionHelper = PermissionHelper(context, activity)
    return permissionHelper.checkPermissions(requireLocation)
}

suspend fun checkNormalPermissions(
    context : Context,
    activity : Activity,
    requireLocation : Boolean
) : Boolean {
    val permissionHelper = PermissionHelper(context, activity)
    return permissionHelper.checkNormalPermissions(requireLocation)
}
