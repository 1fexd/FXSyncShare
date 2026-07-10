import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.TestExtension
import com.gitlab.grrfe.gradlebuild.android.extension.configurePickFirsts
import fe.build.dependencies.Grrfe
import fe.build.dependencies._1fexd
import org.gradle.kotlin.dsl.findByType

plugins {
    kotlin("plugin.compose") apply false
    kotlin("plugin.serialization") apply false
    id("com.android.application") apply false
    id("com.gitlab.grrfe.android-build-plugin") apply false
}

subprojects {
    afterEvaluate {
        if (plugins.hasPlugin("com.android.application")) {
            extensions.findByType<ApplicationExtension>()?.packaging?.configurePickFirsts()
        }
        if (plugins.hasPlugin("com.android.library")) {
            extensions.findByType<LibraryExtension>()?.packaging?.configurePickFirsts()
        }
        if (plugins.hasPlugin("com.android.test")) {
            extensions.findByType<TestExtension>()?.packaging?.configurePickFirsts()
        }

        dependencies {
            configurations.findByName("implementation")?.let { implementation ->
                implementation(platform(Grrfe.std.bom))
                implementation(platform(_1fexd.composeKit.bom))
            }
        }
    }
}
