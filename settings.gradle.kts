@file:Suppress("UnstableApiUsage")

import org.gradle.api.initialization.resolve.RepositoriesMode

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
