package com.phasitapp.rupost.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.app.ActivityCompat
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapsInitializer
import com.huawei.hms.maps.OnMapReadyCallback
import com.phasitapp.rupost.R
import kotlinx.android.synthetic.main.fragment_map.*


class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private var hMap: HuaweiMap? = null

    companion object {
        private const val TAG = "SearchFragment"
        private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MapsInitializer.setApiKey("DAEDAIIRgTrqegv5cQLCqf+eSa8zu6Tb21uuAr6Y741zyJmaFud2N+2V5yyFUSFFZ71nZ9svK8Xv74dRKpK2YPfNgyl9tfKEux22xQ==")

        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mapView.apply {
            onCreate(mapViewBundle)
            getMapAsync(this@MapFragment)
        }
    }

    override fun onMapReady(map: HuaweiMap?) {
        Log.d(TAG, "onMapReady: ")
        hMap = map
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onPause() {
        mapView?.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }
}