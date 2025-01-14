package jat.sharer.com

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : ComponentActivity() {
    private val permissionArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.READ_MEDIA_VIDEO,
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        arrayOf(
            android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,
        )
    } else {
        arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }// Register the permissions callback, which handles the user's response to the

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

        }

    private fun checkPermission(): Pair<Boolean, String?> {
        permissionArray.forEach {
            if (ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED) {
                return Pair(false, it)
            }
        }
        return Pair(true, null)
    }

    companion object{
        lateinit var instance:MainActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        setContent {
            val permissionDenied by remember { mutableStateOf(checkPermission()) }
            Surface(modifier = Modifier.fillMaxSize()) {
                when {
                    permissionDenied.first -> {
                        App()
                    }
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permissionDenied.second!!) -> {
                        AlertDialog(
                            title = { Text("Permission Required") },
                            text = { Text("This app requires permission to access your storage.") },
                            onDismissRequest = {
                                finish()
                            },
                            buttons = {
                                Button(modifier = Modifier.fillMaxWidth(0.8f).padding(30.dp), onClick = {
                                    requestPermissionLauncher.launch(permissionArray)
                                }) {
                                    Text("OK")
                                }
                            }
                        )

                    }
                    else -> {
                        App { requestPermissionLauncher.launch(permissionArray) }
                    }
                }
            }
        }
    }
}
