package com.example.secondnature.data.repository

import android.util.Log
import com.example.secondnature.data.model.Post
import com.example.secondnature.data.model.Store
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class StoreRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getAllStores(): List<Store> {
        return try {
            val doc = firestore.collection("stores").get().await()
            doc.documents.mapNotNull { document ->
                val store = document.toObject(Store::class.java)
                store?.copy(storeId = document.id)  // Set storeId as the document ID
            }
        } catch (e: Exception) {
            Log.e("StoreRepository", "Error fetching stores", e)
            emptyList()
        }
    }

    suspend fun createStore(store: Store): Result<Store> {
        return try {
            val documentRef = firestore.collection("stores").add(store).await()
            val createdStore = store.copy(storeId = documentRef.id)
            Result.success(createdStore)
        } catch (e: Exception) {
            Log.e("PostRepository", "Error creating store: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateStore(storeId: String, newStoreRating: Int, newPriceRating: Int): Result<Store> {
        return try {
            val storeRef = firestore.collection("stores").document(storeId)

            val storeSnapshot = storeRef.get().await()

            if (storeSnapshot.exists()) {
                val currentStore = storeSnapshot.toObject(Store::class.java)

                val newStoreRatingAvg = (currentStore?.storeRating ?: 0.0) * (currentStore?.ratingCount ?: 0) + newStoreRating
                val newPriceRatingAvg = (currentStore?.priceRating ?: 0.0) * (currentStore?.ratingCount ?: 0) + newPriceRating

                val newRatingCount = (currentStore?.ratingCount ?: 0) + 1

                val updatedStore = currentStore?.copy(
                    storeRating = newStoreRatingAvg / newRatingCount,
                    priceRating = newPriceRatingAvg / newRatingCount,
                    ratingCount = newRatingCount
                )

                updatedStore?.let {
                    storeRef.set(it).await()
                    Result.success(it)
                } ?: Result.failure(Exception("Error updating store"))
            } else {
                Result.failure(Exception("Store not found"))
            }
        } catch (e: Exception) {
            Log.e("PostRepository", "Error updating store: ${e.message}")
            Result.failure(e)
        }
    }
}
