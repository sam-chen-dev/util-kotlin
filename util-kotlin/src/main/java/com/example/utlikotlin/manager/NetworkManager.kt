package com.example.utlikotlin.manager

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class NetworkManager(context: Context) {
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

    val isConnected: Flow<Boolean> = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                trySend(capabilities.isConnectedToInternet())
            }

            override fun onLost(network: Network) {
                trySend(false)
            }
        }

        val isConnected = connectivityManager.activeNetwork
            ?.let { activeNetwork -> connectivityManager.getNetworkCapabilities(activeNetwork) }
            ?.isConnectedToInternet()
            ?: false

        trySend(isConnected)

        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        awaitClose { connectivityManager.unregisterNetworkCallback(networkCallback) }
    }.distinctUntilChanged()

    private fun NetworkCapabilities.isConnectedToInternet(): Boolean {
        val hasInternetCapability = hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val isInternetValidated = hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        return hasInternetCapability && isInternetValidated
    }
}