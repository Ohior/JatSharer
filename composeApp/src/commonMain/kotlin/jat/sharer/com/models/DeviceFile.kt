package jat.sharer.com.models

import androidx.compose.ui.graphics.Color
import jat.sharer.com.utils.MediaType
import jat.sharer.com.utils.RequestAction
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class DeviceFile(
    val hashId: Int,
    val name: String,
    val requestAction:RequestAction,
    @Transient val fileColor: Color = Color.Black,
    val path: String?,
    val byteArray: ByteArray,
    val size: Long?
) {
    val mediaType: MediaType get() = MediaType.fromPath(name)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DeviceFile

        if (hashId != other.hashId) return false
        if (size != other.size) return false
        if (name != other.name) return false
        if (requestAction != other.requestAction) return false
        if (fileColor != other.fileColor) return false
        if (path != other.path) return false
        if (!byteArray.contentEquals(other.byteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hashId
        result = 31 * result + (size?.hashCode() ?: 0)
        result = 31 * result + name.hashCode()
        result = 31 * result + requestAction.hashCode()
        result = 31 * result + fileColor.hashCode()
        result = 31 * result + (path?.hashCode() ?: 0)
        result = 31 * result + byteArray.contentHashCode()
        return result
    }
}