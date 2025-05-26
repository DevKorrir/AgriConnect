plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    //id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "dev.korryr.agrimarket"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.korryr.agrimarket"
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
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.navigation.compose.android)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.dtdi)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //di
    // Dagger-Hilt
    implementation ("com.google.dagger:hilt-android:2.56.2")
    kapt          ("com.google.dagger:hilt-android-compiler:2.56.2")

    // Hilt + Compose (only if you’re using Jetpack Compose)
    implementation ("androidx.hilt:hilt-navigation-compose:1.0.0")

    // Firebase & Google Services
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // Logging & Debug
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.23")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    //implementation ("com.google.firebase:firebase-appcheck-playintegrity:18.0.0")

    //courotine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // app check dependecins
    //implementation ("com.google.firebase:firebase-appcheck:18.0.0")
   // implementation ("com.google.firebase:firebase-appcheck-playintegrity:18.0.0")
    //debugImplementation ("com.google.firebase:firebase-appcheck-debug:18.0.0")
    // Debug provider for local testing

        //preference
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //extend icons
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material:material-icons-extended:<version>")

    //image
    implementation("io.coil-kt:coil-compose:2.5.0")




}
