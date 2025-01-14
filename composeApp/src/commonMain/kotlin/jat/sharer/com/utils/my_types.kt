package jat.sharer.com.utils


sealed class MediaType {
    data object Image : MediaType()
    data object Video : MediaType()
    data object Audio : MediaType()
    data object Document : MediaType()
    data object Unknown : MediaType()

    fun getExtension(): String = when (this) {
        is Image -> "jpg,jpeg,png,gif,webp"
        is Video -> "mp4,mkv,mov,avi,webm"
        is Audio -> "mp3,wav,ogg,m4a"
        is Document -> "pdf,doc,docx,txt,xlsx"
        is Unknown -> "*"
    }

    companion object {
        fun fromExtension(extension: String): MediaType {
            return when (extension.lowercase()) {
                in Image.getExtension().split(",") -> Image
                in Video.getExtension().split(",") -> Video
                in Audio.getExtension().split(",") -> Audio
                in Document.getExtension().split(",") -> Document
                else -> Unknown
            }
        }

        fun fromPath(path: String): MediaType {
            val extension = path.substringAfterLast('.', "")
            return fromExtension(extension)
        }
    }
}

enum class RequestAction { SEND, RECIEVE, NONE }
