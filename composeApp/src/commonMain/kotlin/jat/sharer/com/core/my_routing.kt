package jat.sharer.com.core

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import jat.sharer.com.FileInfo
import jat.sharer.com.JeyFile
import jat.sharer.com.getJeyFile


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
                if (part is PartData.FileItem) {
                    val fileName = part.originalFileName ?: "unknown"
                    println("Received file: $fileName")
                    getJeyFile(fileName).downloadFile(part.provider())
                }
                part.dispose()
            }
        }

        post("/upload/{filename}") {
            val filename = call.parameters["filename"] ?: return@post call.respond(HttpStatusCode.BadRequest, null)
            println("Receiving file: $filename")

            val file = getJeyFile(filename)

            call.receiveChannel().copyAndClose(file.byteChannel())
            call.respondRedirect("/")

            println("Upload of $filename completed")
        }


        get("/show-files") {
            val files = SharedDataRepository.deviceFiles.value
            println("DEBUG : ${files.toList<JeyFile>()}")
            val htmlContent = generateShowFilesPage(files)
            // Respond with the generated HTML
            call.respondText(htmlContent, ContentType.Text.Html)
        }
        get("/download/{filename}") {
            val filehash = call.parameters["filename"]!!
            println("DEBUG1 : $filehash")

            val deviceFile = SharedDataRepository.deviceFiles.value //DataStoreManager.getDeviceFiles()
            println("DEBUG2 :$filehash \n ${deviceFile[0].getFileInfo()}")
            val jeyFile = deviceFile.find { it.getFileInfo()[FileInfo.HASH_ID]!!.toInt() == filehash.toInt() }

            println("DEBUG3 : $jeyFile")
            if (jeyFile != null) {
                SharedDataRepository.removeDeviceFiles(jeyFile)
                println("DEBUG4 : ${jeyFile.getFileInfo()}")

                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(
                        ContentDisposition.Parameters.FileName,
                        jeyFile.getFileInfo()[FileInfo.NAME] ?: "title_unknown"
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
                println("DEBUG5: DONE")
            }
            call.respondRedirect("/")
        }
    }
}
