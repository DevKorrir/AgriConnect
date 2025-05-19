buildscript {
    dependencies {
        //classpath ("com.android.tools.build:gradle:8.11.1")
        classpath("com.google.gms:google-services:4.4.2")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.56.2")
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.google.gms.google.services) apply false

    id("com.google.dagger.hilt.android") version "2.56.2" apply false
    //id("com.google.devtools.ksp") version "2.2.0-Beta2-1.0.32"
}