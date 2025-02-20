// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript{

    val agp_version by extra("8.1.2")
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
    }
    repositories {
        mavenCentral()
    }
}



plugins {
    id("com.android.application") version "8.1.4" apply false
    id("com.android.library") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
}