package com.example.utlikotlin

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Service

fun Service.isAppInForeground(): Boolean {
    val runningProcessInfo = RunningAppProcessInfo()

    ActivityManager.getMyMemoryState(runningProcessInfo)

    return runningProcessInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
}