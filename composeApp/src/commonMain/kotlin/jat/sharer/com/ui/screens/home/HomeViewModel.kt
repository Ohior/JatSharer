package jat.sharer.com.ui.screens.home

//import jat.sharer.com.core.DataStoreManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import jat.sharer.com.PlatformFile
import jat.sharer.com.core.ClientManager
import jat.sharer.com.core.DataStoreManager
import jat.sharer.com.models.DeviceFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch


class HomeViewModel() : ScreenModel {
    //    val deviceFiles = DataStoreManager.getDeviceFileFlow()
    private var _deviceFiles = mutableStateListOf<DeviceFile>()
    val deviceFiles get() = _deviceFiles

    var infoPopup by mutableStateOf(false)
//    var startServer by mutableStateOf(false)

    //    fun deleteDeviceFiles(deviceFile: DeviceFile) {
//        screenModelScope.launch { DataStoreManager.deleteDeviceFile(deviceFile) }
//    }

    fun deleteDeviceFiles() {
        _deviceFiles.clear()
    }

    fun deleteDeviceFiles(deviceFile: DeviceFile) {
        _deviceFiles.remove(deviceFile)
        screenModelScope.launch(Dispatchers.IO) {
            DataStoreManager.deleteDeviceFile(deviceFile)
        }
    }


    fun makeDeviceFiles(files: List<PlatformFile>?) {
        if (files != null) {
            screenModelScope.launch {
                val df = files.map {
                    DeviceFile(
                        hashId = it.hashCode(),
                        name = it.name,
                        path = it.path!!,
                        size = it.size
                    )
                }
                _deviceFiles.addAll(df)
                DataStoreManager.saveDeviceFile(df)
            }
        }
    }

    fun downloadFile(deviceFile: DeviceFile) {
        screenModelScope.launch(Dispatchers.IO) {
            deviceFile.path?.let {
                ClientManager.triggerGetResponse("http://192.168.43.1:8181/download/${deviceFile.path}")
                deleteDeviceFiles(deviceFile)
            }
        }
    }
}