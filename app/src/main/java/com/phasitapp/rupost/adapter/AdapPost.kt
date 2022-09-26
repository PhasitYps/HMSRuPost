package com.phasitapp.rupost.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.huawei.hms.maps.*
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.MarkerOptions
import com.phasitapp.rupost.R
import com.phasitapp.rupost.Utils
import com.phasitapp.rupost.model.ModelPost
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*


class AdapPost(private var activity: Activity, private val dataList: ArrayList<ModelPost>) :
    RecyclerView.Adapter<AdapPost.ViewHolder>() {

    private val TAG = "AdapPost"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.listview_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder")

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

    private fun setDetail(holder: ViewHolder, position: Int) {
        val map = holder.huaweiMap
        var lat = dataList[position].latitude
        var long = dataList[position].longitude

        if (lat != null && long != null) {
            map?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        lat.toDouble(),
                        long.toDouble()
                    ), 12f
                )
            )
            map?.addMarker(MarkerOptions().position(LatLng(lat.toDouble(), long.toDouble())))
            map?.mapType = HuaweiMap.MAP_TYPE_NORMAL

            holder.mapView.tag = LatLng(lat.toDouble(), long.toDouble())
        }

        Glide.with(activity).load(dataList[position].profile).into(holder.profileIV)
        holder.usernameTV.text = dataList[position].username
        holder.createDateTV.text = formatCreateDate(dataList[position].createDate.toString().toLong())
        holder.titleTV.text = if(dataList[position].title != null) dataList[position].title else ""
        holder.desciptionTV.text = if (dataList[position].desciption != null) dataList[position].desciption else ""

        //holder.setImageMapStatic(holder.imageView, lat!!, long!!)

        val adapter = AdapImagePost(activity, dataList[position].images)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        holder.imageRCV.adapter = adapter
        holder.imageRCV.layoutManager = layoutManager

    }

    private fun event(holder: ViewHolder) {

        holder.likeIV.setOnClickListener {

        }
        holder.commentIV.setOnClickListener {

        }
        holder.shareIV.setOnClickListener {

        }
        holder.bookmarkIV.setOnClickListener {

        }
    }

    private fun setAnimateIcon(holder: ViewHolder) {
        val defaultIcon = holder.likeIV.layoutParams.width
        val smallIcon = (defaultIcon * 0.85).toInt()

        Log.i("dasdas", "widthDefault: " + defaultIcon)

        holder.likeIV.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    holder.likeIV.layoutParams.width = smallIcon
                    holder.likeIV.layoutParams.height = smallIcon
                    holder.likeIV.requestLayout()
                }
                MotionEvent.ACTION_UP -> {
                    holder.likeIV.layoutParams.width = defaultIcon
                    holder.likeIV.layoutParams.height = defaultIcon
                    holder.likeIV.requestLayout()
                }
            }

            false
        }
        holder.commentIV.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    holder.commentIV.layoutParams.width = smallIcon
                    holder.commentIV.layoutParams.height = smallIcon
                    holder.commentIV.requestLayout()
                }
                MotionEvent.ACTION_UP -> {
                    holder.commentIV.layoutParams.width = defaultIcon
                    holder.commentIV.layoutParams.height = defaultIcon
                    holder.commentIV.requestLayout()
                }
            }

            false
        }
        holder.shareIV.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    holder.shareIV.layoutParams.width = smallIcon
                    holder.shareIV.layoutParams.height = smallIcon
                    holder.shareIV.requestLayout()
                }
                MotionEvent.ACTION_UP -> {
                    holder.shareIV.layoutParams.width = defaultIcon
                    holder.shareIV.layoutParams.height = defaultIcon
                    holder.shareIV.requestLayout()
                }
            }

            false
        }
        holder.bookmarkIV.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    holder.bookmarkIV.layoutParams.width = smallIcon
                    holder.bookmarkIV.layoutParams.height = smallIcon
                    holder.bookmarkIV.requestLayout()
                }
                MotionEvent.ACTION_UP -> {
                    holder.bookmarkIV.layoutParams.width = defaultIcon
                    holder.bookmarkIV.layoutParams.height = defaultIcon
                    holder.bookmarkIV.requestLayout()
                }
            }

            false
        }
    }

    private fun formatCreateDate(createDate: Long): String{
        val currentDate = System.currentTimeMillis()
        val pastTime = currentDate - createDate
        //259200000 = 3 day
        //86,400,000 = 1 day
        //3,600,000 = 1 h
        //60,000 = 1 m
        //1,000 = 1 s
        Log.i("fweewfwe", "pastTime: $pastTime")
        if(pastTime > 259200000){
            return Utils.formatDate("dd MMM yyyy HH:mm", Date(createDate))
        }else if(pastTime >= 86400000){
            val day = (pastTime/86400000).toInt()
            return "$day วัน ที่เเล้ว"
        }else if(pastTime >= 3600000){
            val h = (pastTime/3600000).toInt()
            return "$h ชม. ที่เเล้ว"
        }else if(pastTime >= 60000){
            val m = (pastTime/60000).toInt()
            return "$m น. ที่เเล้ว"
        }else{
            val s = (pastTime/1000).toInt()
            return "$s วิ. ที่เเล้ว"
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
        val imageRCV = itemView.findViewById<RecyclerView>(R.id.imageRCV)
        val profileIV = itemView.findViewById<ImageView>(R.id.profileIV)
        val titleTV = itemView.findViewById<TextView>(R.id.titleTV)
        val desciptionTV = itemView.findViewById<TextView>(R.id.descriptionTV)
        val usernameTV = itemView.findViewById<TextView>(R.id.usernameTV)
        val createDateTV = itemView.findViewById<TextView>(R.id.createDateTV)
        val imageView = itemView.findViewById<ImageView>(R.id.imageView)

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

        fun setImageMapStatic(imageView: ImageView, lat: String, long: String){

            val key = "A1E9EE3AFE018709B6FD60390B23DD7ED013C5A4EF814FCD6F70FA6FC9EEEE0D"
            val url = "https://mapapi.cloud.huawei.com/mapApi/v1/mapService/getStaticMap?" +
                    "key=$key&"+
                    "width=200&" +  // Set the height of the returned map image to 512.
                    "height=200&" +  // Set the address information. (Latitude: 41.43206; Longitude: -81.38992)
                    "location=41.43206%2C-81.38992&" +
                    "zoom=14&" +
                    "pattern=PNG&" +
                    "logo=logoAnchor%3Abottomleft&" +  // Set the maker description information. (Latitude: 41.43206; Longitude: -81.38992)
                    "markers=%7B41.43206%2C-81.38992%7D"


            Picasso.get()
                .load(url)
                .placeholder(R.drawable.gif_dots_loading)
                .into(imageView, object : Callback{
                    override fun onSuccess() {}
                    override fun onError(e: Exception) {
                        Toast.makeText(
                            activity,
                            "Error to load Maps: " + e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        }

    }


}
