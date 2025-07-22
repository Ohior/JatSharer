package jat.sharer.com

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class AndroidFilePickerLauncher(
    private val trigger: () -> Unit
) : JFilePickerLauncher {
    override fun launch(allowedMimeTypes: List<String>) {
        trigger()
    }
}


@Composable
actual fun rememberJFilePicker(onResult: (List<JeyFile>) -> Unit): JFilePickerLauncher {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val uris = mutableListOf<Uri>()

        if (data?.clipData != null) {
            for (i in 0 until data.clipData!!.itemCount) {
                println(data.clipData!!.getItemAt(i))
                uris.add(data.clipData!!.getItemAt(i).uri)
            }
        } else if (data?.data != null) {
            uris.add(data.data!!)
        }

        val jeyFiles = uris.map { uri -> getJeyFile(uri.toString()) }
        onResult(jeyFiles)
    }

    val trigger = remember {
        {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            launcher.launch(intent)
        }
    }

    return AndroidFilePickerLauncher(trigger)
}
