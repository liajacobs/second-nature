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
    val storeDetails by storeDetailsViewModel.storeDetails.observeAsState()

    LaunchedEffect(placeId) {
        placeId?.let { id ->
            storeDetailsViewModel.fetchStoreDetails(id)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        storeDetails?.let { details ->
            Text(text = "Store Details", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Address: ${details.address}")
            Text(text = "Phone: ${details.phoneNumber}")
            Text(text = "Website: ${details.website}")
            Text(text = "Opening Hours: ${details.hours.weekdayText.joinToString("\n")}")

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text(text = "Back")
            }
        } ?: Text(text = "Loading store details...")
    }
}
