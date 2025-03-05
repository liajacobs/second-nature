package com.example.secondnature.ui.screens.post

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.secondnature.viewmodel.PostViewModel
import com.google.firebase.Timestamp

@Composable
fun CreatePostScreen(navController: NavController, postViewModel: PostViewModel = viewModel()) {
    Log.d("Lifecycle", "Entering CreatePostScreen Composable")

    val (storeName, setStoreName) = remember { mutableStateOf("") }
    val (imageURL, setImageURL) = remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        // Store Name TextField
        TextField(
            value = storeName,
            onValueChange = setStoreName,
            label = { Text("Store Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = imageURL,
            onValueChange = setImageURL,
            label = { Text("Image URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                postViewModel.createPost(
                    imageURL = imageURL,
                    storeRating = 1,    // You can make these dynamic too if needed
                    priceRating = 2,    // Same here for priceRating
                    storeName = storeName,
                    username = "TestUser",
                    date = Timestamp.now(),
                    storeId = "storeId", // Provide actual storeId and userId here
                    userId = "userId",
                    onPostCreated = { postId ->
                        // Navigate to the created post's detail page
                        navController.navigate("viewPost/$postId")
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Post")  // Button text
        }
    }
}
