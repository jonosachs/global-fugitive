plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

    //Firebase
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    alias(libs.plugins.kotlin.compose)
}

secrets {
    // The plugin defaults to "local.properties"
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"
}

android {
    namespace = "com.example.globalfugitive"
    compileSdk = 36

    // Enable Build Features
    android.buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "com.example.globalfugitive"
        minSdk = 24
        //noinspection EditedTargetSdkVersion
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.room.common)
    // test, debug
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // implementation
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui)
    implementation(libs.coil.compose.v222)
    implementation(libs.gson)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.maps.compose.v2110) // latest version
    implementation(libs.maps.ktx)
    implementation(libs.maps)
    implementation(libs.material.v190)
    implementation(libs.places.v310)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.volley)
    implementation(platform(libs.androidx.compose.bom))

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    // Also add the dependency for the Google Play services library and specify its version
    implementation(libs.play.services.auth.v2151)
    //Authentication with Credential Manager
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.google.googleid)

    //Room
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)

    // Optional - RxJava support
    implementation(libs.androidx.room.rxjava2)

    // Optional - Coroutines support for Room
    implementation(libs.androidx.room.ktx)
    implementation (libs.androidx.lifecycle.livedata.ktx.v2100)
    implementation(libs.androidx.runtime.livedata)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    //Icons
    implementation(libs.compose.material.icons)

}