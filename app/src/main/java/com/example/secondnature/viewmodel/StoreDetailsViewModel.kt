package com.example.secondnature.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.secondnature.data.model.StoreDetails
import com.example.secondnature.data.repository.PlacesRepository
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.launch

class StoreDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val placesClient: PlacesClient = Places.createClient(application)
    private val placesRepository: PlacesRepository = PlacesRepository(placesClient)

    private val _storeDetails = MutableLiveData<StoreDetails>()
    val storeDetails: LiveData<StoreDetails> get() = _storeDetails

    fun fetchStoreDetails(placeId: String) {
        viewModelScope.launch {
            _storeDetails.value = placesRepository.getStoreDetails(placeId)
        }
    }

}
