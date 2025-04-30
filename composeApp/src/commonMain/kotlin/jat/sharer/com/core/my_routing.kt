package jat.sharer.com.core

import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.Application
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.header
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.utils.io.toByteArray
import io.ktor.utils.io.writeByteArray
import jat.sharer.com.FileInfo
import jat.sharer.com.getJeyFile
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


fun Application.configureRouting() {
    routing {
        get("/") {
            val htmlContent = generateFileSelectorPage()
            // Respond with the generated HTML
            call.respondText(htmlContent, ContentType.Text.Html)
        }
        // You might serve CSS separately
        get("/styles.css") {
            call.respondText(
                """
                     body { font-family: sans-serif; }
                     .content { color: blue; }
                     .readable { line-height: 1.6; }
                 """.trimIndent(), ContentType.Text.CSS
            )
        }
        post("/upload") {
            val multipartData = call.receiveMultipart()
            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val fileName = part.originalFileName ?: "unknown"
                        println("Filename : $fileName")
                        val bytes = part.provider().toByteArray()
                        // Save the file to disk or process it
                        getJeyFile(fileName).downloadFile(bytes)
                        println("Bytes : $bytes")
                    }

                    else -> Unit
                }
                part.dispose()
            }
            call.respondRedirect("/")
            call.respondText("Files uploaded successfully")
        }
        get("/show-files") {
            val files = DataStoreManager.getDeviceFiles()
            println("DEBUG : $files")
            val htmlContent = generateShowFilesPage(files)
            // Respond with the generated HTML
            call.respondText(htmlContent, ContentType.Text.Html)
        }
        get("/download/{filename}") {
            val filename = call.parameters["filename"]!!
            println("DEBUG1 : $filename")

            val deviceFile = DataStoreManager.getDeviceFiles()
            val jeyFile = deviceFile.firstNotNullOfOrNull {
                if (it.hashId == filename.toInt()) {
                    println("DEBUG2 : ${Json.encodeToString(it)}")
                    getJeyFile(it.path)
                } else null
            }

            println("DEBUG3 : $deviceFile")
            if (jeyFile != null) {
                println("DEBUG4 : ${jeyFile.getFileInfo()}")

                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(
                        ContentDisposition.Parameters.FileName,
                        jeyFile.getFileInfo()[FileInfo.NAME] ?: filename
                    ).toString()
                )

                call.respondBytesWriter(
                    contentType = ContentType.Application.OctetStream,
                    contentLength = jeyFile.getFileInfo()[FileInfo.SIZE]?.toLong() ?: 0L
                ) {
                    jeyFile.readBytes { bys ->
                        writeByteArray(bys)
                    }
                }

                DataStoreManager.deleteDeviceFile(deviceFile.first { it.hashId == filename.toInt() })
                println("DEBUG5: DONE")
                return@get  // Ensure it doesn’t fall through
            }

            call.respondRedirect("/")
        }

    }
}
