package jat.sharer.com

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.net.toUri
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AndroidJeyFile(
    private val localContext: Context,
    private val filePath: String
) : JeyFile(filePath) {
    override fun byteChannel(): ByteWriteChannel {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val file = File(downloadsDir, filePath).apply {
            parentFile?.mkdirs() // Create parent directories if needed
        }
        return file.writeChannel()
    }

    override suspend fun downloadFile(data: ByteReadChannel): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val downloadsDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                val file = File(downloadsDir, filePath)
                file.outputStream().use { outputStream ->
                    // Read from the ByteReadChannel and write to file in chunks
                    val buffer = ByteArray(8 * 1024)
                    while (!data.isClosedForRead) {
                        val bytesRead = data.readAvailable(buffer)
                        if (bytesRead == -1) break
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    outputStream.flush()
                }
                // Show success notification on main thread
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        MainActivity.instance,
                        "File saved to ${file.absolutePath}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                true
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        MainActivity.instance,
                        "Download failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                false
            }
        }
    }

    override suspend fun fileExists(): Boolean {
        return File(filePath).exists()
    }

    private fun getBytesFromUri(context: Context, uriPath: String): ByteArray? {
        val contentResolver = context.contentResolver
        return try {
            contentResolver.openInputStream(uriPath.toUri())?.use { input ->
                val buffer = ByteArrayOutputStream()
                val data = ByteArray(4096)  // 4KB chunks
                var nRead: Int
                while (input.read(data, 0, data.size).also { nRead = it } != -1) {
                    buffer.write(data, 0, nRead)
                }
                buffer.toByteArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun readBytes(): ByteArray {
        return if (filePath.startsWith("http") || filePath.startsWith("https"))
            URL(filePath).readBytes()
        else {
            getBytesFromUri(localContext, filePath) ?: byteArrayOf()
        }
    }

    override suspend fun readBytes(byteArray: suspend (ByteArray) -> Unit) {
        if (filePath.startsWith("http") || filePath.startsWith("https")) {
            withContext(Dispatchers.IO) {
                try {
                    URL(filePath).openStream().use { inputStream ->
                        val buffer = ByteArray(4096) // 4KB chunks
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            val chunk =
                                buffer.copyOf(bytesRead) // Create a new array with the actual bytes read
                            byteArray(chunk) // Process each chunk
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            val contentResolver = localContext.contentResolver
            try {
                contentResolver.openInputStream(filePath.toUri())?.use { input ->
                    val buffer = ByteArray(1024 * 10) // 1MB chunks
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        val chunk =
                            buffer.copyOf(bytesRead) // Create a new array with the actual bytes read
                        byteArray(chunk) // Process each chunk
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

//        if (filePath.startsWith("http") || filePath.startsWith("https"))
//            byteArray(URL(filePath).readBytes())
//        else {
//            val contentResolver = localContext.contentResolver
//            try {
//                contentResolver.openInputStream (filePath.toUri())?.use { input ->
//                    val buffer = ByteArrayOutputStream()
//                    val data = ByteArray(4096)  // 4KB chunks
//                    var nRead: Int
//                    while (input.read(data, 0, data.size).also { nRead = it } != -1) {
//                        buffer.write(data, 0, nRead)
//                    }
//                    byteArray(buffer.toByteArray())
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                null
//            }
//        }
    }

    override fun getFileInfo(): Map<FileInfo, String> {
        val contentResolver = localContext.contentResolver
        val projection = arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATA, // For the file path (may not always be available)
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_MODIFIED
        )

        contentResolver.query(filePath.toUri(), projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val pathIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
                val nameIndex = runCatching { cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME) }
                val sizeIndex = runCatching { cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE) }
                val modifiedIndex = runCatching { cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED) }

                val path = cursor.getString(pathIndex) ?: getPathFromUriApi19(
                    localContext,
                    filePath.toUri()
                ) // Try another way for API 19+
                val name = cursor.getString(nameIndex.getOrDefault(0))
                val filehash = path.hashCode()
                val size = cursor.getLong(sizeIndex.getOrDefault(0)).toString()
                val lastModifiedMillis = cursor.getLong(modifiedIndex.getOrDefault(0)) * 1000L // Convert seconds to milliseconds
                val lastModified =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                        Date(lastModifiedMillis)
                    )

                return mapOf(
                    FileInfo.NAME to name,
                    FileInfo.PATH to (path ?: "N/A"),
                    FileInfo.SIZE to size,
                    FileInfo.LAST_MODIFIED to lastModified,
                    FileInfo.HASH_ID to filehash.toString()
                )
            }
        }
        return emptyMap()
    }

    // Helper function for getting the actual path for certain Uri types on API 19+
// (This is a simplified version; a more robust implementation might be needed)
    private fun getPathFromUriApi19(context: Context, uri: Uri): String? {
        if (DocumentsContractWrapper.isDocumentUri(context, uri)) {
            val documentId = DocumentsContractWrapper.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri.authority) {
                val id = documentId?.split(":")[1]
                val selection = MediaStore.Images.Media._ID + "=?"
                val selectionArgs = arrayOf(id)
                context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Images.Media.DATA),
                    selection,
                    selectionArgs,
                    null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                    }
                }
                context.contentResolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Video.Media.DATA),
                    selection,
                    selectionArgs,
                    null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                    }
                }
                context.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Audio.Media.DATA),
                    selection,
                    selectionArgs,
                    null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                    }
                }
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                try {
                    val contentUri = ContentUris.withAppendedId(
                        "content://downloads/public_downloads".toUri(),
                        java.lang.Long.valueOf(documentId)
                    )
                    context.contentResolver.query(
                        contentUri,
                        arrayOf(MediaStore.MediaColumns.DATA),
                        null,
                        null,
                        null
                    )?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
                        }
                    }
                } catch (e: NumberFormatException) {
                    // Handle potential NumberFormatException if documentId is not a valid long
                    e.printStackTrace()
                }
            } else if ("com.google.android.apps.photos.content" == uri.authority) {
                // Google Photos URIs might not have a direct file path
                return null
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // Try to get the path from the MediaStore
            context.contentResolver.query(
                uri,
                arrayOf(MediaStore.MediaColumns.DATA),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
                }
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    // Wrapper to access DocumentsContract in a way that handles its potential absence on older APIs
}

private object DocumentsContractWrapper {
    fun isDocumentUri(context: Context, uri: Uri): Boolean {
        return android.provider.DocumentsContract.isDocumentUri(context, uri)
    }

    fun getDocumentId(uri: Uri): String? {
        return android.provider.DocumentsContract.getDocumentId(uri)
    }
}
