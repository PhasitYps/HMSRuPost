package com.phasitapp.rupost

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat

class GPSManage(private var activity: Activity) {

    interface MyEvent{
        fun onLocationChanged(currentLocation: Location)
        fun onDissAccessGPS()

    }
    private var l: MyEvent? = null
    fun setMyEvent(l: MyEvent){
        this.l = l
    }
    private var lat = 0.0
    private var lng = 0.0

    private var isGPS: Boolean = false
    private var locationManager: LocationManager? = null
    private val PERMISSION_REQUEST = 1001
    private val TAG = "From GPSManage"
    private var isNetwork: Boolean = false


    init {
        locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isGPS = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetwork = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d(TAG, "Latitude:" + location.latitude + ", Longitude:" + location.longitude)
            l?.onLocationChanged(location)
        }
        override fun onProviderDisabled(provider: String) {
            super.onProviderDisabled(provider)
            Log.d(TAG,"disable")
        }
        override fun onProviderEnabled(provider: String) {
            super.onProviderEnabled(provider)
            Log.d(TAG,"enabled")
        }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            super.onStatusChanged(provider, status, extras)
            Log.d(TAG,"status")
        }
    }
    fun requestGPS(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST)


            } else {
                //locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 3f, locationListener)
                if (isNetwork) {

                    locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5f, locationListener)

                    val loc = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                    if (loc != null) {
                        lat = loc.latitude
                        lng = loc.longitude
                    }
                }
                if (isGPS) {
                    locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 3f, locationListener)
                    val loc = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                    if (loc != null) {
                        lat = loc.latitude
                        lng = loc.longitude
                    }
                }
            }
        }
        if (lat != 0.0 && lng != 0.0) {
            val currentLocation = Location("MyLocation")
            currentLocation.latitude = lat
            currentLocation.longitude = lng
            l?.onLocationChanged(currentLocation)
        }
    }


    fun close(){
        locationManager!!.removeUpdates(locationListener)
    }
}

//private var isNetwork: Boolean = false
//isNetwork = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)