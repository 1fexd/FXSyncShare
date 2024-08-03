package fe.firefoxsync.activity

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

open class BaseComponentActivity : ComponentActivity() {
    var edgeToEdge: Boolean = false
        private set

    fun initPadding(): BaseComponentActivity {
        enableEdgeToEdge()
        edgeToEdge = true
        return this
    }

    fun setContent(edgeToEdge: Boolean, content: @Composable () -> Unit) {
        if (edgeToEdge) initPadding()
        return setContent(content = content)
    }
}
