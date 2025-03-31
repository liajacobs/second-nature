package com.example.secondnature.ui.screens.post

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.secondnature.data.model.Store
import com.example.secondnature.data.repository.StoreRepository
import com.example.secondnature.ui.components.ImagePicker
import com.example.secondnature.viewmodel.CreatePostFormViewModel
import com.example.secondnature.viewmodel.PostViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import kotlin.math.roundToInt

@Composable
fun CreatePostScreen(
    navController: NavController,
    postFormViewModel: CreatePostFormViewModel = viewModel(),
    placeId: String?
) {
    Log.d("Lifecycle", "Entering CreatePostScreen Composable")

    val (selectedStore, setSelectedStore) = remember { mutableStateOf<Store?>(null) }
    val (imageURL, setImageURL) = remember { mutableStateOf("") }
    val (storeRating, setStoreRating) = remember { mutableStateOf(0.0) }
    val (priceRating, setPriceRating) = remember { mutableStateOf(1.0) }
    val stores by postFormViewModel.stores.observeAsState(emptyList())
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        postFormViewModel.getStores()
    }

    LaunchedEffect(placeId) {
        placeId?.let {
            postFormViewModel.getNewStoreDetails(it)
        }
    }

    val store = postFormViewModel.selectedStore.value
    store?.let {
        setSelectedStore(it)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedButton(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
                Text(text = selectedStore?.storeName ?: "Select Store")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                stores.forEach { store ->
                    DropdownMenuItem(
                        text = { Text(store.storeName) },
                        onClick = {
                            setSelectedStore(store)
                            Log.d("abc", "selecting... ${selectedStore?.storeId}")
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Image Picker Component
        ImagePicker { uri ->
            selectedImageUri = uri
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedImageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

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
                    isUploading = true

                    try {

                        val imageUrl = selectedImageUri?.let { uri ->
                            uploadImageToFirebase(uri)
                        } ?: ""

                        postFormViewModel.createPost(
                            imageURL = imageUrl,
                            storeRating = storeRating.roundToInt(),
                            priceRating = priceRating.roundToInt(),
                            store = selectedStore ?: Store(),
                            userId = auth.currentUser?.uid ?: "Unknown user id",
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
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedStore != null
        ) {
            Text(if (isUploading) "Uploading..." else "Create Post")
        }
    }
}

suspend fun uploadImageToFirebase(imageUri: Uri): String {
    val storageRef = FirebaseStorage.getInstance().reference
    val imageRef = storageRef.child("post_images/${UUID.randomUUID()}.jpg")

    return try {
        val uploadTask = imageRef.putFile(imageUri).await()
        imageRef.downloadUrl.await().toString()
    } catch (e: Exception) {
        Log.e("FirebaseStorage", "Failed to upload image: ${e.message}", e)
        ""
    }
}
