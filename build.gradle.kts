buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.androidGradlePlugin)
        classpath(libs.kotlin-gradle-plugin())
    }
}

plugins {
    id("org.jetbrains.kotlin.android") version libs.kotlin apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
