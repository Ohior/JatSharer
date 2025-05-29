package jat.sharer.com.core

import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import jat.sharer.com.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

object ServerManager {
    val listenToServer = MutableStateFlow(false)
    private var serverEngine: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine. Configuration>? = null
    suspend fun startServer() {
        withContext(Dispatchers.IO) {
            if (serverEngine == null) {
                serverEngine = embeddedServer(
                    CIO,
                    port = Constants.PORT,
                    host = Constants.HOST
                ) {
                    configureRouting()
                }.apply {
                    start(wait = false) // Start without blocking
                }
                listenToServer.value = true
            }
        }
    }

    suspend fun stopServer() {
        withContext(Dispatchers.IO) {
            serverEngine?.stop(0, 0)
            serverEngine = null
            listenToServer.value = false
        }
    }
}