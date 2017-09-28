plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.ksp)
}

android {
    namespace = "com.iwdael.permissionsdispatcher.example"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.iwdael.permissionsdispatcher.example"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "0.0.1"

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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(project(":dispatcher"))
    ksp(project(":compiler"))
}