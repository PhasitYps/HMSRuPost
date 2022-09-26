package com.phasitapp.rupost

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.chip.Chip
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.SupportMapFragment
import com.huawei.hms.maps.model.*
import com.phasitapp.rupost.Utils.getViewBitmap
import com.phasitapp.rupost.dialog.DetailPostBottomDialog
import com.phasitapp.rupost.dialog.LayersMapDialog
import com.phasitapp.rupost.helper.Prefs
import com.phasitapp.rupost.model.ModelPost
import com.phasitapp.rupost.repository.RepositoryPost
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.fragment_home.categoryCG
import java.io.ByteArrayOutputStream


class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = "MapActivity"

    private var mSupportMapFragment: SupportMapFragment? = null

    private var hMap: HuaweiMap? = null
    private lateinit var gpsManage: GPSManage
    private lateinit var prefs : Prefs

    private val postList = ArrayList<ModelPost>()
    private val latLngList = ArrayList<LatLng>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Initialize the SDK.
        addChipCategoryView()
        initMap()
        init()
        event()
    }

    private var currentMarker: Marker? = null
    private fun init(){
        prefs = Prefs(this)
        gpsManage = GPSManage(this)
        gpsManage.setMyEvent(object : GPSManage.MyEvent{
            override fun onLocationChanged(currentLocation: Location) {
                val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                val markerOption = MarkerOptions().position(latLng)

                currentMarker?.remove()
                setIconMarker(prefs.strPhotoUri!!){ bitmapIcon->
                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(bitmapIcon))
                    currentMarker = hMap?.addMarker(markerOption)
                }
                hMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

            }
            override fun onDissAccessGPS() {

            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()

        gpsManage?.close()
    }

    private fun setIconMarker(image: String, l:(bitmapIcon: Bitmap?)->Unit) {
        val view: View = LayoutInflater.from(this).inflate(R.layout.map_marker_image, null)
        view.layout(0, 0, 240, 240);
        val imageCIV = view.findViewById(R.id.imageCIV) as CircleImageView

        Glide.with(this)
            .asBitmap()
            .load(image)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                    imageCIV.setImageBitmap(resource)
                    val bitmapView = getViewBitmap(view)
                    l(bitmapView)

                }
                override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
            })
    }

    private fun setIconMarker(resIdRes: Int, l:(bitmapIcon: Bitmap?)->Unit) {
        val view: View = LayoutInflater.from(this).inflate(R.layout.map_marker_image, null)
        view.layout(0, 0, 240, 240);
        val imageCIV = view.findViewById(R.id.imageCIV) as CircleImageView

        Glide.with(this)
            .asBitmap()
            .load(resIdRes)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                    imageCIV.setImageBitmap(resource)
                    val bitmapView = getViewBitmap(view)
                    l(bitmapView)

                }
                override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
            })
    }


    private var mTileOverlay: TileOverlay? = null
    private fun showLayersMapDialog(){
        val dialog = LayersMapDialog(this)
        dialog.setMyEvent { mapType->

            when(mapType){
                LayersMapDialog.MAP_TYPE_GOOGLE_HYBRID->{
                    hMap!!.mapType = HuaweiMap.MAP_TYPE_NONE
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
                        matrix.postTranslate((-x * mDimension).toFloat(), (-y * mDimension).toFloat())

                        // Generate a Bitmap image.
                        val googleUrl = "https://mts3.google.com/vt/lyrs=y@186112443&hl=x-local&src=app&x=$x&y=$y&z=$zoom&s=Galile"
                        val bitmap = Picasso.get().load(googleUrl).get()
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        Tile(mDimension, mDimension, stream.toByteArray())

                    }
                    val options = TileOverlayOptions().tileProvider(mTileProvider).transparency(0f)

                    mTileOverlay = hMap!!.addTileOverlay(options)
                }
                LayersMapDialog.MAP_TYPE_GOOGLE_TERRAIN->{
                    hMap!!.mapType = HuaweiMap.MAP_TYPE_NONE
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
                        matrix.postTranslate((-x * mDimension).toFloat(), (-y * mDimension).toFloat())

                        // Generate a Bitmap image.
                        val googleUrl = "https://mts3.google.com/vt/lyrs=p@186112443&hl=x-local&src=app&x=$x&y=$y&z=$zoom&s=Galile"
                        val bitmap = Picasso.get().load(googleUrl).get()
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        Tile(mDimension, mDimension, stream.toByteArray())

                    }
                    val options = TileOverlayOptions().tileProvider(mTileProvider).transparency(0f)

                    mTileOverlay = hMap!!.addTileOverlay(options)
                }
                LayersMapDialog.MAP_TYPE_GOOGLE_SATELLITE->{
                    hMap!!.mapType = HuaweiMap.MAP_TYPE_NONE
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
                        matrix.postTranslate((-x * mDimension).toFloat(), (-y * mDimension).toFloat())

                        // Generate a Bitmap image.
                        val googleUrl = "https://mts3.google.com/vt/lyrs=s@186112443&hl=x-local&src=app&x=$x&y=$y&z=$zoom&s=Galile"
                        val bitmap = Picasso.get().load(googleUrl).get()
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        Tile(mDimension, mDimension, stream.toByteArray())

                    }
                    val options = TileOverlayOptions().tileProvider(mTileProvider).transparency(0f)

                    mTileOverlay = hMap!!.addTileOverlay(options)
                }

                LayersMapDialog.MAP_TYPE_HUAWEI_NORMAL->{
                    mTileOverlay?.remove()
                    mTileOverlay?.clearTileCache()
                    hMap!!.mapType = HuaweiMap.MAP_TYPE_NORMAL
                }
                LayersMapDialog.MAP_TYPE_HUAWEI_TERRAIN->{
                    mTileOverlay?.remove()
                    mTileOverlay?.clearTileCache()
                    hMap!!.mapType = HuaweiMap.MAP_TYPE_TERRAIN
                }
                LayersMapDialog.MAP_TYPE_HUAWEI_SATELLITE->{
                    mTileOverlay?.remove()
                    mTileOverlay?.clearTileCache()
                    hMap!!.mapType = HuaweiMap.MAP_TYPE_SATELLITE
                }

            }
        }
        dialog.show()
    }

    private fun event(){

        backIV.setOnClickListener {
            finish()
        }

        currentLocationFab.setOnClickListener {
            if(prefs.strUid != ""){
                gpsManage.requestGPS()
            }
        }

        layerFab.setOnClickListener {
            showLayersMapDialog()
        }
    }

    private fun initMap(){
        mSupportMapFragment = supportFragmentManager.findFragmentById(R.id.mapfragment_mapfragmentdemo) as SupportMapFragment?
        mSupportMapFragment?.getMapAsync(this)
    }

    override fun onMapReady(huaweiMap: HuaweiMap) {
        Log.d(TAG, "onMapReady: ")
        postList.clear()
        hMap = huaweiMap
        //hMap?.isMyLocationEnabled = true
        hMap!!.uiSettings?.isCompassEnabled = false

        if(intent.getStringExtra(KEY_EVENT) == "post"){
            //when click from map post
            val model = intent.getSerializableExtra("modelPost") as ModelPost
            postList.add(model)
            showDetailPostBottomSheetDialog(model)

            postList.forEach { model->

                val lat = model.latitude
                val long = model.longitude
                val latLng = LatLng(lat!!.toDouble(), long!!.toDouble())
                latLngList.add(latLng)

                if(model.images.isNotEmpty()){
                    setIconMarker(model.images[0]){ bitmap->
                        val marker = hMap!!.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
                        marker.tag = model
                    }
                }else{
                    when(model.category){
                        "คำถาม"->{
                            setIconMarker(R.drawable.ic_question_mark){ bitmap->
                                val marker = hMap!!.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
                                marker.tag = model
                            }
                        }
                        "เเบ่งปัน"->{
                            setIconMarker(R.drawable.ic_share){ bitmap->
                                val marker = hMap!!.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
                                marker.tag = model
                            }
                        }
                        "อีเว้นท์"->{
                            setIconMarker(R.drawable.ic_star){ bitmap->
                                val marker = hMap!!.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
                                marker.tag = model
                            }
                        }
                    }

                }
            }
            moveCameraMulti(hMap!!, latLngList)

        }else{
            //when click from map view
            val repositoryPost = RepositoryPost(this)
            repositoryPost.read { result, posts ->
                if(result == RepositoryPost.RESULT_SUCCESS){
                    postList.addAll(posts)
                    postList.forEach { model->

                        val lat = model.latitude
                        val long = model.longitude
                        val latLng = LatLng(lat!!.toDouble(), long!!.toDouble())
                        latLngList.add(latLng)

                        if(model.images.isNotEmpty()){
                            setIconMarker(model.images[0]){ bitmap->
                                val marker = hMap!!.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
                                marker.tag = model
                            }
                        }else{
                            when(model.category){
                                "คำถาม"->{
                                    setIconMarker(R.drawable.ic_question_mark){ bitmap->
                                        val marker = hMap!!.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
                                        marker.tag = model
                                    }
                                }
                                "เเบ่งปัน"->{
                                    setIconMarker(R.drawable.ic_share){ bitmap->
                                        val marker = hMap!!.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
                                        marker.tag = model
                                    }
                                }
                                "อีเว้นท์"->{
                                    setIconMarker(R.drawable.ic_star){ bitmap->
                                        val marker = hMap!!.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
                                        marker.tag = model
                                    }
                                }
                            }
                        }
                    }
                    moveCameraMulti(hMap!!, latLngList)
                }
            }
        }



        hMap!!.setOnMarkerClickListener { marker->

            hMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 18f), 2000, object : HuaweiMap.CancelableCallback{
                override fun onFinish() {
                    Log.i("fewvdawdwf", "animateCamera onFinish")
                    val model = marker.tag as ModelPost
                    if(model != null){
                        showDetailPostBottomSheetDialog(model)
                    }

                }
                override fun onCancel() {
                    Log.i("fewvdawdwf", "animateCamera onCancel")
                }

            })
            true
        }

    }

    private fun showDetailPostBottomSheetDialog(model: ModelPost){
        val dialog = DetailPostBottomDialog(this, model)
        dialog.show()
    }

    fun moveCameraMulti(mMap: HuaweiMap, list: ArrayList<LatLng>) {
        val bc = LatLngBounds.Builder()
        if (list.size != 0) {
            for (i in list.indices) {
                val item = list[i]
                bc.include(item)
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 150), 2000, null)
        }
    }

    private val categoryDisplayList = ArrayList<ModelCategory>()
    private var indexSelect = 0
    private fun addChipCategoryView() {

        categoryCG?.removeAllViews()
        categoryDisplayList.clear()
        categoryDisplayList.add(ModelCategory("ทั้งหมด", ""))
        categoryDisplayList.add(ModelCategory("คำถาม", "ic_question_mark"))
        categoryDisplayList.add(ModelCategory("เเบ่งปัน", "ic_share"))
        categoryDisplayList.add(ModelCategory("อีเว้นท์", "ic_star"))

        for (i in categoryDisplayList.indices) {
            val chip = Chip(this)
            chip.setChipBackgroundColorResource(R.color.selector_choice_state)
            val colors = ContextCompat.getColorStateList(this, R.color.selector_text_state)
            chip.setTextColor(colors)
            chip.text = categoryDisplayList[i].title
            chip.chipIconSize = 50f

            if(i != 0){
                Glide.with(this).asBitmap().apply(RequestOptions.centerCropTransform())
                    .load(resources.getIdentifier(categoryDisplayList[i].icon, "drawable", packageName))
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                            val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, resource)
                            circularBitmapDrawable.isCircular = false
                            chip.chipIcon = circularBitmapDrawable
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            }
            chip.isCheckedIconVisible = false
            chip.isCloseIconVisible = false
            chip.isClickable = true
            chip.isCheckable = true
            chip.elevation = 5f
            chip.tag = i
            categoryCG.addView(chip)

            chip.setOnClickListener {
                Log.i("dsafawfa", "This Tag: $i")
                if(indexSelect == i){
                    moveCameraMulti(hMap!!, latLngList)
                }else{
                    indexSelect = i
                    latLngList.clear()
                    hMap!!.clear()

                    if(i == 0){
                        postList.forEach { model->
                            val lat = model.latitude
                            val long = model.longitude
                            val latLng = LatLng(lat!!.toDouble(), long!!.toDouble())
                            latLngList.add(latLng)

                            if(model.images.isNotEmpty()){
                                setIconMarker(model.images[0]){ bitmap->
                                    val marker = hMap!!.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
                                    marker.tag = model
                                }
                            }else{
                                when(model.category){
                                    "คำถาม"->{
                                        setIconMarker(R.drawable.ic_question_mark){ bitmap->
                                            val marker = hMap!!.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
                                            marker.tag = model
                                        }
                                    }
                                    "เเบ่งปัน"->{
                                        setIconMarker(R.drawable.ic_share){ bitmap->
                                            val marker = hMap!!.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
                                            marker.tag = model
                                        }
                                    }
                                    "อีเว้นท์"->{
                                        setIconMarker(R.drawable.ic_star){ bitmap->
                                            val marker = hMap!!.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
                                            marker.tag = model
                                        }
                                    }
                                }
                            }//else end
                        }
                        moveCameraMulti(hMap!!, latLngList)
                    }else{
                        val postFilter = postList.filter { it.category ==  categoryDisplayList[i].title}
                        postFilter.forEach { model->
                            val lat = model.latitude
                            val long = model.longitude
                            val latLng = LatLng(lat!!.toDouble(), long!!.toDouble())
                            latLngList.add(latLng)

                            if(model.images.isNotEmpty()){
                                setIconMarker(model.images[0]){ bitmap->
                                    val marker = hMap!!.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
                                    marker.tag = model
                                }
                            }else{
                                when(model.category){
                                    "คำถาม"->{
                                        setIconMarker(R.drawable.ic_question_mark){ bitmap->
                                            val marker = hMap!!.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
                                            marker.tag = model
                                        }
                                    }
                                    "เเบ่งปัน"->{
                                        setIconMarker(R.drawable.ic_share){ bitmap->
                                            val marker = hMap!!.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
                                            marker.tag = model
                                        }
                                    }
                                    "อีเว้นท์"->{
                                        setIconMarker(R.drawable.ic_star){ bitmap->
                                            val marker = hMap!!.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
                                            marker.tag = model
                                        }
                                    }
                                }
                            }//else end
                        }
                        moveCameraMulti(hMap!!, latLngList)
                    }
                }
            }

            if (i == 0){
                chip.isChecked = true
            }
        }
    }
    inner class ModelCategory(
        var title: String? = null,
        var icon: String? = null
    )

}