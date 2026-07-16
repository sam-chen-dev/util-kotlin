package com.example.utlikotlin.extensions.context

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Context.getConnectivityManager() = getSystemService(ConnectivityManager::class.java)

fun Context.getNotificationManager() = getSystemService(NotificationManager::class.java)

fun Context.cancelNotification() = getSystemService(NotificationManager::class.java).cancelAll()

fun Context.closeNotificationPanel() {
    val intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)

    sendBroadcast(intent)
}

suspend fun Context.readFileFromAssets(path: String): Result<String> = withContext(Dispatchers.IO) {
    try {
        val text = assets.open(path).bufferedReader().use { it.readText() }

        Result.success(text)
    } catch (e: Exception) {
        Log.e("readFileFromAssets()", "readFileFromAssets(path: String) failed", e)
        Result.failure(e)
    }
}