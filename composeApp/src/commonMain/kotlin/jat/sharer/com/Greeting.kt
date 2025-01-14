package jat.sharer.com

class JeyFile(private val filePath: String) {
    private val jeyFileImpl = getJeyFileImpl()
    suspend fun fileExists(): Boolean = jeyFileImpl.fileExists(filePath)
    suspend fun downloadFile(byteArray: ByteArray) = jeyFileImpl.downloadFile(filePath, byteArray)
}