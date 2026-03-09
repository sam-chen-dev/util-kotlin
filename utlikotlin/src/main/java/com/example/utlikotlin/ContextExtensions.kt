package com.example.utlikotlin

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

fun Context.getConnectivityManager() = getSystemService(ConnectivityManager::class.java)

fun Context.getNotificationManager() = getSystemService(NotificationManager::class.java)

fun Context.cancelNotification() = getSystemService(NotificationManager::class.java).cancelAll()

fun Context.closeNotificationPanel() {
    val intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)

    sendBroadcast(intent)
}