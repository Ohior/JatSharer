package jat.sharer.com

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.readByteArray
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.net.Inet4Address
import java.net.NetworkInterface
import java.nio.file.Paths

@OptIn(ExperimentalComposeUiApi::class)
@androidx.compose.runtime.Composable
actual fun rememberScreenSize(): Pair<Int, Int> {
    val size = LocalWindowInfo.current
    return remember(size) {
        Pair(size.containerSize.width, size.containerSize.height)
    }
}

actual fun getJeyFile(filePath: String): JeyFile {
    return object : JeyFile(filePath) {
        private val file = File(filePath)
        override suspend fun fileExists(): Boolean = file.exists()

        override suspend fun downloadFile(data: ByteReadChannel): Boolean {
            return withContext(Dispatchers.IO) {
                try {
                    // Get the user's default downloads folder
                    val downloadsPath = Paths.get(System.getProperty("user.home"), "Downloads")
//                    val outputFile = downloadsPath.resolve("downloaded_file.bin").toFile()
                    val fileName = filePath.split("/").lastOrNull()?.takeIf { it.isNotBlank() } ?: "downloaded_file"
                    val outputFile = downloadsPath.resolve(fileName).toFile()

                    // Create output stream
                    outputFile.outputStream().use { outputStream ->
                        while (!data.isClosedForRead) {
                            val packet = data.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                            while (!packet.exhausted()) {
                                val bytes = packet.readByteArray()
                                outputStream.write(bytes)
                            }
                        }
                    }

                    true
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
        }


        override fun readBytes(): ByteArray = file.readBytes()

        override suspend fun readBytes(byteArray: suspend (ByteArray) -> Unit) {
            withContext(Dispatchers.IO) {
                byteArray(file.readBytes())
            }
        }

        override fun getFileInfo(): Map<FileInfo, String> {
            return mapOf(
                FileInfo.NAME to file.name,
                FileInfo.PATH to file.path,
                FileInfo.SIZE to file.length().toString(),
                FileInfo.LAST_MODIFIED to file.lastModified().toString(),
                FileInfo.HASH_ID to file.hashCode().toString()
            )
        }

        override fun byteChannel(): ByteWriteChannel = file.writeChannel()
    }
}

actual fun getPlatform(): Platform {
    return Platform.Desktop("desktop")
}

class DesktopFilePickerLauncher(
    private val trigger: () -> Unit
) : JFilePickerLauncher {
    override fun launch(allowedMimeTypes: List<String>) {
        trigger()
    }
}

@Composable
actual fun rememberJFilePicker(onResult: (List<JeyFile>) -> Unit): JFilePickerLauncher {
    val trigger = remember {
        {
            val fileDialog = FileDialog(null as Frame?, "Choose a File", FileDialog.LOAD).apply {
                isMultipleMode = true
                isVisible = true
            }
            val files = fileDialog.files?.map { getJeyFile(it.absolutePath) }?.toList() ?: emptyList()
            onResult(files)
        }
    }
    return DesktopFilePickerLauncher(trigger)
}

actual fun getDeviceIpAddress(): String? {
    return try {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        for (intf in interfaces) {
            val addresses = intf.inetAddresses
            for (addr in addresses) {
                if (!addr.isLoopbackAddress && addr is Inet4Address) {
                    return addr.hostAddress
                }
            }
        }
        null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

actual fun getHotspotManager(): HotspotManager {
    TODO("Not yet implemented")
}