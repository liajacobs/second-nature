package com.example.secondnature.data.model

data class Store(
    val storeId: String? = "",
    val placeId: String = "",
    val storeName: String = "",
    val storeRating: Double? = 0.0,
    val priceRating: Double? = 0.0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)