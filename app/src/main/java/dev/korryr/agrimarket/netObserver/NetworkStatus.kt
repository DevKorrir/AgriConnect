package dev.korryr.agrimarket.netObserver

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

// NetworkStatus.kt
enum class NetworkStatus {
  Available,
  Losing,
  Lost,
  Unavailable
}

// ConnectivityObserver.kt
interface ConnectivityObserver {
  fun observe(): Flow<NetworkStatus>
}

class NetworkConnectivityObserver(
  private val context: Context
) : ConnectivityObserver {

  @SuppressLint("ServiceCast")
  override fun observe(): Flow<NetworkStatus> = callbackFlow {
    val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val callback = object : ConnectivityManager.NetworkCallback() {
      override fun onAvailable(network: Network) {
        trySend(NetworkStatus.Available)
      }

      override fun onLosing(network: Network, maxMsToLive: Int) {
        trySend(NetworkStatus.Losing)
      }

      override fun onLost(network: Network) {
        trySend(NetworkStatus.Lost)
      }

      override fun onUnavailable() {
        trySend(NetworkStatus.Unavailable)
      }
    }

    // Register for all network changes
    connectivityManager.registerDefaultNetworkCallback(callback)

    // Emit current state immediately
    val current = connectivityManager.activeNetworkInfo?.let {
      if (it.isConnected) NetworkStatus.Available else NetworkStatus.Unavailable
    } ?: NetworkStatus.Unavailable
    trySend(current)

    // Clean up when the flow collector is gone
    awaitClose {
      connectivityManager.unregisterNetworkCallback(callback)
    }
  }.distinctUntilChanged()
}
