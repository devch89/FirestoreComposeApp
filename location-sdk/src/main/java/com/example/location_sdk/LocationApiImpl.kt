package com.example.location_sdk

import LocationApi
import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.CallLog
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


private const val TAG = "LocationApiImpl"

class MyLocationCallback : LocationCallback() {

}

class LocationApiImpl(private val context: Context) : LocationApi {

    var gpsStatus: Boolean = false
    private var locationRequest = LocationRequest.create().apply {
        interval = 5000 // 5 seconds
        fastestInterval = 1000 // 1 second
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private lateinit var locationManager: LocationManager

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private var startedLocationTracking = false
    lateinit var notification: Notification
    private val locationCallback = MyLocationCallback()
    var lastKnowLocation: Location = Location("")
    override fun init(context: Context) {
//        isLocationEnabled()
//        startTracking(5000L, notification){
//
//        }
//        stopTracking()

    }

    fun getLocation(): Flow<Location> = flow {
        emit(lastKnowLocation)
    }

    override fun requestPermissions(permissionGranted: (Boolean) -> Unit) {
        permissionGranted(checkPermissions())
    }

    override fun checkPermissions() =
        (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)

    override suspend fun getSpeed(): String {
        TODO("Not yet implemented")
    }

    override suspend fun getLocationAddress(): String {
        TODO("Not yet implemented")
    }

    override fun getEtaToLocation(location: Location) {
        TODO("Not yet implemented")
    }

    override fun isLocationEnabled() = startedLocationTracking

    override fun getLocationHistory(): Flow<List<CallLog.Locations>> {
        TODO("Not yet implemented")
    }

    override fun getLastKnownLocation(): Flow<Location> {
        Log.d(TAG, "getLastKnownLocation: ${getLocation()}")
        return getLocation()
    }

    override fun startTracking(
        interval: Long,
        foregroundNotification: Notification,
        enableLocation: () -> Unit
    ) {
        Log.d(TAG, "startTracking: Tracking has started")
        if (!startedLocationTracking) {
            locationManager =
                getSystemService(context, LocationManager::class.java) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                enableLocation.invoke()
            }

            locationRequest.fastestInterval = interval
//            noinspection MissingPermission
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                { locationResult ->
                    locationResult ?: return@requestLocationUpdates
                    lastKnowLocation = locationResult
                    Log.d(TAG, "startTracking: this is the location $lastKnowLocation")
                },
                Looper.getMainLooper()
            )
            startedLocationTracking = true
        }
    }

    override fun stopTracking() {
        Log.d(TAG, "stopTracking: Tracking has stopped")
        fusedLocationClient.removeLocationUpdates(locationCallback)
        startedLocationTracking = false
    }

    override fun destroy() {
        TODO("Not yet implemented")
    }
}
