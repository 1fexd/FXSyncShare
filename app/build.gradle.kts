import com.gitlab.grrfe.gradlebuild.Version
import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import com.gitlab.grrfe.gradlebuild.android.ArchiveBaseName
import com.gitlab.grrfe.gradlebuild.android.version.DefaultFallbackVersionCodeProducer
import com.gitlab.grrfe.gradlebuild.android.version.SemverProducer
import com.gitlab.grrfe.gradlebuild.android.version.createAndroidVersionProvider
import com.gitlab.grrfe.gradlebuild.util.PropertiesFile
import com.gitlab.grrfe.gradlebuild.util.SystemEnvironment
import com.gitlab.grrfe.gradlebuild.util.withProviders
import fe.build.dependencies.Grrfe
import fe.build.dependencies.MozillaComponents
import fe.build.dependencies._1fexd

plugins {
    kotlin("plugin.compose")
    kotlin("plugin.serialization")
    id("com.android.application")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
    id("com.gitlab.grrfe.android-build-plugin")
}

var appName = "FXSyncShare"

android {
    namespace = "fe.fxsyncshare"
    compileSdk = 37

    defaultConfig {
        applicationId = "fe.fxsyncshare"
        minSdk = AndroidSdk.MIN_SDK
        targetSdk = AndroidSdk.COMPILE_SDK

        val now = System.currentTimeMillis()

        val versionProvider = createAndroidVersionProvider(
            versionCodeProducer = SemverProducer,
            fallbackVersionCodeProducer = DefaultFallbackVersionCodeProducer
        )
        val (name, code, commit, branch) = versionProvider.get()
        versionCode = code
        versionName = name

        with(ArchiveBaseName) {
            project.base.setArchivesName(appName, name, now)
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testOptions.unitTests.isIncludeAndroidResources = true

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        register("env") {
            val properties = with(PropertiesFile) {
                rootProject.file(".ignored/keystore.properties").readPropertiesOrNull()
            }
            val provider = withProviders(properties, SystemEnvironment)
            storeFile = provider.get("KEYSTORE_FILE_PATH")?.let { rootProject.file(it) }
            storePassword = provider.get("KEYSTORE_PASSWORD")
            keyAlias = provider.get("KEY_ALIAS")
            keyPassword = provider.get("KEY_PASSWORD")
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

    buildFeatures {
        buildConfig = true
        aidl = true
        resValues = true
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

kotlin {
    jvmToolchain(Version.JVM)
}

dependencies {
    coreLibraryDesugaring(Android.tools.desugarJdkLibs)

    implementation(MozillaComponents.concept.storage)
    implementation(MozillaComponents.concept.toolbar)
    implementation(MozillaComponents.browser.storageSync)
    implementation(MozillaComponents.service.firefoxAccounts)
    implementation(MozillaComponents.service.syncLogins)
    implementation(MozillaComponents.service.syncAutofill)
    implementation(MozillaComponents.support.utils)
    implementation(MozillaComponents.support.appServices)
    implementation(MozillaComponents.lib.fetchHttpUrlConnection)
    implementation(MozillaComponents.lib.dataProtect)

    implementation(platform("androidx.compose:compose-bom-alpha:_"))
    implementation(AndroidX.compose.foundation)
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.ui.text)
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.material3)
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

    implementation(platform(Grrfe.std.bom))
    implementation(Grrfe.std.core)
    implementation(Grrfe.std.time.core)
    implementation(Grrfe.std.time.java)
    implementation(Grrfe.std.result.core)
    implementation(Grrfe.std.uri)
    implementation(Grrfe.std.stringbuilder)
    implementation(Grrfe.std.test)
    implementation(Grrfe.std.process.core)

    implementation(platform(_1fexd.composeKit.bom))
    implementation(_1fexd.composeKit.compose.core)
    implementation(_1fexd.composeKit.compose.layout)
    implementation(_1fexd.composeKit.compose.component)
    implementation(_1fexd.composeKit.compose.app)
    implementation(_1fexd.composeKit.compose.theme.core)
    implementation(_1fexd.composeKit.compose.theme.preference)
    implementation(_1fexd.composeKit.compose.dialog)
    implementation(_1fexd.composeKit.compose.route)
    implementation(_1fexd.composeKit.core)
    implementation(_1fexd.composeKit.koin)
    implementation(_1fexd.composeKit.process)
    implementation(_1fexd.composeKit.intent)
    implementation(_1fexd.composeKit.lifecycle.core)
    implementation(_1fexd.composeKit.lifecycle.koin)
    implementation(_1fexd.composeKit.preference.core)
    implementation(_1fexd.composeKit.preference.compose.core)
    implementation(_1fexd.composeKit.preference.compose.core2)
    implementation(_1fexd.composeKit.preference.compose.mock)
    implementation(_1fexd.composeKit.span.core)
    implementation(_1fexd.composeKit.span.compose)
    implementation(_1fexd.composeKit.ext.mozillaSupportUtils)

    implementation(COIL)
    implementation(COIL.compose)

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
