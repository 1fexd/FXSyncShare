@file:Suppress("UnstableApiUsage")

import org.gradle.api.initialization.resolve.RepositoriesMode
import java.util.*

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }

    plugins {
        id("de.fayard.refreshVersions") version "0.60.5"
    }
}

plugins {
    id("de.fayard.refreshVersions")
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.mozilla.org/maven2") }
        mavenLocal()
    }
}

rootProject.name = "FXSyncShare"
include(":app")

val dev = false
if (dev) {
    val properties = Properties().apply {
        file("local.properties").reader().use(::load)
    }

    val composeKitDir = properties["composekit.dir"].toString()
    includeBuild(composeKitDir) {
        val projects = setOf("app-core", "theme-core", "theme-preference", "component", "core", "layout")

        dependencySubstitution {
            for (project in projects) {
                substitute(module("com.github.1fexd.composekit:$project")).using(project(":$project"))
            }
        }
    }
}
