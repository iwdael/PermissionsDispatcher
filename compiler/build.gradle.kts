plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("maven-publish")
}



dependencies {
    implementation(libs.symbol.processing.api)
    implementation(libs.kotlinsymbolprocessor)
    implementation(project(":annotation"))
}

publishing {
    publications {
        register("release", MavenPublication::class) {
            groupId = "com.iwdael.permissionsdispatcher"
            artifactId = "compiler"
            version = "0.0.1"
        }
    }
}