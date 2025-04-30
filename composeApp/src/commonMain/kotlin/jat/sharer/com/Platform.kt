package jat.sharer.com

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

sealed class Platform {
    data class Ios(val name: String, val version: String? = null) : Platform()
    data class Android(val name: String, val version: String? = null) : Platform()
}


expect fun getPlatform(): Platform

enum class FileInfo {
    NAME, PATH, SIZE, LAST_MODIFIED
}

abstract class JeyFile(private val filePath: String) {
    abstract suspend fun fileExists(): Boolean
    abstract suspend fun downloadFile(data: ByteArray): Boolean
    abstract fun readBytes(): ByteArray
    abstract suspend fun readBytes(byteArray: suspend (ByteArray)-> Unit)
    abstract fun getFileInfo(): Map<FileInfo, String>

}

expect fun getJeyFile(filePath: String): JeyFile

internal const val DATA_STORE_FILE_NAME = "prefs.preferences_pb"

expect fun createDataStore(): DataStore<Preferences>
//expect fun createDataStore(producePath: () -> String): DataStore<Preferences>
//{
//    return PreferenceDataStoreFactory.createWithPath(
//        produceFile = { producePath().toPath() }
//    )
//}
