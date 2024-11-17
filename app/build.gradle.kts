
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.cst3115.enterprise.techfixapp"
    compileSdk = 35
    buildFeatures {
        buildConfig = true
    }
    kapt {
        correctErrorTypes = true
    }

    defaultConfig {

        applicationId = "com.cst3115.enterprise.techfixapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        val mapsApiKey = project.findProperty("MAPS_API_KEY") as? String ?: ""
//        buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
//        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
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
    implementation(libs.androidx.navigation.compose)
    implementation(libs.places)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson converter for JSON parsing
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("androidx.compose.material:material-icons-extended:1.4.3")
    // Location Services
    implementation ("com.google.android.gms:play-services-location:21.3.0") // Check for the latest version

    // Accompanist Permissions for Jetpack Compose
    implementation ("com.google.accompanist:accompanist-permissions:0.34.0")
    // Google Maps Compose library
    val mapsComposeVersion = "2.11.4"
    implementation("com.google.maps.android:maps-compose:$mapsComposeVersion")
    // Google Maps Compose utility library
    implementation("com.google.maps.android:maps-compose-utils:$mapsComposeVersion")
    // Google Maps Compose widgets library
    implementation("com.google.maps.android:maps-compose-widgets:$mapsComposeVersion")
    // Maps Compose (for Jetpack Compose integration)
    // Hilt
    val hiltVersion = "2.51.1"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation(kotlin("reflect"))


    // Accompanist Permissions (if not already added)


}
