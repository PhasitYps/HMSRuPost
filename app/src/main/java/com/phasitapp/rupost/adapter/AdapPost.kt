package com.phasitapp.rupost.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hms.maps.*
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.MarkerOptions
import com.phasitapp.rupost.R
import com.phasitapp.rupost.model.ModelPost

class AdapPost(private var activity: Activity, private val dataList: ArrayList<ModelPost>): RecyclerView.Adapter<AdapPost.ViewHolder>(){

    private val TAG = "AdapPost"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val map = holder.huaweiMap
        var lat = dataList[position].latitude
        var long = dataList[position].longitude

        Log.i(TAG, "onBindViewHolder")

        if(lat != null && long != null){
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat.toDouble(), long.toDouble()), 14f))
            map?.addMarker(MarkerOptions().position(LatLng(lat.toDouble(), long.toDouble())))
            map?.mapType = HuaweiMap.MAP_TYPE_NORMAL

            holder.mapView.tag = LatLng(lat.toDouble(), long.toDouble())
        }



        setDetail(holder, position)
        event(holder)
        setAnimateIcon(holder)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        Log.i(TAG, "onViewRecycled")

        holder.huaweiMap?.clear()
        holder.huaweiMap?.mapType = HuaweiMap.MAP_TYPE_NONE

    }

    private fun setDetail(holder: ViewHolder, position: Int){


    }

    private fun event(holder: ViewHolder){

        holder.likeIV.setOnClickListener {

        }
        holder.commentIV.setOnClickListener {

        }
        holder.shareIV.setOnClickListener {

        }
        holder.bookmarkIV.setOnClickListener {

        }
    }

    private fun setAnimateIcon(holder: ViewHolder){
        val defaultIcon = holder.likeIV.layoutParams.width
        val smallIcon = (defaultIcon * 0.85).toInt()

        Log.i("dasdas", "widthDefault: " + defaultIcon)

        holder.likeIV.setOnTouchListener { view, motionEvent ->
            when(motionEvent.action){
                MotionEvent.ACTION_DOWN->{
                    holder.likeIV.layoutParams.width = smallIcon
                    holder.likeIV.layoutParams.height = smallIcon
                    holder.likeIV.requestLayout()
                }
                MotionEvent.ACTION_UP->{
                    holder.likeIV.layoutParams.width = defaultIcon
                    holder.likeIV.layoutParams.height = defaultIcon
                    holder.likeIV.requestLayout()
                }
            }

            false
        }
        holder.commentIV.setOnTouchListener { view, motionEvent ->
            when(motionEvent.action){
                MotionEvent.ACTION_DOWN->{
                    holder.commentIV.layoutParams.width = smallIcon
                    holder.commentIV.layoutParams.height = smallIcon
                    holder.commentIV.requestLayout()
                }
                MotionEvent.ACTION_UP->{
                    holder.commentIV.layoutParams.width = defaultIcon
                    holder.commentIV.layoutParams.height = defaultIcon
                    holder.commentIV.requestLayout()
                }
            }

            false
        }
        holder.shareIV.setOnTouchListener { view, motionEvent ->
            when(motionEvent.action){
                MotionEvent.ACTION_DOWN->{
                    holder.shareIV.layoutParams.width = smallIcon
                    holder.shareIV.layoutParams.height = smallIcon
                    holder.shareIV.requestLayout()
                }
                MotionEvent.ACTION_UP->{
                    holder.shareIV.layoutParams.width = defaultIcon
                    holder.shareIV.layoutParams.height = defaultIcon
                    holder.shareIV.requestLayout()
                }
            }

            false
        }
        holder.bookmarkIV.setOnTouchListener { view, motionEvent ->
            when(motionEvent.action){
                MotionEvent.ACTION_DOWN->{
                    holder.bookmarkIV.layoutParams.width = smallIcon
                    holder.bookmarkIV.layoutParams.height = smallIcon
                    holder.bookmarkIV.requestLayout()
                }
                MotionEvent.ACTION_UP->{
                    holder.bookmarkIV.layoutParams.width = defaultIcon
                    holder.bookmarkIV.layoutParams.height = defaultIcon
                    holder.bookmarkIV.requestLayout()
                }
            }

            false
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnMapReadyCallback {

        var huaweiMap: HuaweiMap? = null

        val likeIV = itemView.findViewById<ImageView>(R.id.likeIV)
        val commentIV = itemView.findViewById<ImageView>(R.id.commentIV)
        val shareIV = itemView.findViewById<ImageView>(R.id.shareIV)
        val bookmarkIV = itemView.findViewById<ImageView>(R.id.bookmarkIV)
        val mapView = itemView.findViewById<MapView>(R.id.mapView)

        init {
            Log.i(TAG, "init")
            mapView.onCreate(null)
            mapView.getMapAsync(this)

        }

        override fun onMapReady(map: HuaweiMap?) {
            huaweiMap = map
            huaweiMap?.uiSettings?.isMapToolbarEnabled = false
            huaweiMap?.uiSettings?.isCompassEnabled = false
            huaweiMap?.uiSettings?.isZoomControlsEnabled = false
            huaweiMap?.uiSettings?.setAllGesturesEnabled(false)

            val latLng = mapView.tag as LatLng
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
            map?.addMarker(MarkerOptions().position(latLng))

            Log.i(TAG, "This is MapReady in RecyclerView")

        }

    }
}
