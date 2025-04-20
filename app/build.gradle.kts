plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
}

android {
    namespace = "com.iteration.climbingmuse"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.iteration.climbingmuse"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    viewBinding {
        enable=true
    }
    dataBinding {
        enable=true
    }

    sourceSets.getByName("main") {
        res.srcDirs(
            "src/main/res/layout/components",
            "src/main/res/layout/settings",
            "src/main/res/layout",
            "src/main/res",
        // Assets
        )
        assets.srcDirs(
            "src/main/assets/mediapipe",
            "src/main/assets"
        )
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation (libs.androidx.constraintlayout)
    implementation (libs.androidx.lifecycle.livedata.ktx)
    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)

    // CameraX core library
    implementation(libs.androidx.camera.core)
    // CameraX Camera2 extensions
    implementation (libs.androidx.camera.camera2)
    // CameraX Lifecycle library
    implementation (libs.androidx.camera.lifecycle)
    // CameraX View class
    implementation (libs.androidx.camera.view)
    implementation (libs.androidx.camera.video)

    // Timber logger
    implementation (libs.timber)

    // MediaPipe Library
    implementation (libs.tasks.vision)
}