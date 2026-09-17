import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

kotlin {
    // Единый JDK для Java и Kotlin
    jvmToolchain(17)

    // Явно фиксируем Kotlin bytecode на JVM 17
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

android {
    namespace = "com.alivpn.app"

    // androidx.core:1.17.0 требует API 36+
    compileSdk = 36

    defaultConfig {
        applicationId = "com.alivpn.app"

        minSdk = 26
        targetSdk = 35

        versionCode = 1
        versionName = "0.1.0"
    }

    // Java тоже строго JVM 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.activity:activity-ktx:1.10.1")
}
