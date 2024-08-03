package fe.firefoxsync.shortcut

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.Person
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import fe.firefoxsync.share.R
import mozilla.components.concept.sync.Device

object ShortcutUtil {
    const val CAPABILITY_SEND_MESSAGE = "actions.intent.SEND_MESSAGE"
    const val CAPABILITY_RECEIVE_MESSAGE = "actions.intent.RECEIVE_MESSAGE"
    const val CATEGORY_LINK_SHARE_TARGET = "fe.firefoxsync.category.LINK_SHARE_TARGET"

    enum class Direction(val capability: String?) {
        None(null),
        Send(CAPABILITY_SEND_MESSAGE),
        Receive(CAPABILITY_RECEIVE_MESSAGE)
    }

    fun publishShortcuts(context: Context, devices: List<Device>): Boolean {
        if (ShortcutManagerCompat.isRateLimitingActive(context)) {
            Log.e("ShortcutUtil", "Ignoring publish shortcut request as we are rate-limited")
            return false
        }

        val maxShortcuts = ShortcutManagerCompat.getMaxShortcutCountPerActivity(context)
        if(devices.size > maxShortcuts) {
            Log.w("ShortcutUtil", "Max shortcut count ($maxShortcuts) > ${devices.size}, truncating")
        }

        val shortcuts = devices
            .take(maxShortcuts)
            .mapIndexed { index, device -> buildShortcut(context, device, Direction.None) }

        return ShortcutManagerCompat.setDynamicShortcuts(context, shortcuts)
    }

    fun pushShortcut(context: Context, device: Device, direction: Direction): Boolean {
        val shortcut = buildShortcut(context, device, direction)
        return ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
    }

    private fun buildShortcut(context: Context, device: Device, direction: Direction): ShortcutInfoCompat {
        return ShortcutInfoCompat.Builder(context, device.id)
            .setIcon(IconCompat.createWithResource(context, R.drawable.ic_launcher_foreground))
            .setShortLabel(device.displayName)
            .setLongLabel(device.id)
            .setLongLived(true)
//            .setRank(rank)
            .setIntent(Intent(Intent.ACTION_SEND))
            .apply { direction.capability?.let { addCapabilityBinding(it) } }
            .setCategories(setOf(CATEGORY_LINK_SHARE_TARGET))
            .setPerson(device.toPerson())
            .build()
    }

    private fun Device.toPerson(): Person {
        return Person.Builder()
            .setKey(id)
            .setImportant(true)
            .setName(displayName)
            .build()
    }
}
