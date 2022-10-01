package com.phasitapp.rupost.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapView
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.model.*
import com.phasitapp.rupost.*
import com.phasitapp.rupost.Utils.formatCreateDate
import com.phasitapp.rupost.helper.Prefs
import com.phasitapp.rupost.model.ModelPost
import com.phasitapp.rupost.repository.RepositoryPost
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream


class AdapPost(private var activity: Activity, private val dataList: ArrayList<ModelPost>) :
    RecyclerView.Adapter<AdapPost.ViewHolder>() {

    private val TAG = "AdapPost"
    private val prefs = Prefs(activity)
    private val repositoryPost = RepositoryPost(activity)
    private var huaweiMap: HuaweiMap? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.listview_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder")

        setDetail(holder, position)
        event(holder, position)
        setAnimateIcon(holder)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        Log.i(TAG, "onViewRecycled")

//        holder.huaweiMap?.clear()
//        holder.huaweiMap?.mapType = HuaweiMap.MAP_TYPE_NONE

    }

    fun onDestroy() {

    }

    private fun setDetail(holder: ViewHolder, position: Int) {
        val map = huaweiMap
        var lat = dataList[position].latitude!!
        var long = dataList[position].longitude!!

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

        //map?.mapType = HuaweiMap.MAP_TYPE_NORMAL

        //holder.mapView.tag = LatLng(lat.toDouble(), long.toDouble())

        Glide.with(activity).load(dataList[position].profile).into(holder.profileIV)
        holder.usernameTV.text = dataList[position].username
        holder.createDateTV.text =
            formatCreateDate(dataList[position].createDate.toString().toLong())
        holder.titleTV.text = if (dataList[position].title != null) dataList[position].title else ""
        holder.desciptionTV.text =
            if (dataList[position].desciption != null) dataList[position].desciption else ""

        repositoryPost.getStaticLike(dataList[position].id!!) { userLikes ->
            Log.i("agehaehes", "Like: " + userLikes.size)

            holder.setLike(userLikes.size)

            if (userLikes.size != 0) {
                holder.bgLikeCountLL.visibility = View.VISIBLE
                userLikes.filter { it == prefs.strUid }.apply {
                    if (userLikes.isNotEmpty()) {
                        holder.likeIV.tag = true
                        Glide.with(activity).load(R.drawable.ic_heart_red).into(holder.likeIV)
                    } else {
                        holder.likeIV.tag = false
                        Glide.with(activity).load(R.drawable.ic_heart).into(holder.likeIV)
                    }
                }

            } else {
                holder.bgLikeCountLL.visibility = View.GONE
                holder.likeIV.tag = false
            }
        }
        repositoryPost.getCommentsCount(dataList[position].id!!){ count->
            if(count != 0){
                holder.bgCommentCountLL.visibility = View.VISIBLE
                holder.countCommentTV.text = "$count"
            }
        }

        //set adap image post
        val adapter = AdapImagePost(activity, dataList[position].images)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        holder.imageRCV.adapter = adapter
        holder.imageRCV.layoutManager = layoutManager

    }

    private fun event(holder: ViewHolder, position: Int) {

        holder.likeIV.setOnClickListener {
            Log.i("agehaehes", "likeIV click")
            if (prefs.strUid != "") {
                if (holder.likeIV.tag != null) {
                    val currnetLike = holder.likeIV.tag as Boolean
                    when (currnetLike) {
                        true -> {
                            repositoryPost.like(dataList[position].id!!, false)
                            holder.likeIV.tag = false
                            Glide.with(activity).load(R.drawable.ic_heart).into(holder.likeIV)
                            holder.minusLike()
                        }
                        false -> {
                            repositoryPost.like(dataList[position].id!!, true)
                            holder.likeIV.tag = true
                            Glide.with(activity).load(R.drawable.ic_heart_red).into(holder.likeIV)
                            holder.plusLike()
                        }
                    }
                }
            }
        }

        holder.commentIV.setOnClickListener {
            Log.i("sadafwgeaggaw", "commentIV click")
            if(prefs.strUid != ""){
                val intent = Intent(activity, CommentsActivity::class.java)
                intent.putExtra(KEY_DATA, dataList[position])
                activity.startActivity(intent)
            }
        }

        holder.shareIV.setOnClickListener {

        }
        holder.bookmarkIV.setOnClickListener {
        }

        holder.imageMapIV.setOnClickListener {
            val intent = Intent(activity, MapActivity::class.java)
            intent.putExtra(KEY_EVENT, "post")
            intent.putExtra("modelPost", dataList[position])
            activity.startActivity(intent)
        }

        holder.bgCommentLL.setOnClickListener {
            val intent = Intent(activity, CommentsActivity::class.java)
            intent.putExtra(KEY_DATA, dataList[position])
            activity.startActivity(intent)
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

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnMapReadyCallback {

        var huaweiMap: HuaweiMap? = null
        var countLike: Int = 0

        val likeIV = itemView.findViewById<ImageView>(R.id.likeIV)
        val commentIV = itemView.findViewById<ImageView>(R.id.commentIV)
        val shareIV = itemView.findViewById<ImageView>(R.id.shareIV)
        val bookmarkIV = itemView.findViewById<ImageView>(R.id.bookmarkIV)
        val mapView = itemView.findViewById<MapView>(R.id.mapView)
        val imageRCV = itemView.findViewById<RecyclerView>(R.id.imageRCV)
        val profileIV = itemView.findViewById<ImageView>(R.id.profileIV)
        val titleTV = itemView.findViewById<TextView>(R.id.titleTV)
        val desciptionTV = itemView.findViewById<TextView>(R.id.desciptionTV)
        val usernameTV = itemView.findViewById<TextView>(R.id.usernameTV)
        val createDateTV = itemView.findViewById<TextView>(R.id.createDateTV)
        val imageMapIV = itemView.findViewById<ImageView>(R.id.imageMapIV)
        val countLikeTV = itemView.findViewById<TextView>(R.id.countLikeTV)
        val bgLikeCountLL = itemView.findViewById<LinearLayout>(R.id.bgLikeCountLL)
        val countCommentTV = itemView.findViewById<TextView>(R.id.countCommentTV)
        val bgCommentCountLL = itemView.findViewById<LinearLayout>(R.id.bgCommentCountLL)
        val bgCommentLL = itemView.findViewById<LinearLayout>(R.id.bgCommentLL)

        init {
            Log.i(TAG, "init")
            mapView.onCreate(null)
            mapView.getMapAsync(this)

            bgLikeCountLL.visibility = View.GONE
            bgCommentCountLL.visibility = View.GONE
        }

        fun plusLike() {
            countLike++
            countLikeTV.text = "$countLike"
            if (countLike != 0) {
                bgLikeCountLL.visibility = View.VISIBLE
            } else {
                bgLikeCountLL.visibility = View.GONE
            }
        }

        fun minusLike() {
            countLike--
            countLikeTV.text = "$countLike"
            if (countLike != 0) {
                bgLikeCountLL.visibility = View.VISIBLE
            } else {
                bgLikeCountLL.visibility = View.GONE
            }
        }

        fun setLike(count: Int) {
            countLike = count
            countLikeTV.text = "$countLike"
            if (countLike != 0) {
                bgLikeCountLL.visibility = View.VISIBLE
            } else {
                bgLikeCountLL.visibility = View.GONE
            }
        }

        override fun onMapReady(map: HuaweiMap?) {
            huaweiMap = map
            huaweiMap!!.uiSettings.isMapToolbarEnabled = false
            huaweiMap!!.uiSettings.isCompassEnabled = false
            huaweiMap!!.uiSettings.isZoomControlsEnabled = false
            huaweiMap!!.uiSettings.setAllGesturesEnabled(false)

            val latLng = mapView.tag as LatLng
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
            map?.addMarker(MarkerOptions().position(latLng))

            val mTileSize = 256
            val mScale = 1
            val mDimension = mScale * mTileSize
            val mTileProvider = TileProvider { x, y, zoom ->
                Log.i("sadafwgeaggaw", "x: $x, y:$y, z:$zoom")
                val matrix = Matrix()
                val scale: Float = Math.pow(2.0, zoom.toDouble()).toFloat() * mScale
                matrix.postScale(scale, scale)
                matrix.postTranslate((-x * mDimension).toFloat(), (-y * mDimension).toFloat())

                // Generate a Bitmap image.
                val googleUrl =
                    "https://mts3.google.com/vt/lyrs=s@186112443&hl=x-local&src=app&x=$x&y=$y&z=$zoom&s=Galile"
                val bitmap = Picasso.get().load(googleUrl).get()
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                Tile(mDimension, mDimension, stream.toByteArray())

            }
            val options = TileOverlayOptions().tileProvider(mTileProvider).transparency(0f)
            huaweiMap?.addTileOverlay(options)

            Log.i(TAG, "This is MapReady in RecyclerView")

        }

    }


}
