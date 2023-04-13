package com.example.firestorecomposeapp.firestore.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class LocationReceiver: BroadcastReceiver() {

   // val locationFlow: MutableStateFlow<Location?> = MutableStateFlow(null)

    @Inject
    lateinit var locationUseCase: LocationUseCase

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            val location = LocationResult.extractResult(it)?.lastLocation
            locationUseCase.updateLocation(location)
        }
    }

    // A way that we can add static methods and variables without creating an instance of the class
    companion object {


        private const val LOCATION_ACTION = "com.example.firestorecomposeapp.firestore.location"

        fun getIntent(context: Context) = Intent(context, LocationReceiver::class.java)

        val intentFilter = IntentFilter().apply{
            addAction(LOCATION_ACTION)
        }

    }
}