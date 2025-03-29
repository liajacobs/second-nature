package com.example.secondnature.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.secondnature.data.model.Post
import com.example.secondnature.data.model.Store
import com.example.secondnature.data.repository.PlacesRepository
import com.example.secondnature.data.repository.PostRepository
import com.example.secondnature.data.repository.StoreRepository
import com.example.secondnature.data.repository.UserRepository
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class CreatePostFormViewModel(application: Application) : AndroidViewModel(application) {

    private val placesClient: PlacesClient = Places.createClient(application)
    private val placesRepository: PlacesRepository = PlacesRepository(placesClient)

    private val postRepository = PostRepository()
    private val userRepository = UserRepository()
    private val storeRepository = StoreRepository()

    private val _post = MutableLiveData<Post?>()
    val post: LiveData<Post?>
        get() = _post

    private val _stores = MutableLiveData<List<Store>>()
    val stores: LiveData<List<Store>>
        get() = _stores

    private val _selectedStore = MutableLiveData<Store?>()
    val selectedStore: LiveData<Store?>
        get() = _selectedStore

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun getStores() {
        viewModelScope.launch {
            try {
                val storeList = storeRepository.getAllStores()
                _stores.value = storeList
            } catch (e: Exception) {
                _error.value = "Error fetching stores: ${e.message}"
            }
        }
    }

    // Gets name and location of new store to be passed into create store
    fun getNewStoreDetails(placeId: String?) {
        viewModelScope.launch {
            try {
                placeId?.let {
                    // Fetch store details by placeId
                    val store = placesRepository.getStoreNameAndLocation(placeId)

                    store?.let { fetchedStore ->
                        val currentStores = _stores.value ?: emptyList()
                        if (!currentStores.contains(fetchedStore)) {
                            _stores.value = currentStores + fetchedStore
                        }

                        _selectedStore.value = fetchedStore
                    } ?: run {
                        _error.value = "Store not found for placeId: $placeId"
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error fetching store details: ${e.message}"
            }
        }
    }

    // Creates post, updates ratings or creates stores new store as needed
    fun createPost(
        imageURL: String,
        storeRating: Int,
        priceRating: Int,
        store: Store,
        userId: String,
        onPostCreated: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val userProfileResult = userRepository.getUserProfile()

                if (userProfileResult.isFailure) {
                    _error.value = "Failed to get user profile: ${userProfileResult.exceptionOrNull()?.message}"
                    return@launch
                }

                val user = userProfileResult.getOrThrow()

                var updatedStore = store

                Log.d("abc", store.storeId ?: "none")
                if (store.storeId.isNullOrBlank()) {
                    // Store doesn't exist, create a new one
                    val newStore = store.copy(
                        storeRating = storeRating.toDouble(),
                        priceRating = priceRating.toDouble(),
                        ratingCount = 1
                    )
                    val createStoreResult = storeRepository.createStore(newStore)

                    if (createStoreResult.isFailure) {
                        _error.value = "Failed to create store: ${createStoreResult.exceptionOrNull()?.message}"
                        return@launch
                    }

                    updatedStore = createStoreResult.getOrThrow()
                } else {
                    // Store exists, update it
                    val updateStoreResult = storeRepository.updateStore(store.storeId, storeRating, priceRating)

                    if (updateStoreResult.isFailure) {
                        _error.value = "Failed to update store: ${updateStoreResult.exceptionOrNull()?.message}"
                        return@launch
                    }

                    updatedStore = updateStoreResult.getOrThrow()
                }

                // Create Post
                val post = Post(
                    postId = "",
                    imageURL = imageURL,
                    storeRating = storeRating,
                    priceRating = priceRating,
                    storeName = updatedStore.storeName,
                    username = user.username,
                    date = Timestamp.now(),
                    storeId = updatedStore.storeId ?: "Cannot find store id",
                    userId = userId
                )

                val createPostResult = postRepository.createPost(post)

                if (createPostResult.isSuccess) {
                    onPostCreated(createPostResult.getOrThrow())
                } else {
                    _error.value = createPostResult.exceptionOrNull()?.message ?: "Unknown error"
                }

            } catch (e: Exception) {
                _error.value = "Error creating post: ${e.message}"
            }
        }
    }

}
