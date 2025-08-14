package jat.sharer.com.utils

import androidx.compose.runtime.mutableStateOf
import jat.sharer.com.getDeviceIpAddress

object Constants {
    const val PORT = 8181
    val HOST = getDeviceIpAddress() ?: "192.168.43.1" // For device

    //    const val HOST = "10.0.2.2" // For emulator
    val HOWTO = "👉🏽 Switch on your hotspot \n" +
            " 👉🏽 Connect external device to your hotspot \n" +
            " 👉🏽 Click on Select Files\n" +
            " 👉🏽 Click on Start server \n" +
            " 👉🏽 Connect to http://$HOST:$PORT in your PC Browser \n" +
            " 👉🏽 Navigate browser items"

    val myHost = mutableStateOf("http://${HOST}:${PORT}")
}