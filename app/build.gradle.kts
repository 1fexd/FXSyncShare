import com.android.build.api.dsl.VariantDimension
import de.fayard.refreshVersions.core.versionFor
import fe.buildsrc.*
import net.nemerosa.versioning.ReleaseInfo
import net.nemerosa.versioning.SCMInfo
import net.nemerosa.versioning.VersioningExtension
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "fe.firefoxsync.share"
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        applicationId = "fe.firefoxsync.share"
        minSdk = Version.MIN_SDK
        targetSdk = Version.COMPILE_SDK

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    kotlin {
        jvmToolchain(Version.JVM)
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = versionFor(AndroidX.compose.compiler)
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    coreLibraryDesugaring(Android.tools.desugarJdkLibs)

    implementation(MozillaComponents.concept.storage)
    implementation(MozillaComponents.concept.toolbar)
    implementation(MozillaComponents.browser.storageSync)
    implementation(MozillaComponents.service.firefoxAccounts)
    implementation(MozillaComponents.service.syncLogins)
    implementation(MozillaComponents.service.syncAutofill)
    implementation(MozillaComponents.support.rustLog)
    implementation(MozillaComponents.support.rustHttp)
    implementation(MozillaComponents.lib.fetchHttpUrlConnection)
    implementation(MozillaComponents.lib.dataProtect)

    implementation(AndroidX.core.ktx)
    implementation(AndroidX.lifecycle.process)
    implementation(AndroidX.lifecycle.runtime.compose)
    implementation(AndroidX.lifecycle.viewModelCompose)
    implementation(AndroidX.lifecycle.runtime.ktx)
    implementation(AndroidX.activity.compose)
    implementation(AndroidX.navigation.compose)

    implementation(Koin.android)
    implementation(Koin.compose)

    implementation("com.github.1fexd:compose-route-util:0.0.12")


    implementation(platform(AndroidX.compose.bom))
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.ui.graphics)
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.material3)

    testImplementation(Testing.junit4)

    androidTestImplementation(platform(AndroidX.compose.bom))
    androidTestImplementation(AndroidX.test.ext.junit)
    androidTestImplementation(AndroidX.test.espresso.core)
    androidTestImplementation(AndroidX.compose.ui.testJunit4)

    debugImplementation(AndroidX.compose.ui.tooling)
    debugImplementation(AndroidX.compose.ui.testManifest)
}
