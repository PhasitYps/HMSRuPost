package com.phasitapp.rupost.dialog

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.view.Window
import com.phasitapp.rupost.R
import kotlinx.android.synthetic.main.dialog_themes_map.*

class LayersMapDialog(private val activity: Activity): Dialog(activity) {

    companion object{

        val MAP_TYPE_GOOGLE_HYBRID = "googleHybrid"
        val MAP_TYPE_GOOGLE_TERRAIN = "googleTerrain"
        val MAP_TYPE_GOOGLE_SATELLITE = "googleSatellite"

        val MAP_TYPE_HUAWEI_NORMAL = "huaweiNormal"
        val MAP_TYPE_HUAWEI_TERRAIN = "huaweiTerrain"
        val MAP_TYPE_HUAWEI_SATELLITE = "huaweiSatellite"

    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_themes_map)
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        setCancelable(true)
        create()

        goolgeHybridCv.setOnClickListener {
            //hMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
            l?.let { it1 -> it1(MAP_TYPE_GOOGLE_HYBRID) }
        }
        googleTerrainCV.setOnClickListener {
            //hMap!!.mapType = GoogleMap.MAP_TYPE_HYBRID
            l?.let { it1 -> it1(MAP_TYPE_GOOGLE_TERRAIN) }
        }

        googleSatelliteCV.setOnClickListener {
            l?.let { it1 -> it1(MAP_TYPE_GOOGLE_SATELLITE) }
        }

        huaweiNormalCv.setOnClickListener {
            l?.let { it1 -> it1(MAP_TYPE_HUAWEI_NORMAL) }
        }

        huaweiTerrainCV.setOnClickListener {
            l?.let { it1 -> it1(MAP_TYPE_HUAWEI_TERRAIN) }
        }

        huaweiSatelliteCV.setOnClickListener {
            l?.let { it1 -> it1(MAP_TYPE_HUAWEI_SATELLITE) }
        }

    }

    private var l: ((String) -> Unit)? = null
    fun setMyEvent(l: (type: String)->Unit){
        this.l = l
    }
}