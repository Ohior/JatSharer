package jat.sharer.com.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import jat.sharer.com.JeyFile
import jat.sharer.com.core.SharedDataRepository
import kotlinx.coroutines.flow.StateFlow


class HomeViewModel() : ScreenModel {
    // Private mutable state for internal use
//    private val _deviceFiles = mutableStateListOf<JeyFile>()
//
//    // Public read-only exposure of the state
//    val deviceFiles: List<JeyFile> get() = _deviceFiles
    val deviceFiles: StateFlow<List<JeyFile>> get() = SharedDataRepository.deviceFiles


    var infoPopup by mutableStateOf(false)

    fun deleteDeviceFiles(): Unit = SharedDataRepository.clearDeviceFiles()

    fun deleteDeviceFiles(deviceFile: JeyFile): Unit = SharedDataRepository.removeDeviceFiles(deviceFile)


    fun makeDeviceFiles(files: List<JeyFile>?) {
        if (files != null) {
            SharedDataRepository.addDeviceFiles(files)
        }
    }
}