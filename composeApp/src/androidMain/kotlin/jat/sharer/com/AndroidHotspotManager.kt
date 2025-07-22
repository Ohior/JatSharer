package jat.sharer.com

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AndroidHotspotManager(private val context: Context) : HotspotManager {
    private val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    override fun isHotspotOn(): Flow<Boolean> {
        return flow {
            val onOff = try {
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val method = wifiManager.javaClass.getDeclaredMethod("isWifiApEnabled")
                method.isAccessible = true
                method.invoke(wifiManager) as? Boolean ?: false
            } catch (e: Exception) {
                println(e)
                false
            }
            emit(onOff)
        }
    }

    override fun enableHotspot() {
        val intent = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                Intent(Settings.ACTION_WIRELESS_SETTINGS)
            }
            else -> {
                // Older versions use different intent
                Intent().apply {
                    component = ComponentName(
                        "com.android.settings",
                        "com.android.settings.TetherSettings"
                    )
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
        }

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Fallback to general settings
            context.startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }

}