import android.app.Notification
import android.content.Context
import android.location.Location
import android.provider.CallLog.Locations
import kotlinx.coroutines.flow.Flow

interface LocationApi {

    /**
     * THis methods initialises the Location SDK with the application context
     */
    fun init(context: Context)
    /**
     * This method will check for location granted
     *
     *  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
     *  <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
     */
    fun requestPermissions(permissionGranted: (Boolean) -> Unit)

    fun checkPermissions(): Boolean

    // will only need one call
    suspend fun getSpeed(): String

    suspend fun getLocationAddress(): String

    fun getEtaToLocation(location: Location)

    fun isLocationEnabled(): Boolean

    fun getLocationHistory(): Flow<List<Locations>>

    fun getLastKnownLocation(): Flow<Location>

    /**
     *
     */
    fun startTracking(interval: Long, foregroundNotification: Notification,enableLocation : () -> Unit)

    fun stopTracking()

    fun destroy()
}