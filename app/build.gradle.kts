import de.fayard.refreshVersions.core.versionFor
import fe.buildsrc.KotlinClosure4
import fe.buildsrc.dependency.MozillaComponents
import fe.buildsrc.Version
import fe.buildsrc.dependency._1fexd
import fe.buildsrc.extension.getOrSystemEnv
import fe.buildsrc.extension.readPropertiesOrNull
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
    id("net.nemerosa.versioning")
}

// Must be defined before the android block, or else it won't work
versioning {
    releaseMode = KotlinClosure4<String?, String?, String?, VersioningExtension, String>({ _, _, currentTag, _ ->
        currentTag
    })

    releaseParser = KotlinClosure2<SCMInfo, String, ReleaseInfo>({ info, _ -> ReleaseInfo("release", info.tag) })
}

var appName = "FXSyncShare"
val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH_mm_ss")

android {
    namespace = "fe.fxsyncshare"
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        applicationId = "fe.fxsyncshare"
        minSdk = Version.MIN_SDK
        targetSdk = Version.COMPILE_SDK

        val now = System.currentTimeMillis()
        val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(now), ZoneId.of("UTC"))
        val versionInfo = providers.provider { versioning.info }.get()

        versionCode = versionInfo.tag?.let {
            versionInfo.versionNumber.versionCode
        } ?: (now / 1000).toInt()

        versionName = versionInfo.tag ?: versionInfo.full
        val archivesBaseName = if (versionInfo.tag != null) {
            "$appName-$versionName"
        } else "$appName-${dtf.format(localDateTime)}-$versionName"

        setProperty("archivesBaseName", archivesBaseName)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        register("env") {
            val properties = rootProject.file(".ignored/keystore.properties").readPropertiesOrNull()

            storeFile = properties.getOrSystemEnv("KEYSTORE_FILE_PATH")?.let { rootProject.file(it) }
            storePassword = properties.getOrSystemEnv("KEYSTORE_PASSWORD")
            keyAlias = properties.getOrSystemEnv("KEY_ALIAS")
            keyPassword = properties.getOrSystemEnv("KEY_PASSWORD")
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"

            resValue("string", "app_name", "$appName Debug")
        }

        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        register("nightly") {
            initWith(buildTypes.getByName("release"))
            matchingFallbacks.add("release")
            signingConfig = signingConfigs.getByName("env")

            applicationIdSuffix = ".nightly"
            versionNameSuffix = "-nightly"

            resValue("string", "app_name", "$appName Nightly")
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
            excludes += "/META-INF/{AL2.0,LGPL2.1,atomicfu.kotlin_module,LICENSE.md,LICENSE-notice.md}"
        }

        jniLibs {
            useLegacyPackaging = true
        }
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("x86", "armeabi-v7a", "arm64-v8a")
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
    implementation(MozillaComponents.support.utils)
    implementation(MozillaComponents.lib.fetchHttpUrlConnection)
    implementation(MozillaComponents.lib.dataProtect)

    implementation(AndroidX.compose.ui.withVersion("1.7.0-beta06"))
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.material3.withVersion("1.3.0-beta05"))
    implementation(AndroidX.compose.material.icons.core)
    implementation(AndroidX.compose.material.icons.extended)

    implementation(AndroidX.core.ktx)
    implementation(AndroidX.lifecycle.process)
    implementation(AndroidX.lifecycle.runtime.compose)
    implementation(AndroidX.lifecycle.viewModelCompose)
    implementation(AndroidX.lifecycle.runtime.ktx)
    implementation(AndroidX.activity.compose)
    implementation(AndroidX.navigation.compose)

    implementation(Google.android.material)
    implementation(Google.accompanist.permissions)

    implementation(Koin.android)
    implementation(Koin.compose)

    implementation(_1fexd.android.preference.core)
    implementation(_1fexd.android.preference.compose)
    implementation(_1fexd.android.preference.composeMock)
    implementation(_1fexd.android.compose.dialog)
    implementation(_1fexd.android.compose.route)
    implementation(_1fexd.android.span.compose)
    implementation(_1fexd.android.uiKit.components)
    implementation(_1fexd.android.uiKit.util)

    testImplementation(Testing.robolectric)
    testImplementation(Testing.junit.jupiter)

    testImplementation(AndroidX.test.core)
    testImplementation(AndroidX.test.coreKtx)
    testImplementation(AndroidX.test.ext.truth)
    testImplementation(AndroidX.test.runner)
    androidTestUtil(AndroidX.test.orchestrator)
    androidTestImplementation(platform(AndroidX.compose.bom))

    debugImplementation(AndroidX.compose.ui.tooling)
    debugImplementation(AndroidX.compose.ui.testManifest)
}
