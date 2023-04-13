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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.Flow

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

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var startedLocationTracking = false
    lateinit var notification: Notification
    private val locationCallback = MyLocationCallback()

    override fun init(context: Context) {

//        isLocationEnabled()
//        startTracking(5000L, notification){
//
//        }
//        stopTracking()

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
        TODO("Not yet implemented")
    }

    override fun startTracking(interval: Long, foregroundNotification: Notification, enableLocation : () -> Unit) {
        Log.d(TAG, "startTracking: Tracking has started")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (!startedLocationTracking) {
            locationManager = getSystemService(context, LocationManager::class.java) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                enableLocation.invoke()
            }

            locationRequest.fastestInterval = interval
//            noinspection MissingPermission
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
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
