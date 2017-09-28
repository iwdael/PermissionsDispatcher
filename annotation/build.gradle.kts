plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("maven-publish")
}

publishing {
    publications {
        register("release", MavenPublication::class) {
            groupId = "com.iwdael.permissionsdispatcher"
            artifactId = "annotation"
            version = "0.0.1"
        }
    }
}