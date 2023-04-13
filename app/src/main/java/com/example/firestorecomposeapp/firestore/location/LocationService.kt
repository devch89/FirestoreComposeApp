package com.example.firestorecomposeapp.firestore.location

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY

class LocationService : Service() {

    private val fusedLocation by lazy {
       LocationServices.getFusedLocationProviderClient(applicationContext)
    }

    private var locationRequest: LocationRequest? = null

    override fun onCreate() {
        super.onCreate()
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            val interval = it.getLongExtra("INTERVAL", DEFAULT_INTERVAL)
            val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                it.getParcelableExtra("NOTIFICATION", Notification::class.java)
            } else {
                it.getParcelableExtra<Notification>("NOTIFICATION")
            }
            // set priority for notification as we get closer to locations
            locationRequest?.let {location ->
                location.interval = interval
            }?: let {
                locationRequest = LocationRequest.Builder(
                    PRIORITY_HIGH_ACCURACY,
                    interval
                ).build()
            }
            locationRequest?.let { request ->
                fusedLocation.requestLocationUpdates(
                    request,
                    getPendingIntent(applicationContext)
                )
            }

            startForeground(123,notification)
        }

        // Returns an Int, a flag so we dont have to recreate the service if it is killed
        // will use the same instance it was originally created
        return START_STICKY
    }

    // This is not a Bound Service so we must set to null
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object{
        const val DEFAULT_INTERVAL = 5000L

         fun getLocationServiceIntent(
             context: Context,
             notification: Notification,
             interval: Long) = Intent(context, LocationService::class.java).apply {
             putExtra("NOTIFICATION", notification)
             putExtra("INTERVAL", interval)
         }

        fun getPendingIntent(context: Context) =
            // add if call
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getBroadcast(
                    context, 123, LocationReceiver.getIntent(context), PendingIntent.FLAG_MUTABLE
                )
            } else {
                PendingIntent.getBroadcast(
                    context, 123, LocationReceiver.getIntent(context), PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
    }

}