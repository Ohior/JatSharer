package jat.sharer.com

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext


// Actual class implementing the launcher logic for Android
class AndroidFilePickerLauncher(
    private val onResult: (List<PlatformFile>) -> Unit,
    // Keep references to the underlying Android launchers
    private val singleFileLauncher: androidx.activity.result.ActivityResultLauncher<Array<String>>,
    private val multipleFilesLauncher: androidx.activity.result.ActivityResultLauncher<Array<String>>
) : FilePickerLauncher(onResult = onResult) {
    override fun launch(allowMultiple: Boolean, allowedMimeTypes: List<String>?) {
        val mimeTypes = allowedMimeTypes?.toTypedArray() ?: arrayOf("*/*") // Default to all types

        if (allowMultiple) {
            multipleFilesLauncher.launch(mimeTypes)
        } else {
            singleFileLauncher.launch(mimeTypes)
        }
    }
}


@Composable
actual fun rememberFilePickerLauncher(
    onResult: (List<PlatformFile>) -> Unit,
): FilePickerLauncher {
    val context = LocalContext.current

    // Launcher for single file selection
    val singleFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            val files = uri?.let {
                // You need ContentResolver to get metadata like display name
                val fileName = it.getFileName(context) ?: "Untitled"
                val fileSize = getFileSizeFromUri(context, it) ?: 0L
                // Path might not be directly accessible, Uri is the key
                listOf(
                    PlatformFile(
                        name = fileName, path = uri.toString(), size = fileSize
                    )
                )
                // Content reading would typically happen here or be passed via the PlatformFile
                // val content = context.contentResolver.openInputStream(it)?.readBytes()
            } ?: emptyList()
            onResult(files)
        }
    )

    // Launcher for multiple file selection
    val multipleFilesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { uris: List<Uri> ->
            val files = uris.map { uri ->
                val fileName = uri.getFileName(context) ?: "Untitled_${uris.indexOf(uri)}"
                val fileSize = getFileSizeFromUri(context, uri) ?: 0L
                PlatformFile(name = fileName, path = uri.toString(), size = fileSize)
                // Content reading logic here...
            }
            onResult(files)
        }
    )

    // Return the actual launcher implementation
    // We remember this instance to ensure stability across recompositions
    return remember(singleFileLauncher, multipleFilesLauncher, onResult) {
        AndroidFilePickerLauncher(onResult, singleFileLauncher, multipleFilesLauncher)
    }
}

// Helper function to get file name from Uri
private fun Uri.getFileName(context: Context): String? {
    var name: String? = null
    // Try to get display name from ContentResolver
    context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                name = cursor.getString(nameIndex)
            }
        }
    }
    // Fallback if display name is not available
    return name ?: this.lastPathSegment
}

private fun getFileSizeFromUri(context: Context, uri: Uri): Long? {
    val contentResolver = context.contentResolver
    var fileSize: Long? = null

    try {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val sizeIndex = cursor.getColumnIndex("length") // Try "length" first
                if (sizeIndex != -1) {
                    fileSize = cursor.getLong(sizeIndex)
                } else {
                    // Some content providers might use "_size" instead
                    val sizeIndexAlt = cursor.getColumnIndex("_size")
                    if (sizeIndexAlt != -1) {
                        fileSize = cursor.getLong(sizeIndexAlt)
                    }
                }
            }
        }
    } catch (e: SecurityException) {
        // Handle cases where you don't have the necessary permissions
        e.printStackTrace()
        return null
    } catch (e: Exception) {
        // Handle other potential exceptions
        e.printStackTrace()
        return null
    }

    return fileSize
}



