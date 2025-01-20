package jat.sharer.com.core

import io.github.vinceglb.filekit.core.FileKit
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import jat.sharer.com.JeyFile
import jat.sharer.com.Platform
import jat.sharer.com.getPlatform


fun Route.downloadRoute() {
    post("/upload") {
        call.respondRedirect("/")
        call.respondText("Files uploaded successfully")
        val multipartData = call.receiveMultipart()
        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    val fileName = part.originalFileName ?: "unknown"
                    println("Filename : $fileName")
                    val bytes = part.provider().toByteArray()
                    // Save the file to disk or process it
                    when (getPlatform()) {
                        is Platform.Android -> {
                            JeyFile(fileName).apply {
                                downloadFile(bytes)
                            }
                        }

                        is Platform.Ios -> {
                            val f = fileName.split(".", limit = 2)
                            FileKit.saveFile(bytes, f[0], f[1])
                        }
                    }
                    println("Bytes : $bytes")
                }

                else -> Unit
            }
            part.dispose()
        }
    }
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText(contentType = ContentType.Text.Html) {
                HtmlTemplate.myHtmlPage(
                    contents = listOf(HtmlTemplate.selectFileForm()),
                    details = DataStoreManager.getDeviceFiles().map { m1 ->
                        HtmlTemplate.fileItem(
                            title = m1.name,
                            description = m1.path ?: "No Path",
                            urlPath = "${m1.hashId}",
                        )
                    }
                )
            }
        }
        downloadRoute()
        get("/{hashid}") {
            val filename = call.parameters["hashid"]!!
            val deviceFile =
                DataStoreManager.getDeviceFiles().find { it.hashId == filename.toInt() }
            // get filename from request url
            // construct reference to file
            // ideally this would use a different filename
            deviceFile?.path?.let {
//                val file = JeyFile(deviceFile.path)
//                if (file.fileExists()) {
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(
                        ContentDisposition.Parameters.FileName,
                        deviceFile.name
                    ).toString()
                )
                call.respondBytes(deviceFile.byteArray)
//                }
            }
            call.respondRedirect("/")
//            call.respond(HttpStatusCode.NotFound)
        }
    }
}
