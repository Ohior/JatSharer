package jat.sharer.com.ui.screens.home

import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PlatformFiles
import io.github.vinceglb.filekit.core.pickFile
import jat.sharer.com.JeyFile
import jat.sharer.com.core.DataStoreManager
import jat.sharer.com.core.ServerManager
import jat.sharer.com.models.DeviceFile
import jat.sharer.com.utils.RequestAction
import kotlinx.coroutines.launch


class HomeViewModel() : ScreenModel {
    val deviceFiles = DataStoreManager.getDeviceFileFlow()

    fun deleteDeviceFiles(deviceFile: DeviceFile) {
        screenModelScope.launch { DataStoreManager.deleteDeviceFile(deviceFile) }
    }

    fun makeDeviceFiles(files: PlatformFiles?) {
        if (files != null) {
            screenModelScope.launch {
            val df =  files.map {
                DeviceFile(
                    hashId = it.hashCode(),
                    name = it.name,
                    path = it.path,
                    size = it.getSize(),
                    requestAction = RequestAction.SEND,
                    byteArray = it.readBytes(),
                    fileColor = Color.Blue
                )
            }
                DataStoreManager.saveDeviceFile(df) }
        }
    }

    fun pickFile(){
        screenModelScope.launch {
            val name = FileKit.pickFile()
            println("FILE PICKER $name")
        }
    }

//    private val _deviceFiles = MutableStateFlow<List<DeviceFile>>(emptyList())
//    val deviceFiles: StateFlow<List<DeviceFile>> = _deviceFiles.asStateFlow()
//
//
//    fun deleteDeviceFiles(deviceFile: DeviceFile) {
//        _deviceFiles.update { df ->
//            df.filter { it != deviceFile }
//        }
//    }
//
//    fun makeDeviceFiles(files: PlatformFiles?) {
//        if (files != null) {
//            _deviceFiles.update { df ->
//                df + files.map {
//                    DeviceFile(
//                        hashId = it.hashCode(),
//                        name = it.name,
//                        path = it.path,
//                        size = it.getSize(),
//                        fileColor = Color.Blue
//                    )
//                }
//            }
//        }
//    }
}