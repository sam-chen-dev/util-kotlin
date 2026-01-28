package com.example.utlikotlin

import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration

fun AndroidViewModel.getConnectivityManager(): ConnectivityManager {
    return (getApplication() as Context).getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
}

fun AndroidViewModel.getContentResolver() = (getApplication() as Context).contentResolver

fun AndroidViewModel.getAssets() = (getApplication() as Context).assets

fun AndroidViewModel.getInteger(resId: Int) = (getApplication() as Context).resources.getInteger(resId)

fun AndroidViewModel.getString(resId: Int) = (getApplication() as Context).getString(resId)

fun AndroidViewModel.getString(resId: Int, vararg formatArgs: Any) = (getApplication() as Context).getString(resId, *formatArgs)

fun AndroidViewModel.showToast(text: String) = Toast.makeText(getApplication(), text, Toast.LENGTH_SHORT).show()

fun AndroidViewModel.showToast(resId: Int) = Toast.makeText(getApplication(), resId, Toast.LENGTH_SHORT).show()

fun AndroidViewModel.showToastLong(text: String) = Toast.makeText(getApplication(), text, Toast.LENGTH_LONG).show()

fun AndroidViewModel.showToastLong(resId: Int) = Toast.makeText(getApplication(), resId, Toast.LENGTH_LONG).show()

fun AndroidViewModel.getRawUri(resId: Int) = "android.resource://${(getApplication() as Context).packageName}/$resId".toUri()

fun AndroidViewModel.getRawUris(arrayResId: Int): List<Uri> {
    val rawUris = mutableListOf<Uri>()
    val raws = (getApplication() as Context).resources.obtainTypedArray(arrayResId)

    for (index in 0 until raws.length()) {
        val resourceId = raws.getResourceId(index, 0)

        rawUris.add(getRawUri(resourceId))
    }

    raws.recycle()

    return rawUris
}

suspend fun AndroidViewModel.readFileFromAssets(path: String): String? {
    try {
        return getAssets().open(path).bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        Log.e("readFileFromAssets()", "Error: [${e.javaClass.simpleName}]: ${e.message.toString()}")
        return null
    }
}

suspend fun AndroidViewModel.countDown(duration: Duration, action: () -> Unit) {
    delay(duration.inWholeMilliseconds)

    action()
}