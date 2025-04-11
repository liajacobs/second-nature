plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.secondnature"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.secondnature"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val googlePlacesApiKey = project.findProperty("GOOGLE_PLACES_API_KEY") as String? ?: ""
        buildConfigField("String", "GOOGLE_PLACES_API_KEY", "\"$googlePlacesApiKey\"")
        resValue("string", "google_maps_key", googlePlacesApiKey)
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
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions { jvmTarget = "11" }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    testImplementation (libs.kotlinx.coroutines.test.v160)

    implementation (libs.firebase.auth.v2101)
    implementation (libs.firebase.firestore.v2400)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth) // Firebase Authentication SDK
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging) // Firestore SDK
    implementation(libs.androidx.lifecycle.viewmodel.compose.v250)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended) // for icons
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.runtime.livedata.v100)
    implementation(libs.androidx.navigation.compose) // for navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.android.gms.play.services.location)
    implementation(libs.accompanist.permissions)
    implementation(libs.coil.compose)
    implementation("com.google.accompanist:accompanist-swiperefresh:0.32.0")
    implementation(libs.google.places)

    // Maps 
    implementation(libs.maps.compose)
    implementation(libs.maps.ktx)
    implementation(libs.maps.utils.ktx)
    implementation(libs.play.services.maps)
    implementation(libs.firebase.storage.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.9")

    //Testing
    testImplementation (libs.mockk)
    testImplementation (libs.kotlinx.coroutines.test)
    testImplementation (libs.junit)
    testImplementation (libs.androidx.core.testing)
    implementation (libs.firebase.firestore.ktx)
    testImplementation (libs.mockito.inline)
    testImplementation ("org.mockito:mockito-core:5.2.0") // Use the latest version
    testImplementation ("net.bytebuddy:byte-buddy:1.12.20") // Ensure this is the latest
    testImplementation ("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation ("org.robolectric:robolectric:4.11.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.0") // for Compose UI tests
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.0")





    testImplementation(kotlin("test"))


}
