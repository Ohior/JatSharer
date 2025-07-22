package jat.sharer.com.models

sealed class ConnectionState {
    data class Success(val message: String): ConnectionState()
    data class Failed(val message: String): ConnectionState()
    object Load: ConnectionState()
    object None: ConnectionState()
}