package com.phasitapp.rupost

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.huawei.hms.maps.*
import com.huawei.hms.maps.model.LatLng

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var hMap: HuaweiMap? = null
    private var mSupportMapFragment: SupportMapFragment? = null

    companion object {
        private const val TAG = "MapActivity"
        private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(this)
        setContentView(R.layout.activity_map)
        MapsInitializer.setApiKey("DAEDAIIRgTrqegv5cQLCqf+eSa8zu6Tb21uuAr6Y741zyJmaFud2N+2V5yyFUSFFZ71nZ9svK8Xv74dRKpK2YPfNgyl9tfKEux22xQ==")

        // Initialize the SDK.
        mSupportMapFragment = supportFragmentManager.findFragmentById(R.id.mapfragment_mapfragmentdemo) as SupportMapFragment?
        mSupportMapFragment?.getMapAsync(this)


    }

    override fun onMapReady(map: HuaweiMap?) {
        Log.d(TAG, "onMapReady: ")
        hMap = map
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(13.760474204625705, 100.52330713650157), 15f))
    }
}