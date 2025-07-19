@file:OptIn(ExperimentalForeignApi::class)

package jat.sharer.com

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
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

actual fun getJeyFile(filePath: String): JeyFile {
    return IosJeyFile(filePath)
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun rememberScreenSize(): Pair<Int, Int> {
    val size = LocalWindowInfo.current
    return remember(size.containerSize) {
        Pair(size.containerSize.width, size.containerSize.height)
    }
}