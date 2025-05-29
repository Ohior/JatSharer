package jat.sharer.com

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel

sealed class Platform {
    data class Ios(val name: String, val version: String? = null) : Platform()
    data class Android(val name: String, val version: String? = null) : Platform()
}


expect fun getPlatform(): Platform

enum class FileInfo {
    NAME, PATH, SIZE, LAST_MODIFIED, HASH_ID
}

abstract class JeyFile(private val filePath: String) {
    abstract suspend fun fileExists(): Boolean
    abstract suspend fun downloadFile(data: ByteReadChannel): Boolean
    abstract fun readBytes(): ByteArray
    abstract suspend fun readBytes(byteArray: suspend (ByteArray) -> Unit)
    abstract fun getFileInfo(): Map<FileInfo, String>
    abstract fun byteChannel(): ByteWriteChannel


}

expect fun getJeyFile(filePath: String): JeyFile

internal const val DATA_STORE_FILE_NAME = "prefs.preferences_pb"

expect fun createDataStore(): DataStore<Preferences>

interface JFilePickerLauncher {
    fun launch(allowedMimeTypes: List<String> = listOf("*/*"))
}

@Composable
expect fun rememberJFilePicker(onResult: (List<JeyFile>) -> Unit): JFilePickerLauncher