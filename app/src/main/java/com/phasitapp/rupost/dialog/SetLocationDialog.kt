package com.phasitapp.rupost.dialog

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.location.Location
import android.net.ConnectivityManager
import android.util.Log
import android.view.KeyEvent
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.model.*
import com.phasitapp.rupost.GPSManage
import com.phasitapp.rupost.R
import com.phasitapp.rupost.helper.Prefs
import com.squareup.picasso.Picasso


import kotlinx.android.synthetic.main.dialog_set_location.*
import java.io.ByteArrayOutputStream


class SetLocationDialog(private var activity: Activity):Dialog(activity) {

    private var l: ((LatLng) -> Unit?)? = null
    fun setMyEvent(l: (latLng: LatLng)->Unit){
        this.l = l
    }

    fun setLatLng(latLng: LatLng){
        latitude = latLng.latitude
        longitude = latLng.longitude
    }

    private var prefs = Prefs(activity)
    private var gpsManager = GPSManage(activity)

    private lateinit var mMap: HuaweiMap
    private var latitude: Double? = null
    private var longitude: Double? = null

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_set_location)
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT)
        setCancelable(false)
        create()

        init()
        initMap()
        event()

    }

    private var currentMarker: Marker? = null
    private fun init(){
        setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss()
                true
            }
            false
        }

        setOnDismissListener {
            mapView.onDestroy()
            gpsManager.close()
        }

        gpsManager.setMyEvent(object : GPSManage.MyEvent{
            override fun onLocationChanged(currentLocation: Location) {
                latitude = currentLocation.latitude
                longitude = currentLocation.longitude

                val myLocation = LatLng(latitude!!, longitude!!)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17f))
                gpsManager.close()
            }

            override fun onDissAccessGPS() {}
        })
    }

    private fun initMap(){

        mapView.onCreate(onSaveInstanceState())
        mapView.onResume()

        mapView.getMapAsync{ hMap->
            mMap = hMap    //13.668217, 100.614021
            mMap!!.uiSettings.isMapToolbarEnabled = false
            mMap!!.uiSettings.isCompassEnabled = false
            mMap!!.uiSettings.isZoomControlsEnabled = false

            mMap!!.mapType = HuaweiMap.MAP_TYPE_NONE
            mTileOverlay?.remove()
            mTileOverlay?.clearTileCache()
            val mTileSize = 256
            val mScale = 1
            val mDimension = mScale * mTileSize
            val mTileProvider = TileProvider { x, y, zoom ->
                Log.i("sadafwgeaggaw", "x: $x, y:$y, z:$zoom")
                val matrix = Matrix()
                val scale: Float = Math.pow(2.0, zoom.toDouble()).toFloat() * mScale
                matrix.postScale(scale, scale)
                matrix.postTranslate(
                    (-x * mDimension).toFloat(),
                    (-y * mDimension).toFloat()
                )

                // Generate a Bitmap image.
                val googleUrl = "https://mts3.google.com/vt/lyrs=y@186112443&hl=x-local&src=app&x=$x&y=$y&z=$zoom&s=Galile"
                val bitmap = Picasso.get().load(googleUrl).get()
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                Tile(mDimension, mDimension, stream.toByteArray())

            }
            val options = TileOverlayOptions().tileProvider(mTileProvider).transparency(0f)
            mTileOverlay = mMap!!.addTileOverlay(options)

            if(latitude != null && longitude!= null){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude!!, longitude!!), 17f))
            }

        }
    }

    private fun event(){

        selectRL.setOnClickListener {
            if(isNetworkAvailable()){
                val latLng = mMap.cameraPosition.target
                latitude = latLng.latitude
                longitude = latLng.longitude

                l?.let { it1 -> it1(LatLng(latitude!!, longitude!!)) }

                dismiss()
            }else {
                Toast.makeText(activity, "กรุณาเช็คเครือข่าย", Toast.LENGTH_SHORT).show()
            }
        }

        currentFab.setOnClickListener {
            gpsManager.requestGPS()
        }

        backRL.setOnClickListener {
            dismiss()
        }

        layerFab.setOnClickListener {
            showLayersMapDialog()
        }

    }

    private var mTileOverlay: TileOverlay? = null
    private fun showLayersMapDialog() {
        val dialog = LayersMapDialog(activity)
        dialog.setMyEvent { mapType ->

            when (mapType) {
                LayersMapDialog.MAP_TYPE_GOOGLE_HYBRID -> {
                    mMap!!.mapType = HuaweiMap.MAP_TYPE_NONE
                    mTileOverlay?.remove()
                    mTileOverlay?.clearTileCache()
                    val mTileSize = 256
                    val mScale = 1
                    val mDimension = mScale * mTileSize
                    val mTileProvider = TileProvider { x, y, zoom ->
                        Log.i("sadafwgeaggaw", "x: $x, y:$y, z:$zoom")
                        val matrix = Matrix()
                        val scale: Float = Math.pow(2.0, zoom.toDouble()).toFloat() * mScale
                        matrix.postScale(scale, scale)
                        matrix.postTranslate(
                            (-x * mDimension).toFloat(),
                            (-y * mDimension).toFloat()
                        )

                        // Generate a Bitmap image.
                        val googleUrl =
                            "https://mts3.google.com/vt/lyrs=y@186112443&hl=x-local&src=app&x=$x&y=$y&z=$zoom&s=Galile"
                        val bitmap = Picasso.get().load(googleUrl).get()
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        Tile(mDimension, mDimension, stream.toByteArray())

                    }
                    val options = TileOverlayOptions().tileProvider(mTileProvider).transparency(0f)

                    mTileOverlay = mMap!!.addTileOverlay(options)
                }
                LayersMapDialog.MAP_TYPE_GOOGLE_TERRAIN -> {
                    mMap!!.mapType = HuaweiMap.MAP_TYPE_NONE
                    mTileOverlay?.remove()
                    mTileOverlay?.clearTileCache()
                    val mTileSize = 256
                    val mScale = 1
                    val mDimension = mScale * mTileSize
                    val mTileProvider = TileProvider { x, y, zoom ->
                        Log.i("sadafwgeaggaw", "x: $x, y:$y, z:$zoom")
                        val matrix = Matrix()
                        val scale: Float = Math.pow(2.0, zoom.toDouble()).toFloat() * mScale
                        matrix.postScale(scale, scale)
                        matrix.postTranslate(
                            (-x * mDimension).toFloat(),
                            (-y * mDimension).toFloat()
                        )

                        // Generate a Bitmap image.
                        val googleUrl =
                            "https://mts3.google.com/vt/lyrs=p@186112443&hl=x-local&src=app&x=$x&y=$y&z=$zoom&s=Galile"
                        val bitmap = Picasso.get().load(googleUrl).get()
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        Tile(mDimension, mDimension, stream.toByteArray())

                    }
                    val options = TileOverlayOptions().tileProvider(mTileProvider).transparency(0f)

                    mTileOverlay = mMap!!.addTileOverlay(options)
                }
                LayersMapDialog.MAP_TYPE_GOOGLE_SATELLITE -> {
                    mMap!!.mapType = HuaweiMap.MAP_TYPE_NONE
                    mTileOverlay?.remove()
                    mTileOverlay?.clearTileCache()
                    val mTileSize = 256
                    val mScale = 1
                    val mDimension = mScale * mTileSize
                    val mTileProvider = TileProvider { x, y, zoom ->
                        Log.i("sadafwgeaggaw", "x: $x, y:$y, z:$zoom")
                        val matrix = Matrix()
                        val scale: Float = Math.pow(2.0, zoom.toDouble()).toFloat() * mScale
                        matrix.postScale(scale, scale)
                        matrix.postTranslate(
                            (-x * mDimension).toFloat(),
                            (-y * mDimension).toFloat()
                        )

                        // Generate a Bitmap image.
                        val googleUrl =
                            "https://mts3.google.com/vt/lyrs=s@186112443&hl=x-local&src=app&x=$x&y=$y&z=$zoom&s=Galile"
                        val bitmap = Picasso.get().load(googleUrl).get()
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        Tile(mDimension, mDimension, stream.toByteArray())

                    }
                    val options = TileOverlayOptions().tileProvider(mTileProvider).transparency(0f)

                    mTileOverlay = mMap!!.addTileOverlay(options)
                }

                LayersMapDialog.MAP_TYPE_HUAWEI_NORMAL -> {
                    mTileOverlay?.remove()
                    mTileOverlay?.clearTileCache()
                    mMap!!.mapType = HuaweiMap.MAP_TYPE_NORMAL
                }
                LayersMapDialog.MAP_TYPE_HUAWEI_TERRAIN -> {
                    mTileOverlay?.remove()
                    mTileOverlay?.clearTileCache()
                    mMap!!.mapType = HuaweiMap.MAP_TYPE_TERRAIN
                }
                LayersMapDialog.MAP_TYPE_HUAWEI_SATELLITE -> {
                    mTileOverlay?.remove()
                    mTileOverlay?.clearTileCache()
                    mMap!!.mapType = HuaweiMap.MAP_TYPE_SATELLITE
                }

            }
        }
        dialog.show()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}