package jat.sharer.com

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration


actual fun getPlatform(): Platform = Platform.Android(
    name = "Android",
    version = Build.VERSION.SDK_INT.toString()
)


actual fun getJeyFile(filePath: String): JeyFile {
    return AndroidJeyFile(MainActivity.instance, filePath)
}

@Composable
actual fun rememberScreenSize(): Pair<Int, Int> {
    val size = LocalConfiguration.current
    return remember(size) {
        Pair(size.screenWidthDp, size.screenHeightDp)
    }
}

actual fun getHotspotManager(): HotspotManager {
    return AndroidHotspotManager(MainActivity.instance)
}