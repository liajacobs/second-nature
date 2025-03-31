package com.example.secondnature

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import com.google.android.libraries.places.api.Places

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Google Places API
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.GOOGLE_PLACES_API_KEY)
        }
        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST, null)
    }
}
