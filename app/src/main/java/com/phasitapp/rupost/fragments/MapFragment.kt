package com.phasitapp.rupost.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.huawei.hms.maps.*
import com.phasitapp.rupost.R
import com.phasitapp.rupost.utils.MapUtils
import kotlinx.android.synthetic.main.fragment_map.*


class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback  {


    companion object {
        private const val TAG = "MapFragment"
        private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    }

    private var hMap: HuaweiMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }


    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }


    override fun onMapReady(map: HuaweiMap?) {
        Log.d(TAG, "onMapReady: ")

        hMap = map!!
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