package fe.firefoxsync.module.fxa

import android.content.Context
import android.os.Build
import fe.firefoxsync.share.R

object FxaUtil {
    fun defaultDeviceName(context: Context): String {
        return context.getString(
            R.string.default_device_name_2,
            context.getString(R.string.app_name),
            Build.MANUFACTURER,
            Build.MODEL,
        )
    }
}
