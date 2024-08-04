package fe.fxsyncshare.shortcut

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.Person
import androidx.core.content.LocusIdCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import fe.fxsyncshare.R
import fe.fxsyncshare.activity.bottomsheet.BottomSheetActivity
import mozilla.components.concept.sync.Device

object ShortcutUtil {
    const val CAPABILITY_SEND_MESSAGE = "actions.intent.SEND_MESSAGE"
    const val CAPABILITY_RECEIVE_MESSAGE = "actions.intent.RECEIVE_MESSAGE"
    const val CATEGORY_LINK_SHARE_TARGET = "fe.fxsyncshare.category.LINK_SHARE_TARGET"
    const val EXTRA_DEVICE_ID = "fe.fxsyncshare.intent.extra.DEVICE_ID"

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
        if (devices.size > maxShortcuts) {
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
            .setIcon(IconCompat.createWithResource(context, R.mipmap.ic_launcher))
            .setShortLabel(device.displayName)
            .setLongLabel(device.id)
            .setLongLived(true)
//            .setRank(rank)
            .setActivity(ComponentName(context, BottomSheetActivity::class.java))
            .setIntent(device.createIntent(context))
            .setCategories(setOf(CATEGORY_LINK_SHARE_TARGET))
            .setPerson(device.toPerson())
            .setLocusId(LocusIdCompat(device.id))
            .apply { direction.capability?.let { addCapabilityBinding(it) } }
            .build()
    }

    private fun Device.toPerson(): Person {
        return Person.Builder()
            .setKey(id)
//            .setImportant(true)
            .setName(displayName)
            .build()
    }

    private fun Device.createIntent(context: Context): Intent {
        return Intent(context, BottomSheetActivity::class.java)
            .setAction(Intent.ACTION_SEND)
            .putExtra(EXTRA_DEVICE_ID, id)
    }
}
