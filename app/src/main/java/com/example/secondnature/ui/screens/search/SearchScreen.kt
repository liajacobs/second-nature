package com.example.secondnature.ui.screens.search

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.secondnature.ui.components.RequestLocationPermission
import com.example.secondnature.viewmodel.LocationViewModel

@Composable
fun SearchScreen(viewModel: LocationViewModel = viewModel()) {
    Log.d("Lifecycle", "Entering SearchScreen Composable")
    val location by viewModel.location.observeAsState()
    val errorMessage by viewModel.errorMessage.observeAsState()

    RequestLocationPermission(
        onPermissionGranted = {
            viewModel.getCurrentLocation()
        },
        onPermissionDenied = {
            viewModel.errorMessage.setValue("Location permission is required")
        }
    )

    Column {
        errorMessage?.let {
            Text(text = it, color = Color.Red)
        } ?: run {
            if (location != null) {
                Text(text = "${location?.first}, : ${location?.second}")
            } else {
                Text(text = "Fetching location...")
            }
        }
    }
}
