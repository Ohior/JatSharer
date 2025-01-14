@file:OptIn(ExperimentalForeignApi::class)

package jat.sharer.com

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIDevice


actual fun getPlatform(): Platform = Platform.Ios(
    name = UIDevice.currentDevice.systemName(),
    version = UIDevice.currentDevice.systemVersion
)

actual fun getJeyFileImpl(): JeyFileImpl {
    return IosJeyFileImpl()
}

actual fun createDataStore(): DataStore<Preferences> {
    val directory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            val p = requireNotNull(directory).path + "/$DATA_STORE_FILE_NAME"
            p.toPath()
        }
    )
}