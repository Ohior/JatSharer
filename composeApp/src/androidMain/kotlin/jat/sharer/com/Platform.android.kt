package jat.sharer.com

import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath


actual fun getPlatform(): Platform = Platform.Android(
    name = "Android",
    version = Build.VERSION.SDK_INT.toString()
)

actual fun createDataStore(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { MainActivity.instance.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath.toPath() }
    )
}

actual fun getJeyFile(filePath: String): JeyFile {
    return AndroidJeyFile(MainActivity.instance, filePath)
}

