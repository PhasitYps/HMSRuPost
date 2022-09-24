package com.phasitapp.rupost

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.huawei.hms.maps.*
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.MarkerOptions
import com.phasitapp.rupost.utils.MapUtils

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = "MapActivity"

    private var mSupportMapFragment: SupportMapFragment? = null

    private var hMap: HuaweiMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Initialize the SDK.
        mSupportMapFragment = supportFragmentManager.findFragmentById(R.id.mapfragment_mapfragmentdemo) as SupportMapFragment?
        mSupportMapFragment?.getMapAsync(this)
    }

    override fun onMapReady(huaweiMap: HuaweiMap) {
        Log.d(TAG, "onMapReady: ")
        hMap = huaweiMap
        hMap?.isMyLocationEnabled = true
        //hMap?.mapType = HuaweiMap.MAP_TYPE_HYBRID
        huaweiMap.setOnMapClickListener {
            huaweiMap.addMarker(MarkerOptions().position(it))
        }
    }

}