package com.example.firestorecomposeapp.firestore.location

import android.location.Location
import kotlinx.coroutines.flow.MutableStateFlow

class LocationUseCase {

    private val _locationFLow: MutableStateFlow<Location?> = MutableStateFlow(null)
    val locationFlow: MutableStateFlow<Location?> get() = _locationFLow


    fun updateLocation(location: Location?){
        _locationFLow.value = location
    }
}