package com.phasitapp.rupost.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapView
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.MarkerOptions
import com.phasitapp.rupost.R
import com.phasitapp.rupost.model.ModelPost

class AdapImagePost(private var activity: Activity, private val dataList: List<String>) :
    RecyclerView.Adapter<AdapImagePost.ViewHolder>() {

    private val TAG = "AdapImagePost"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.listview_image_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder")
        val image = dataList[position]
        Glide.with(activity).load(image).into(holder.imageIV)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageIV = itemView.findViewById<ImageView>(R.id.imageIV)
    }

}