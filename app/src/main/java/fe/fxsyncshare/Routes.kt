package fe.fxsyncshare

import androidx.annotation.Keep
import fe.composekit.route.Route
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class LoginRoute(val authUrl: String) : Route

object Routes {
    const val Main = "route__main"
//    val Login = route(
//        "route__login",
//        route = LoginRouteData
//    )
    const val Login = "route__login"
}
