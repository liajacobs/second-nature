package com.example.secondnature.data.model

import com.google.android.libraries.places.api.model.OpeningHours

data class Store(
    val storeId: String? = null,
    val placeId: String = "",
    val storeName: String = "",
    val storeRating: Double? = 0.0,
    val priceRating: Double? = 0.0,
    val ratingCount: Int? = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String? = "",
    val phoneNumber: String? = "",
    val website: String? = "",
    val hours: OpeningHours? = null
)