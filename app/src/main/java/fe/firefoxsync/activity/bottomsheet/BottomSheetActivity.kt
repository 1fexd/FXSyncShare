package fe.firefoxsync.activity.bottomsheet

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import fe.firefoxsync.activity.BaseComponentActivity
import fe.firefoxsync.module.viewmodel.BottomSheetViewModel
import fe.firefoxsync.theme.PreviewTheme
import fe.firefoxsync.util.IntentParser
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
            viewModel.getDeviceConstellation()?.let { constellation ->
                constellation.registerDeviceObserver(viewModel, this@BottomSheetActivity, autoPause = true)
                constellation.refreshDevices()
            }
        }

        setContent(edgeToEdge = true) {
            val constellationState by viewModel.deviceConstellationFlow.collectAsStateWithLifecycle(context = Dispatchers.Main)
            val targets = remember(constellationState) {
                constellationState?.otherDevices?.filter { it.capabilities.contains(DeviceCapability.SEND_TAB) }
            }

            val coroutineScope = rememberCoroutineScope()
            val drawerState = rememberModalBottomSheetState()

            val hideDrawer: () -> Unit = {
                coroutineScope.launch { drawerState.hide() }.invokeOnCompletion { finish() }
            }

            val configuration = LocalConfiguration.current
            val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

            ImprovedBottomDrawer(
                landscape = landscape,
                // TODO: Replace with pref
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
                    Text(text = url)
                    if (targets != null && uri != null) {
                        val command = DeviceCommandOutgoing.SendTab("", url)

                        SheetContent(targets = targets, onClick = { device ->
                            coroutineScope.launch {
                                viewModel.sendTab(device, command)
                            }
                        })
                    }
                }
            )
        }
    }

    @Composable
    fun SheetContent(targets: List<Device>, onClick: (Device) -> Unit) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            items(items = targets, key = { it.id }) { device ->
                SyncDevice(device = device, onClick = { onClick(device) })
            }
        }
    }
}

@Composable
fun SyncDevice(device: Device, onClick: () -> Unit) {
    Column(
        modifier = Modifier.padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FilledTonalIconButton(onClick = onClick) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = device.displayName)
    }
}

@Preview(showBackground = true)
@Composable
fun SyncDevicePreview() {
    PreviewTheme {
        SyncDevice(
            device = Device(
                "123",
                "FirefoxSync",
                DeviceType.DESKTOP,
                false,
                System.currentTimeMillis(),
                listOf(DeviceCapability.SEND_TAB),
                false,
                null
            ),
            onClick = {}
        )
    }
}
