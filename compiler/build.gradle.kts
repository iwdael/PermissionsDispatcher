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
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "com.iwdael.permissionsdispatcher"
                artifactId = "compiler"
            }
        }
    }
}