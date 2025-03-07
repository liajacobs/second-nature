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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.secondnature.data.repository.StoreRepository
import com.example.secondnature.viewmodel.PostViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun CreatePostScreen(navController: NavController, postViewModel: PostViewModel = viewModel()) {
    Log.d("Lifecycle", "Entering CreatePostScreen Composable")

    val (storeName, setStoreName) = remember { mutableStateOf("") }
    val (imageURL, setImageURL) = remember { mutableStateOf("") }
    val (storeRating, setStoreRating) = remember { mutableStateOf(0.0) }
    val (priceRating, setPriceRating) = remember { mutableStateOf(1.0) }
    val coroutineScope = rememberCoroutineScope()
    val storeRepository = remember { StoreRepository() }
    val auth = FirebaseAuth.getInstance()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = storeName,
            onValueChange = { newName ->
                setStoreName(newName)
                coroutineScope.launch {
                    Log.d("CreatePostScreen", "User entered store name: $newName")
                }
            },
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

        Text("Store Rating: ${storeRating.roundToInt()}")
        Slider(
            value = storeRating.toFloat(),
            onValueChange = { setStoreRating(it.toDouble()) },
            valueRange = 0f..5f,
            steps = 4
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Price Rating: ${priceRating.roundToInt()}")
        Slider(
            value = priceRating.toFloat(),
            onValueChange = { setPriceRating(it.toDouble()) },
            valueRange = 1f..3f,
            steps = 1
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    Log.d("CreatePostScreen", "Creating post with user ratings...")

                    try {
                        val storeId = "storeId" // Replace this with actual logic to fetch storeId
                        val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

                        val storeResult = storeRepository.checkAndUpdateStore(
                            storeName,
                            storeId,
                            storeRating,
                            priceRating
                        )

                        val updatedStore = storeResult.getOrNull()
                        Log.d("CreatePostScreen", "Store successfully updated -> Name: Store Rating: ${updatedStore?.storeRating}, Price Rating: ${updatedStore?.priceRating}")

                        if (storeResult.isFailure) {
                            Log.e("CreatePostScreen", "Failed to handle store")
                        }

                        Log.d("CreatePostScreen", "Store successfully updated")

                        postViewModel.createPost(
                            imageURL = imageURL,
                            storeRating = storeRating.roundToInt(),
                            priceRating = priceRating.roundToInt(),
                            storeName = storeName,
                            date = Timestamp.now(),
                            storeId = storeId,
                            userId = userId,
                            onPostCreated = { postId ->
                                Log.d("CreatePostScreen", "Post successfully created: $postId")
                                navController.navigate("viewPost/$postId")
                            }
                        )
                    } catch (e: Exception) {
                        Log.e("CreatePostScreen", "Exception occurred: ${e.message}", e)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Post")
        }
    }
}
