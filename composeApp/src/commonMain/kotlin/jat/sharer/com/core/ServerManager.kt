package jat.sharer.com.core

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import jat.sharer.com.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow

object ServerManager {
    val listenToServer = MutableStateFlow(false)
    var uploadRoute = mutableStateOf("/")
    private var serverEngine: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine. Configuration>? = null
    fun startServer() {
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

    fun stopServer() {
        serverEngine?.stop(0, 0)
        serverEngine = null
        listenToServer.value = false
    }
}
//
//fun Route.homeRoute() {
//    get("/") {
//        call.respondText(contentType = ContentType.Text.Html) {
//            HtmlTemplate.myHtmlPage(
//                contents = listOf(
//                    HtmlTemplate.tagButton(),
//                    HtmlTemplate.selectFileForm(),
//                    HtmlTemplate.tagButton(),
//                )
//            )
//        }
//    }
//}
//
//fun Route.downloadRoute(paths:List<String>) {
//    get("/") {
//        val file = getFileSaver.saveFile("files/sample.pdf") // Change path as needed
//        if (file.exists()) {
//            call.respondBytes (file)
//        } else {
//            call.respondText("File not found", status = io.ktor.http.HttpStatusCode.NotFound)
//        }
//    }
//}