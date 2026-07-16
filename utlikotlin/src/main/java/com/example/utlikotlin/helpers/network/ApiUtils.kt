package com.example.utlikotlin.helpers.network

import android.util.Log
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

suspend fun <T> retryWithBackoff(
    maxAttempts: Int = 3,
    initialDelayInMillis: Long = 4000,
    factor: Double = 2.0,
    block: suspend () -> T
): T {
    var currentDelay = initialDelayInMillis

    repeat(maxAttempts - 1) {
        try {
            return block()
        } catch (e: Exception) {
            Log.e(block.toString(), "$block failed", e)

            delay(currentDelay.milliseconds)

            currentDelay = (currentDelay * factor).toLong()
        }
    }

    return block()
}