package com.example.secondnature.data.repository

import android.util.Log
import com.example.secondnature.data.model.Store
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class StoreRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getStoreRatings(storeId: String): Pair<Double, Double>? {
        return try {
            val doc = firestore.collection("stores").document(storeId).get().await()
            if (doc.exists()) {
                val storeRating = doc.getDouble("storeRating") ?: 0.0
                val priceRating = doc.getDouble("priceRating") ?: 0.0
                Log.d("StoreRepository", "Fetched Ratings -> Store: $storeRating, Price: $priceRating")
                Pair(storeRating, priceRating)
            } else {
                Log.d("StoreRepository", "No existing store found for storeId: $storeId")
                null
            }
        } catch (e: Exception) {
            Log.e("StoreRepository", "Error fetching store ratings", e)
            null
        }
    }

    // Check if store exists and either update or create a new store with average rating calculations
    suspend fun checkAndUpdateStore(
        storeName: String,
        placeId: String,
        storeRating: Double,
        priceRating: Double
    ): Result<Store> {
        return try {
            val storeRef = firestore.collection("stores").document(placeId)

            // Check if the store exists
            val documentSnapshot = storeRef.get().await()
            if (documentSnapshot.exists()) {
                Log.d("StoreRepository", "Store exists. Updating ratings...")

                // Fetch current values
                val currentStoreRating = documentSnapshot.getDouble("storeRating") ?: 0.0
                val currentStoreRatingCount = documentSnapshot.getLong("storeRatingCount") ?: 0L
                val currentPriceRating = documentSnapshot.getDouble("priceRating") ?: 0.0
                val currentPriceRatingCount = documentSnapshot.getLong("priceRatingCount") ?: 0L

                Log.d("StoreRepository", "Current Ratings -> Store: $currentStoreRating, Store Count: $currentStoreRatingCount, Price: $currentPriceRating, Price Count: $currentPriceRatingCount")

                // Calculate new average ratings
                val newStoreRatingCount = currentStoreRatingCount + 1
                val newStoreRating = (currentStoreRating * currentStoreRatingCount + storeRating) / newStoreRatingCount

                val newPriceRatingCount = currentPriceRatingCount + 1
                val newPriceRating = (currentPriceRating * currentPriceRatingCount + priceRating) / newPriceRatingCount

                Log.d("StoreRepository", "Updated Ratings -> New Store: $newStoreRating, New Store Count: $newStoreRatingCount, New Price: $newPriceRating, New Price Count: $newPriceRatingCount")

                // Update Firestore
                storeRef.update(
                    "placeId", placeId,
                    "storeName", storeName,
                    "storeRating", newStoreRating,
                    "storeRatingCount", newStoreRatingCount,
                    "priceRating", newPriceRating,
                    "priceRatingCount", newPriceRatingCount
                ).await()

                // Return the updated Store object
                val updatedStore = Store(
                    placeId = placeId,
                    storeName = storeName,
                    storeRating = newStoreRating,
                    priceRating = newPriceRating
                )

                Log.d("StoreRepository", "Store successfully updated -> Name: ${updatedStore.storeName}, Store Rating: ${updatedStore.storeRating}, Price Rating: ${updatedStore.priceRating}")

                Result.success(updatedStore)
            } else {
                Log.d("StoreRepository", "Store does not exist. Creating new store...")

                // Store does not exist, create a new one
                val newStoreData = hashMapOf(
                    "placeId" to placeId,
                    "storeName" to storeName,
                    "storeRating" to storeRating,
                    "storeRatingCount" to 1L,
                    "priceRating" to priceRating,
                    "priceRatingCount" to 1L
                )

                storeRef.set(newStoreData).await()

                val newStore = Store(
                    placeId = placeId,
                    storeName = storeName,
                    storeRating = storeRating,
                    priceRating = priceRating
                )

                Log.d("StoreRepository", "New store created -> Name: ${newStore.storeName}, Store Rating: ${newStore.storeRating}, Price Rating: ${newStore.priceRating}")

                Result.success(newStore)
            }
        } catch (e: Exception) {
            Log.e("StoreRepository", "Error handling store: ${e.message}", e)
            Result.failure(e)
        }
    }
}
