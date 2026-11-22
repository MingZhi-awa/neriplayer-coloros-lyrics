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

    buildTypes {
        release {
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
