package jat.sharer.com.core

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get


object ClientManager {
    private val client = HttpClient(CIO)

    suspend fun triggerGetResponse(stringUrl: String) {
        try {
            client.get(stringUrl).body()
        } catch (e: Exception) {
            print("Error: ${e.message}")
        }
    }
}

