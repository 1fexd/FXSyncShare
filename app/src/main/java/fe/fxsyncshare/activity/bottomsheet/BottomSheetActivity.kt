package fe.fxsyncshare.activity.bottomsheet

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import fe.composekit.appbase.AppBaseComponentActivity
import fe.composekit.appbase.AppTheme
import fe.composekit.intent.IntentParser
import fe.composekit.mozilla.components.support.utils.toSafeIntent
import fe.fxsyncshare.R
import fe.fxsyncshare.composable.component.bottomsheet.ImprovedBottomDrawer
import fe.fxsyncshare.composable.theme.AppColor
import fe.fxsyncshare.composable.theme.Typography
import fe.fxsyncshare.module.viewmodel.BottomSheetViewModel
import fe.std.result.isFailure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mozilla.components.concept.sync.DeviceCapability
import mozilla.components.concept.sync.DeviceCommandOutgoing
import mozilla.components.concept.sync.TabPrivacy
import org.koin.androidx.viewmodel.ext.android.viewModel

class BottomSheetActivity : AppBaseComponentActivity() {
    private val viewModel by viewModel<BottomSheetViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("Intent", "$intent")
        val safeIntent = intent.toSafeIntent()
        val uri = IntentParser.getUriFromIntent(safeIntent)
        if(uri.isFailure()) {
            Toast.makeText(this@BottomSheetActivity, R.string.link_sent, Toast.LENGTH_SHORT).show()
            return
        }

        val url = uri.value.toString()

        lifecycleScope.launch {
            viewModel.fetchDeviceConstellation()?.let { constellation ->
                constellation.registerDeviceObserver(viewModel, this@BottomSheetActivity, autoPause = true)
                val success = constellation.refreshDevices()
                Log.d("Constellation", "Refresh devices success=$success")
            }
        }

        setContent(edgeToEdge = true) {
            AppTheme(
                appColor = AppColor,
                typography = Typography,
                theme = viewModel.theme(),
                materialYou = viewModel.themeMaterialYou(),
                amoled = viewModel.themeAmoled()
            ) { Wrapper(url) }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Wrapper(url: String) {
        val constellationState by viewModel.deviceConstellationFlow.collectAsStateWithLifecycle(context = Dispatchers.Main)
        val targets = remember(constellationState) {
            constellationState?.otherDevices?.filter { it.capabilities.contains(DeviceCapability.SEND_TAB) }
        }

        val drawerState = rememberModalBottomSheetState()
        val coroutineScope = rememberCoroutineScope()

        val hideDrawer: () -> Unit = {
            coroutineScope.launch { drawerState.hide() }.invokeOnCompletion { finish() }
        }

        val configuration = LocalConfiguration.current
        val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val isBlackTheme = viewModel.themeAmoled()

        ImprovedBottomDrawer(
            landscape = landscape,
            isBlackTheme = isBlackTheme,
            drawerState = drawerState,
            shape = RoundedCornerShape(
                topStart = 22.0.dp,
                topEnd = 22.0.dp,
                bottomEnd = 0.0.dp,
                bottomStart = 0.0.dp
            ),
            hide = hideDrawer,
            sheetContent = {
                BottomSheetContent(
                    targets = targets,
                    closeDrawer = hideDrawer,
                    sendTab = { viewModel.sendTab(it, DeviceCommandOutgoing.SendTab("", url, TabPrivacy.Normal)) }
                )
            }
        )
    }
}
