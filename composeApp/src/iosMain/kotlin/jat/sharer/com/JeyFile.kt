package jat.sharer.com


class IosJeyFile(private val filePath: String) : JeyFile(filePath) {
    override suspend fun downloadFile(data: ByteArray): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun fileExists(): Boolean {
        TODO("Not yet implemented")
    }

    override fun readBytes(): ByteArray {
        TODO("Not yet implemented")
    }

    override suspend fun readBytes(byteArray: suspend (ByteArray) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getFileInfo(): Map<FileInfo, String> {
        TODO("Not yet implemented")
    }
}