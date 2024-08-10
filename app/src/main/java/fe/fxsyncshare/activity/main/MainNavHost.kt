package fe.fxsyncshare.activity.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fe.android.compose.route.util.argumentRouteComposable
import fe.fxsyncshare.Routes
import fe.fxsyncshare.composable.page.login.LoginRoute
import fe.fxsyncshare.composable.page.main.NewMainRoute

@Composable
fun MainNavHost(
    navController: NavHostController,
    navigate: (String) -> Unit,
    onBackPressed: () -> Unit,
) {
    NavHost(
//        modifier = Modifier.background(Color.Red),
        navController = navController,
        startDestination = Routes.Main
    ) {
        composable(route = Routes.Main) {
            NewMainRoute(navigate = navigate)
        }

        composable(route = Routes.Login) {
            LoginRoute(navigate=navigate)
        }

//        argumentRouteComposable(route = Routes.Login) { entry, route ->
//            LoginRoute(authUrl = route.authUrl)
//        }
    }
}
