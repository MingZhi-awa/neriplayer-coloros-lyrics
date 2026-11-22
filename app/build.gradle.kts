plugins {
    id("com.android.application")
}

android {
    namespace = "io.github.mingzhiawa.neriplayerlyrics"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.github.mingzhiawa.neriplayerlyrics"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    // A committed debug keystore keeps CI signatures stable so a new build can update the old
    // one in place. It is a debug-only key, never used for a store release.
    signingConfigs {
        getByName("debug") {
            storeFile = file("keystore/debug.jks")
            storePassword = "neriplayer-coloros-lyrics"
            keyAlias = "neriplayerlyrics"
            keyPassword = "neriplayer-coloros-lyrics"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = false
    }
}

dependencies {
    // libxposed API is provided by the framework at runtime.
    compileOnly("io.github.libxposed:api:102.0.0")
}
