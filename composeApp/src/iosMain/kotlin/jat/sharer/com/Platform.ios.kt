@file:OptIn(ExperimentalForeignApi::class)

package jat.sharer.com

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import kotlinx.cinterop.*
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.darwin.freeifaddrs
import platform.darwin.getifaddrs
import platform.darwin.ifaddrs
import platform.darwin.inet_ntop
import platform.posix.*

actual fun getDeviceIpAddress(): String? {
    memScoped {
        val ifAddrPtr = alloc<CPointerVar<ifaddrs>>()
        if (getifaddrs(ifAddrPtr.ptr) == 0) {
            var ptr = ifAddrPtr.value
            while (ptr != null) {
                val ifa = ptr.pointed
                val saFamily = ifa.ifa_addr?.pointed?.sa_family?.toInt()

                if (saFamily == AF_INET) { // IPv4 only
                    val addr = allocArray<ByteVar>(INET_ADDRSTRLEN)
                    val sockAddrIn = ifa.ifa_addr!!.reinterpret<sockaddr_in>()
                    inet_ntop(
                        AF_INET,
                        sockAddrIn.pointed.sin_addr.ptr,
                        addr,
                        INET_ADDRSTRLEN.toUInt()
                    )
                    val ip = addr.toKString()
                    if (ip != "127.0.0.1") {
                        freeifaddrs(ifAddrPtr.value)
                        return ip
                    }
                }
                ptr = ifa.ifa_next
            }
            freeifaddrs(ifAddrPtr.value)
        }
    }
    return null
}

actual fun getPlatform(): Platform = Platform.Ios(
    name = UIDevice.currentDevice.systemName(),
    version = UIDevice.currentDevice.systemVersion
)

actual fun getJeyFile(filePath: String): JeyFile {
    return IosJeyFile(filePath)
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun rememberScreenSize(): Pair<Int, Int> {
    val size = LocalWindowInfo.current
    return remember(size.containerSize) {
        Pair(size.containerSize.width, size.containerSize.height)
    }
}

@Composable
actual fun rememberJFilePicker(onResult: (List<JeyFile>) -> Unit): JFilePickerLauncher {
    TODO("Not yet implemented")
}

actual fun getHotspotManager(): HotspotManager {
    TODO("Not yet implemented")
}

actual fun getScreenKeeper(): ScreenKeeper {
    return object : ScreenKeeper {
        override fun keepScreenOn(enable: Boolean) {
            UIApplication.sharedApplication.idleTimerDisabled = enable
        }
    }
}