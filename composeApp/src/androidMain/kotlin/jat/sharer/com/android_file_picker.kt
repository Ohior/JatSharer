package jat.sharer.com

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class AndroidFilePickerLauncher(
    private val launcher: ActivityResultLauncher<Intent>
) : JFilePickerLauncher {
    private var allowedTypes: List<String> = listOf("*/*")

    override fun launch(allowedMimeTypes: List<String>) {
        allowedTypes = allowedMimeTypes
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = if (allowedMimeTypes.size == 1) allowedMimeTypes[0] else "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        launcher.launch(intent)
    }
}

@Composable
actual fun rememberJFilePicker(onResult: (List<JeyFile>) -> Unit): JFilePickerLauncher {
    val context = MainActivity.instance//LocalContext.current as? ComponentActivity
    var launcher by remember { mutableStateOf<ActivityResultLauncher<Intent>?>(null) }

    DisposableEffect(Unit) {
        val l = context.activityResultRegistry.register(
            "multiFilePicker",
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data = result.data
            val filePaths = mutableListOf<JeyFile>()
            if (data?.clipData != null) {
                for (i in 0 until data.clipData!!.itemCount) {
                    val uri: Uri = data.clipData!!.getItemAt(i).uri
                    filePaths.add(getJeyFile(uri.toString()))
                }
            } else if (data?.data != null) {
                filePaths.add(getJeyFile(data.data!!.toString()))
            }
            onResult(filePaths)
        }
        launcher = l
        onDispose { launcher?.unregister() }
    }

    return remember(launcher) {
        launcher?.let { AndroidFilePickerLauncher(it) } ?: object : JFilePickerLauncher {
            override fun launch(allowedMimeTypes: List<String>) {
                // no-op if launcher not ready
            }
        }
    }
}