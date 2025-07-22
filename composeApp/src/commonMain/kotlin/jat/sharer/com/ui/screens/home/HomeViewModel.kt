package jat.sharer.com.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import jat.sharer.com.JeyFile
import jat.sharer.com.core.SharedDataRepository
import jat.sharer.com.getHotspotManager
import jat.sharer.com.models.ConnectionState
import kotlinx.coroutines.flow.StateFlow


class HomeViewModel() : ScreenModel {
    // Private mutable state for internal use
//    private val _deviceFiles = mutableStateListOf<JeyFile>()
//
//    // Public read-only exposure of the state
//    val deviceFiles: List<JeyFile> get() = _deviceFiles
    val deviceFiles: StateFlow<List<JeyFile>> get() = SharedDataRepository.deviceFiles
    val hotspotManager = getHotspotManager()

    var infoPopup by mutableStateOf(false)
        private set
    var hotspotPopup by mutableStateOf<ConnectionState>(ConnectionState.None)

    fun deleteDeviceFiles(): Unit = SharedDataRepository.clearDeviceFiles()

    fun deleteDeviceFiles(deviceFile: JeyFile): Unit = SharedDataRepository.removeDeviceFiles(deviceFile)

    fun infoPopup(boolean: Boolean){
        infoPopup = boolean
        hotspotPopup = ConnectionState.None
    }

    fun makeDeviceFiles(files: List<JeyFile>?) {
        if (files != null) {
            SharedDataRepository.addDeviceFiles(files)
        }
    }
}