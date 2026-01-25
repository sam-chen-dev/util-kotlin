package com.example.utlikotlin

import android.content.BroadcastReceiver
import android.content.BroadcastReceiver.PendingResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
fun BroadcastReceiver.runAsync(action: suspend CoroutineScope.(PendingResult) -> Unit) {
    val pendingResult = goAsync()

    GlobalScope.launch(Dispatchers.IO) {
        try {
            action(pendingResult)
        } finally {
            pendingResult.finish()
        }
    }
}