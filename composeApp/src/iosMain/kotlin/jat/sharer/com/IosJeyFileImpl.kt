package jat.sharer.com


class IosJeyFileImpl() : JeyFileImpl {
    override suspend fun downloadFile(filePath: String, data: ByteArray): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun fileExists(filePath: String): Boolean {
        return true
    }

}