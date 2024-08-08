package fe.buildsrc.dependency

import AndroidX
import org.gradle.kotlin.dsl.IsNotADependency


object PinnedVersions : IsNotADependency {
    private const val COMPOSE_VERSION = "1.7.0-beta06"

    var ComposeUi = AndroidX.compose.ui.withVersion(COMPOSE_VERSION)
    var ComposeFoundation = AndroidX.compose.foundation.withVersion(COMPOSE_VERSION)

    var Material3 = AndroidX.compose.material3.withVersion("1.3.0-beta05")
}
