package jat.sharer.com

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

//
//class AndroidFilePickerLauncher(
//    private val launcher: ActivityResultLauncher<Intent>
//) : JFilePickerLauncher {
//    private var allowedTypes: List<String> = listOf("*/*")
//
//    override fun launch(allowedMimeTypes: List<String>) {
//        allowedTypes = allowedMimeTypes
//        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
//            type = if (allowedMimeTypes.size == 1) allowedMimeTypes[0] else "*/*"
//            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//        }
//        launcher.launch(intent)
//    }
//}

//@Composable
//actual fun rememberJFilePicker(onResult: (List<JeyFile>) -> Unit): JFilePickerLauncher {
//    val context = MainActivity.instance//LocalContext.current as? ComponentActivity
//    var launcher by remember { mutableStateOf<ActivityResultLauncher<Intent>?>(null) }
//
//    DisposableEffect(Unit) {
//        val l = context.activityResultRegistry.register(
//            "multiFilePicker",
//            ActivityResultContracts.StartActivityForResult()
//        ) { result ->
//            val data = result.data
//            val filePaths = mutableListOf<JeyFile>()
//            if (data?.clipData != null) {
//                for (i in 0 until data.clipData!!.itemCount) {
//                    val uri: Uri = data.clipData!!.getItemAt(i).uri
//                    filePaths.add(getJeyFile(uri.toString()))
//                }
//            } else if (data?.data != null) {
//                filePaths.add(getJeyFile(data.data!!.toString()))
//            }
//            onResult(filePaths)
//        }
//        launcher = l
//        onDispose { launcher?.unregister() }
//    }
//
//    return remember(launcher) {
//        launcher?.let { AndroidFilePickerLauncher(it) } ?: object : JFilePickerLauncher {
//            override fun launch(allowedMimeTypes: List<String>) {
//                // no-op if launcher not ready
//            }
//        }
//    }
//}


class AndroidFilePickerLauncher(
    private val trigger: () -> Unit
) : JFilePickerLauncher {
    override fun launch(allowedMimeTypes: List<String>) {
        trigger()
    }
}

//
//@Composable
//actual fun rememberJFilePicker(onResult: (List<JeyFile>) -> Unit): JFilePickerLauncher {
//    val pickFileLauncher = rememberLauncherForActivityResult(
//            ActivityResultContracts.OpenMultipleDocuments()
//        ) { uris: List<Uri>? ->
//            val filePaths = uris?.map { uri -> getJeyFile(uri.toString()) } ?: emptyList()
//            onResult(filePaths)
//        }
//    return AndroidFilePickerLauncher(pickFileLauncher)
//}


@Composable
actual fun rememberJFilePicker(onResult: (List<JeyFile>) -> Unit): JFilePickerLauncher {
    val context = LocalContext.current
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
