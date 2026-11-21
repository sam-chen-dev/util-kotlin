package com.example.utlikotlin.extensions.permissions

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

@RequiresApi(Build.VERSION_CODES.S)
fun Context.isBluetoothPermissionGranted(): Boolean {
    val result = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)

    return result == PackageManager.PERMISSION_GRANTED
}

@RequiresApi(Build.VERSION_CODES.S)
fun ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>.launchBluetoothPermission() {
    launch(
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    )
}

fun Context.isBluetoothEnabled(): Boolean {
    val bluetoothManager = getSystemService(BluetoothManager::class.java)

    return bluetoothManager.adapter.isEnabled
}

fun ManagedActivityResultLauncher<Intent, ActivityResult>.launchEnableBluetooth() {
    val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

    launch(intent)
}