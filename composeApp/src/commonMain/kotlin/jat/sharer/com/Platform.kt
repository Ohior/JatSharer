package jat.sharer.com

import androidx.compose.runtime.Composable
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow

sealed class Platform {
    data class Ios(val name: String, val version: String? = null) : Platform()
    data class Android(val name: String, val version: String? = null) : Platform()
    data class Desktop(val name: String, val version: String? = null) : Platform()
}


expect fun getPlatform(): Platform

enum class FileInfo {
    NAME, PATH, SIZE, LAST_MODIFIED, HASH_ID
}

interface JFilePickerLauncher {
    fun launch(allowedMimeTypes: List<String> = listOf("*/*"))
}


abstract class JeyFile(private val filePath: String) {
    abstract suspend fun fileExists(): Boolean
    abstract suspend fun downloadFile(data: ByteReadChannel): Boolean
    abstract fun readBytes(): ByteArray
    abstract suspend fun readBytes(byteArray: suspend (ByteArray) -> Unit)
    abstract fun getFileInfo(): Map<FileInfo, String>
    abstract fun byteChannel(): ByteWriteChannel
}

interface HotspotManager{
    fun isHotspotOn(): Flow<Boolean>
    fun enableHotspot()
}
expect fun getJeyFile(filePath: String): JeyFile


@Composable
expect fun rememberJFilePicker(onResult: (List<JeyFile>) -> Unit): JFilePickerLauncher

@Composable
expect fun rememberScreenSize():Pair<Int, Int>

expect fun getHotspotManager(): HotspotManager