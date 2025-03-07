package com.example.secondnature.viewmodel

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val _location = MutableLiveData<Pair<Double, Double>?>()
    val location: LiveData<Pair<Double, Double>?> get() = _location

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> get() = _errorMessage

    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)

    fun getCurrentLocation() {
        Log.d("LocationViewModel", "Attempting to get current location")
        val context: Context = getApplication<Application>().applicationContext
        val accuracy = Priority.PRIORITY_HIGH_ACCURACY

        if (ActivityCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("LocationViewModel", "Location permissions not granted")
            _errorMessage.value = "Location permission is required"
            return
        }

        Log.d("LocationViewModel", "Location permissions granted, requesting location update")
        // Safe to request location
        fusedLocationProviderClient.getCurrentLocation(accuracy, CancellationTokenSource().token)
            .addOnSuccessListener { location ->
                location?.let {
                    Log.d("LocationViewModel", "Location update received - Lat: ${it.latitude}, Long: ${it.longitude}")
                    _location.value = Pair(it.latitude, it.longitude)
                } ?: run {
                    Log.e("LocationViewModel", "Received null location update")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("LocationViewModel", "Failed to get location: ${exception.message}", exception)
                _errorMessage.value = "Error retrieving location"
            }
    }

}

