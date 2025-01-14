package jat.sharer.com

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.net.URL

class AndroidJeyFileImpl(private val context: Context) : JeyFileImpl {
    override suspend fun downloadFile(filePath: String, data: ByteArray): Boolean {
        withContext(Dispatchers.IO) {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, filePath)
            FileOutputStream(file).use { outputStream ->
                outputStream.write(data)
                outputStream.flush()
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "File saved successfully${file.absoluteFile}", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }

    override suspend fun fileExists(filePath: String): Boolean {
        return File(filePath).exists()
    }

//    override suspend fun getByteArray(urlPath: String): ByteArray {
//        return if (urlPath.startsWith("http") || urlPath.startsWith("https"))
//            URL(urlPath).readBytes()
//        else {
//            val file = File(urlPath)
//            file.readBytes()
//        }
//    }
}
