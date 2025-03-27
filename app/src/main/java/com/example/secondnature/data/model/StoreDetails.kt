package com.example.secondnature.data.model

import com.google.android.libraries.places.api.model.OpeningHours

data class StoreDetails(
    val placeId: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val website: String = "",
    val hours: OpeningHours,
)