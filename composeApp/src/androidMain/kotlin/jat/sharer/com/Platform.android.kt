package jat.sharer.com

import android.os.Build
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import java.net.Inet4Address
import java.net.NetworkInterface


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

actual fun getDeviceIpAddress(): String? {
    return try {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        for (intf in interfaces) {
            val addresses = intf.inetAddresses
            for (addr in addresses) {
                if (!addr.isLoopbackAddress && addr is Inet4Address) {
                    return addr.hostAddress
                }
            }
        }
        null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

actual fun getScreenKeeper(): ScreenKeeper {
    return object : ScreenKeeper {
        private val activity = MainActivity.instance
        override fun keepScreenOn(enable: Boolean) {
            if (enable) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }
}