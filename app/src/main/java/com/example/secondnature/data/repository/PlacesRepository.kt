package com.example.secondnature.data.repository

import android.util.Log
import com.example.secondnature.data.model.Store
import com.example.secondnature.data.model.StoreDetails
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.OpeningHours
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PlacesRepository(
    private val placesClient: PlacesClient,
) {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getNearbyStores(latitude: Double, longitude: Double): List<Store> {
        return try {
            val placeFields = listOf(Place.Field.ID, Place.Field.DISPLAY_NAME, Place.Field.LOCATION)
            val searchQuery = "thrift stores"

            val searchCenter = LatLng(latitude, longitude)

            val searchByTextRequest = SearchByTextRequest.builder(searchQuery, placeFields)
                .setMaxResultCount(10)
                .setLocationBias(CircularBounds.newInstance(searchCenter, 500.0))
                .build()

            val response = placesClient.searchByText(searchByTextRequest).await()

            response.places.map { place ->
                val placeId = place.id ?: ""
                val location = place.location

                val storeSnapshot = firestore.collection("stores")
                    .whereEqualTo("placeId", placeId)
                    .get()
                    .await()

                val firestoreStore =
                    storeSnapshot.documents.firstOrNull()?.toObject(Store::class.java)

                Store(
                    storeId = firestoreStore?.storeId ?: null,
                    placeId = placeId,
                    storeName = place.displayName,
                    storeRating = firestoreStore?.storeRating ?: null,
                    priceRating = firestoreStore?.priceRating ?: null,
                    latitude = location?.latitude ?: 0.0,
                    longitude = location?.longitude ?: 0.0
                )
            }
        } catch (e: Exception) {
            Log.e("PlacesRepository", "Error fetching nearby stores: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getStoreDetails(placeId: String): StoreDetails? {
        return try {
            val placeFields = listOf(
                Place.Field.ID,
                Place.Field.FORMATTED_ADDRESS,
                Place.Field.INTERNATIONAL_PHONE_NUMBER,
                Place.Field.WEBSITE_URI,
                Place.Field.OPENING_HOURS
            )

            val request = FetchPlaceRequest.newInstance(placeId, placeFields)
            val response = placesClient.fetchPlace(request).await()
            val place = response.place

            StoreDetails(
                placeId = place.id,
                address = place.formattedAddress ?: "Address not available",
                phoneNumber = place.internationalPhoneNumber ?: "Phone number not available",
                website = place.websiteUri?.toString() ?: "No website",
                hours = place.openingHours
            )
        } catch (e: Exception) {
            Log.e("PlacesRepository", "Error fetching store details: ${e.message}", e)
            null
        }
    }
}

