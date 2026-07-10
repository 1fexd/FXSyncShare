@file:Suppress("UnstableApiUsage")

import com.gitlab.grrfe.gradlebuild.config.configureRepositories
import com.gitlab.grrfe.gradlebuild.repository.GradlePluginPortalRepository
import com.gitlab.grrfe.gradlebuild.repository.MavenRepository
import com.gitlab.grrfe.gradlebuild.repository.google
import com.gitlab.grrfe.gradlebuild.repository.jitpack
import com.gitlab.grrfe.gradlebuild.repository.mavenCentral
import com.gitlab.grrfe.gradlebuild.repository.mozilla
import fe.build.dependencies.Grrfe
import fe.build.dependencies._1fexd

rootProject.name = "FXSyncShare"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }

    plugins {
        id("de.fayard.refreshVersions") version "0.60.6"
        id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
        id("com.android.library")
        id("androidx.navigation.safeargs") version "2.9.6"
    }

    when (val gradleBuildDir = extra.properties["gradle.build.dir"]) {
        null -> {
            val gradleBuildVersion = extra.properties["gradle.build.version"]
            resolutionStrategy {
                eachPlugin {
                    with(requested.id) {
                        if (namespace == "com.gitlab.grrfe") {
                            useModule("com.gitlab.grrfe.gradle-build:$name:$gradleBuildVersion")
                        }
                    }
                }
            }
        }
        else -> includeBuild(gradleBuildDir.toString())
    }
}

plugins {
    id("de.fayard.refreshVersions")
    id("org.gradle.toolchains.foojay-resolver-convention")
    id("com.gitlab.grrfe.settings-build-plugin")
}

configureRepositories(
    MavenRepository.google(),
    MavenRepository.mavenCentral(),
    MavenRepository.jitpack(),
    MavenRepository.mozilla(),
    GradlePluginPortalRepository
)

extra.properties["gradle.build.dir"]
    ?.let { includeBuild(it.toString()) }

include(":app")

buildSettings {
    substitutes {
        trySubstitute(Grrfe.std, properties["kotlin-ext.dir"])
        trySubstitute(Grrfe.httpkt, properties["httpkt.dir"])
        trySubstitute(Grrfe.gsonExt, properties["gson-ext.dir"])
        trySubstitute(_1fexd.composeKit, properties["composekit.dir"])
    }
}

