package jat.sharer.com.core

import jat.sharer.com.JeyFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SharedDataRepository {
    // Private mutable state flow for internal updates
    private val _deviceFiles = MutableStateFlow<List<JeyFile>>(emptyList())

    // Public read-only flow to observe the list
    val deviceFiles: StateFlow<List<JeyFile>> = _deviceFiles.asStateFlow()

    // Method to add files (avoiding duplicates)
    fun addDeviceFiles(files: List<JeyFile>) {
        val currentFiles = _deviceFiles.value.toMutableList()
        val newFiles = files.filterNot { currentFiles.contains(it) }
        currentFiles.addAll(newFiles)
        _deviceFiles.value = currentFiles
    }

    // Optional: Method to clear or remove files
    fun clearDeviceFiles() {
        _deviceFiles.value = emptyList()
    }

    fun removeDeviceFiles(file: JeyFile) {
        val currentFiles = _deviceFiles.value.toMutableList()
        currentFiles.remove(file)
        _deviceFiles.value = currentFiles
    }

}

