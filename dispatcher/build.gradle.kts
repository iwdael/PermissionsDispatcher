plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.iwdael.permissionsdispatcher"
    compileSdk = 34

    defaultConfig {
        minSdk = 19
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

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    api(project(":annotation"))
}

publishing {
    publications {
        register("release", MavenPublication::class) {
            groupId = "com.iwdael.permissionsdispatcher"
            artifactId = "dispatcher"
            version = "0.0.1"
        }
    }
}