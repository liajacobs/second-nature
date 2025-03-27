package com.example.secondnature.ui.screens.search

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.secondnature.viewmodel.SearchViewModel

@Composable
fun SearchScreen(locationViewModel: LocationViewModel = viewModel(),
                 searchViewModel: SearchViewModel = viewModel(),
                 navController: NavController
) {
    Log.d("Lifecycle", "Entering SearchScreen Composable")

    val location by locationViewModel.location.observeAsState()
    val errorMessage by locationViewModel.errorMessage.observeAsState()
    val stores by searchViewModel.stores.observeAsState(emptyList())

    LaunchedEffect(location) {
        location?.let { (lat, lon) ->
            searchViewModel.fetchNearbyStores(lat, lon) // Call only when location is available
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
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = store.storeName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "Place ID: ${store.placeId}")

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


