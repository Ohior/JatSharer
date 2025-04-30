package jat.sharer.com.core

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import jat.sharer.com.createDataStore
import jat.sharer.com.models.DeviceFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


object DataStoreManager {
    private lateinit var preferences: DataStore<Preferences>
    private val deviceFileKey = stringPreferencesKey("DEVICE_FILE")

    fun initializeDataStore() {
        preferences = createDataStore()
    }


    suspend fun saveDeviceFile(deviceFiles: List<DeviceFile>) {
        preferences.edit { ds ->
            ds[deviceFileKey] = Json.encodeToString(deviceFiles)
        }
    }

    suspend fun getDeviceFiles(): List<DeviceFile> {
        var df = listOf<DeviceFile>()
        preferences.edit { ds ->
            df = Json.decodeFromString<List<DeviceFile>>(ds[deviceFileKey] ?: "[]")
        }
        return df
    }

    suspend fun deleteDeviceFile(deviceFile: DeviceFile) {
        var d :List<DeviceFile>?= null
        preferences.edit { ds ->
            val data = ds[deviceFileKey]?.let { Json.decodeFromString<List<DeviceFile>>(it) }
            if (data != null) {
                 d = data.filter { it.path != deviceFile.path }
            }
        }
        d?.let { saveDeviceFile(it) }
    }

    fun getDeviceFileFlow(): Flow<List<DeviceFile>> {
        return preferences
            .data
            .map {
                val df = it[deviceFileKey] ?: "[]"
                Json.decodeFromString<List<DeviceFile>>(df)
            }
    }

    suspend fun deleteAllDeviceFile() {
        preferences.edit { pf -> pf.remove(deviceFileKey) }
    }
}