package com.example.secondnature.ui.screens.search

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.secondnature.data.model.Store
import com.example.secondnature.ui.components.RequestLocationPermission
import com.example.secondnature.viewmodel.LocationViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.secondnature.BuildConfig
import com.example.secondnature.viewmodel.SearchViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

// Store name and ratings
private fun formatMarkerTitle(store: Store): String {
    val starsString = store.storeRating?.let { rating ->
        val fullStars = rating.toInt()
        "★".repeat(fullStars)
    } ?: ""
    
    val dollarsString = store.priceRating?.let { price ->
        val fullDollars = price.toInt()
        "$".repeat(fullDollars)
    } ?: ""
    
    return "${store.storeName} $starsString $dollarsString"
}

@Composable
fun SearchScreen(locationViewModel: LocationViewModel = viewModel(),
                 searchViewModel: SearchViewModel = viewModel(),
                 navController: NavController
) {
    Log.d("Lifecycle", "Entering SearchScreen Composable")

    val location by locationViewModel.location.observeAsState()
    val errorMessage by locationViewModel.errorMessage.observeAsState()
    val stores by searchViewModel.stores.observeAsState(emptyList())
    
    val defaultLocation = LatLng(40.0067, -83.0305) // Ohio State University
    val userLocation = location?.let { LatLng(it.first, it.second) } ?: defaultLocation
    
    // Store the currently selected location (either user location or a clicked marker)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 12f)
    }

    LaunchedEffect(location) {
        location?.let { (lat, lon) ->
            searchViewModel.fetchNearbyStores(lat, lon) // Call only when location is available
            cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(lat, lon), 12f)
        }
    }

    RequestLocationPermission(
        onPermissionGranted = {
            locationViewModel.getCurrentLocation()
        },
        onPermissionDenied = {
            locationViewModel.errorMessage.setValue("Location permission is required")
        }
    )

    Column(modifier = Modifier.padding(16.dp)) {
        errorMessage?.let {
            Text(text = it, color = Color.Red)
        } ?: run {
            if (location != null) {
                // Map
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = true,
                        mapType = MapType.NORMAL,
                        isTrafficEnabled = false
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        myLocationButtonEnabled = true,
                        compassEnabled = true
                    )
                ) {
                    stores.forEach { store ->
                        Marker(
                            state = MarkerState(
                                position = LatLng(store.latitude, store.longitude)
                            ),
                            title = formatMarkerTitle(store),
                            onClick = {
                                false 
                            },
                            onInfoWindowClick = {
                                navController.navigate("store/${store.placeId}")
                            }
                        )
                    }
                }
                
                if (stores.isEmpty()) {
                    Text(text = "No stores found nearby.")
                } else {
                    LazyColumn {
                        items(stores) { store ->
                            StoreItem(store, navController)
                        }
                    }
                }
            } else {
                Text(text = "Loading Stores...")
            }
        }
    }
}

@Composable
fun StoreItem(store: Store,  navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp).clickable {
        navController.navigate("store/${store.placeId}")
    }
    ) {
        Log.d("Lifecycle", "Entering StoreItem Composable")
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = store.storeName, fontWeight = FontWeight.Bold, fontSize = 18.sp)

            if (!store.storeId.isNullOrEmpty()) {
                Text(text = "Store ID: ${store.storeId}")
            }
            Text(
                text = "Store Rating: ${store.storeRating?.toString() ?: "Not rated"}",
                color = if (store.storeRating != null) Color.Black else Color.Gray
            )
            Text(
                text = "Price Rating: ${store.priceRating?.toString() ?: "Not rated"}",
                color = if (store.priceRating != null) Color.Black else Color.Gray
            )

            Text(text = "Location: ${"%.5f".format(store.latitude)}, ${"%.5f".format(store.longitude)}")
        }
    }
}


