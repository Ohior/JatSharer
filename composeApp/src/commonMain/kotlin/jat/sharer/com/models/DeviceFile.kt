package jat.sharer.com.models

import jat.sharer.com.utils.MediaType
import kotlinx.serialization.Serializable

@Serializable
data class DeviceFile(
    val hashId: Int,
    val name: String,
    val path: String,
    val size: Long
) {
    val mediaType: MediaType get() = MediaType.fromPath(name)
}