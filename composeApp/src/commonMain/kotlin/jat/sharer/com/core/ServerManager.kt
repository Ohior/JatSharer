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
    private var serverEngine: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? =
        null

    suspend fun startServer() {
        try {
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
        } catch (e: Exception) {
            println("Error starting server 2: $e")
            Constants.myHost.value = "http://${Constants.SAMSUNG_HOST}:${Constants.PORT}"
            withContext(Dispatchers.IO) {
                if (serverEngine == null) {
                    serverEngine = embeddedServer(
                        CIO,
                        port = Constants.PORT,
                        host = Constants.SAMSUNG_HOST
                    ) {
                        configureRouting()
                    }.apply {
                        start(wait = false) // Start without blocking
                    }
                    listenToServer.value = true
                }
            }

        } catch (e: Exception) {
            println("Error starting server: $e")
            withContext(Dispatchers.IO) {
                Constants.myHost.value = "http://${Constants.WILD_HOST}:${Constants.PORT}"
                if (serverEngine == null) {
                    serverEngine = embeddedServer(
                        CIO,
                        port = Constants.PORT,
                        host = Constants.WILD_HOST
                    ) {
                        configureRouting()
                    }.apply {
                        start(wait = false) // Start without blocking
                    }
                    listenToServer.value = true
                }
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
