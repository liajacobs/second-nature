package com.example.secondnature.ui.screens.post

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
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
    val (storeRating, setStoreRating) = remember { mutableIntStateOf(0) }
    val (priceRating, setPriceRating) = remember { mutableIntStateOf(1) }

    Column(modifier = Modifier.padding(16.dp)) {
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

        Text("Store Rating: $storeRating")
        Slider(
            value = storeRating.toFloat(),
            onValueChange = { setStoreRating(it.toInt()) },
            valueRange = 0f..5f,
            steps = 4
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Price Rating: $priceRating")
        Slider(
            value = priceRating.toFloat(),
            onValueChange = { setPriceRating(it.toInt()) },
            valueRange = 1f..3f,
            steps = 1
        )

        Button(
            onClick = {
                postViewModel.createPost(
                    imageURL = imageURL,
                    storeRating = storeRating,
                    priceRating = priceRating,
                    storeName = storeName,
                    username = "TestUser",
                    date = Timestamp.now(),
                    storeId = "storeId",
                    userId = "userId",
                    onPostCreated = { postId ->
                        navController.navigate("viewPost/$postId")
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Post")
        }
    }
}
