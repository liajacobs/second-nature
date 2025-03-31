package com.example.secondnature.ui.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.secondnature.viewmodel.StoreDetailsViewModel
import androidx.compose.material3.Text
import androidx.compose.material3.Button

@Composable
fun StoreScreen(
    navController: NavController,
    storeDetailsViewModel: StoreDetailsViewModel = viewModel()
) {
    val placeId = navController.currentBackStackEntry?.arguments?.getString("placeId")
    val store by storeDetailsViewModel.store.observeAsState()

    LaunchedEffect(placeId) {
        placeId?.let { id ->
            storeDetailsViewModel.fetchStoreDetails(id)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        store?.let { store ->
            Text(text = store.storeName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Place ID: ${store.placeId}")
            Text(text = "Address: ${store.address}")
            Text(text = "Phone: ${store.phoneNumber}")
            Text(text = "Website: ${store.website}")
            Text(text = "Opening Hours: ${store.hours?.weekdayText?.joinToString("\n")}")
            Text(text = "Longitude: ${store.longitude}")
            Text(text = "Latitude: ${store.latitude}")
            Text(text = "Store Rating: ${store.storeRating.toString()}")
            Text(text = "Price Rating: ${store.priceRating.toString()}")

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text(text = "Back")
            }
            Button(
                onClick = {
                    placeId?.let {
                        navController.navigate("createPost/$it")
                    } ?: navController.navigate("createPost")
                }
            ) {
                Text(text = "Create Post")
            }
        } ?: Text(text = "Loading store details...")
    }
}
