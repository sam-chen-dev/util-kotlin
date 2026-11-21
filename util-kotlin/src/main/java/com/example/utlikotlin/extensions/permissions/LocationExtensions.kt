package com.example.utlikotlin.extensions.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

fun Context.isLocationPermissionGranted(): Boolean {
    val result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

    return result == PackageManager.PERMISSION_GRANTED
}

fun ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>.launchLocationPermission() {
    launch(
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )
}

fun Context.isGpsEnabled(): Boolean {
    val locationManager = getSystemService(LocationManager::class.java)

    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>.launchEnableGps(context: Context) {
    val locationRequest = LocationRequest.Builder(1000L).run {
        setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        build()
    }

    val settingsRequest = LocationSettingsRequest.Builder().run {
        addLocationRequest(locationRequest)
        build()
    }

    val settingsClient = LocationServices.getSettingsClient(context)
    val task = settingsClient.checkLocationSettings(settingsRequest)

    task.addOnFailureListener {
        val intentSender = (it as ResolvableApiException).resolution.intentSender
        val intentSenderRequest = IntentSenderRequest.Builder(intentSender).build()

        launch(intentSenderRequest)
    }
}