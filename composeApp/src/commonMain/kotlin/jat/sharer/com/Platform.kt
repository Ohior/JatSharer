package jat.sharer.com

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import io.ktor.utils.io.*
import okio.Path.Companion.toPath

sealed class Platform {
    data class Ios(val name: String, val version: String? = null) : Platform()
    data class Android(val name: String, val version: String? = null) : Platform()
}

interface JeyFileImpl {
    suspend fun downloadFile(filePath: String, data: ByteArray): Boolean
    suspend fun fileExists(filePath: String): Boolean
}


expect fun getPlatform(): Platform

expect fun getJeyFileImpl(): JeyFileImpl

internal const val DATA_STORE_FILE_NAME = "prefs.preferences_pb"
expect fun createDataStore(): DataStore<Preferences>
//expect fun createDataStore(producePath: () -> String): DataStore<Preferences>
//{
//    return PreferenceDataStoreFactory.createWithPath(
//        produceFile = { producePath().toPath() }
//    )
//}
