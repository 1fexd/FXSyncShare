package fe.fxsyncshare.activity.bottomsheet

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import fe.fxsyncshare.activity.BaseComponentActivity
import fe.fxsyncshare.component.icon.FilledIcon
import fe.fxsyncshare.component.icon.IconDefaults
import fe.fxsyncshare.component.icon.containerColor
import fe.fxsyncshare.component.icon.contentColor
import fe.fxsyncshare.module.viewmodel.BottomSheetViewModel
import fe.fxsyncshare.share.R
import fe.fxsyncshare.theme.PreviewTheme
import fe.fxsyncshare.util.IntentParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mozilla.components.concept.sync.Device
import mozilla.components.concept.sync.DeviceCapability
import mozilla.components.concept.sync.DeviceCommandOutgoing
import mozilla.components.concept.sync.DeviceType
import mozilla.components.support.utils.toSafeIntent
import org.koin.androidx.viewmodel.ext.android.viewModel

class BottomSheetActivity : BaseComponentActivity() {
    private val viewModel by viewModel<BottomSheetViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("Intent", "$intent")
        val safeIntent = intent.toSafeIntent()
        val uri = IntentParser.parseSendAction(safeIntent)
        val url = uri.toString()

        lifecycleScope.launch {
            viewModel.fetchDeviceConstellation()?.let { constellation ->
                constellation.registerDeviceObserver(viewModel, this@BottomSheetActivity, autoPause = true)
                val success = constellation.refreshDevices()
                Log.d("Constellation", "Refresh devices success=$success")
            }
        }

        setContent(edgeToEdge = true) {
            val constellationState by viewModel.deviceConstellationFlow.collectAsStateWithLifecycle(context = Dispatchers.Main)
            val targets = remember(constellationState) {
                constellationState?.otherDevices?.filter { it.capabilities.contains(DeviceCapability.SEND_TAB) }
            }

            val drawerState = rememberModalBottomSheetState()

            val hideDrawer: () -> Unit = {
                lifecycleScope.launch { drawerState.hide() }.invokeOnCompletion { finish() }
            }

            val configuration = LocalConfiguration.current
            val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

            ImprovedBottomDrawer(
                landscape = landscape,
                isBlackTheme = false,
                drawerState = drawerState,
                shape = RoundedCornerShape(
                    topStart = 22.0.dp,
                    topEnd = 22.0.dp,
                    bottomEnd = 0.0.dp,
                    bottomStart = 0.0.dp
                ),
                hide = hideDrawer,
                sheetContent = {
                    SheetContainer {
                        if (targets != null) {
                            val command = DeviceCommandOutgoing.SendTab("", url)

                            SyncDeviceRow(
                                targets = targets,
                                sendTab = { viewModel.sendTab(it, command) },
                                closeDrawer = hideDrawer
                            )
                        } else {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }
            )
        }
    }

    @Composable
    fun SheetContainer(content: @Composable BoxScope.() -> Unit) {
        Box(
            modifier = Modifier
                .heightIn(min = 56.dp)
                .fillMaxWidth()
                .padding(start = 5.dp, end = 5.dp, bottom = 10.dp), content = content
        )
    }

    @Composable
    fun SyncDeviceRow(targets: List<Device>, sendTab: suspend (Device) -> Unit, closeDrawer: () -> Unit) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            items(items = targets, key = { it.id }) { device ->
                var loading by remember { mutableStateOf(false) }

                SyncDevice(
                    loading = loading,
                    device = device,
                    onClick = {
                        loading = true
                        lifecycleScope.launch { sendTab(device) }.invokeOnCompletion {
                            loading = false
                            Toast.makeText(this@BottomSheetActivity, R.string.link_sent, Toast.LENGTH_SHORT).show()
                            closeDrawer()
                        }
                    }
                )
            }
        }
    }

    private val DeviceType.icon
        get() = when (this) {
            DeviceType.DESKTOP -> Icons.Default.Computer
            DeviceType.MOBILE -> Icons.Default.Smartphone
            DeviceType.TABLET -> Icons.Default.TabletAndroid
            else -> Icons.Default.DeviceUnknown
        }

    @Composable
    fun SyncDevice(loading: Boolean, device: Device, onClick: () -> Unit) {
        TextButton(onClick = onClick) {
            if (loading) {
                SyncDeviceLoadIndicator()
            } else {
                FilledIcon(imageVector = device.deviceType.icon, contentDescription = null)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(text = device.displayName, textAlign = TextAlign.Center)
        }
    }

    @Composable
    fun SyncDeviceLoadIndicator(
        enabled: Boolean = true,
        modifier: Modifier = Modifier,
        iconSize: Dp = IconDefaults.IconSize,
        containerSize: Dp = IconDefaults.ContainerSize,
        shape: Shape = IconButtonDefaults.filledShape,
        colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
    ) {
        Surface(
            shape = shape,
            color = colors.containerColor(enabled),
            contentColor = colors.contentColor(enabled),
        ) {
            Box(modifier = Modifier.size(containerSize), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(iconSize)
                        .then(modifier),
                    color = colors.contentColor
                )
            }
        }
    }

    @Composable
    @Preview
    fun SyncDevicePreview() {
        PreviewTheme {
            SyncDevice(
                device = Device(
                    "123",
                    "FirefoxSync on Android 14",
                    DeviceType.DESKTOP,
                    false,
                    System.currentTimeMillis(),
                    listOf(DeviceCapability.SEND_TAB),
                    false,
                    null
                ),
                onClick = {},
                loading = false
            )
        }
    }
}
