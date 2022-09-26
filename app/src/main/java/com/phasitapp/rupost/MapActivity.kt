package com.phasitapp.rupost

import android.app.ActionBar
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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
import com.huawei.hms.maps.model.animation.Animation
import com.phasitapp.rupost.Utils.getViewBitmap
import com.phasitapp.rupost.dialog.DetailPostBottomDialog
import com.phasitapp.rupost.dialog.LayersMapDialog
import com.phasitapp.rupost.helper.Prefs
import com.phasitapp.rupost.model.ModelPost
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.dialog_themes_map.*
import kotlinx.android.synthetic.main.fragment_home.categoryCG
import java.io.ByteArrayOutputStream


class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = "MapActivity"

    private var mSupportMapFragment: SupportMapFragment? = null

    private var hMap: HuaweiMap? = null
    private lateinit var gpsManage: GPSManage
    private lateinit var prefs : Prefs

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
                val option = MarkerOptions().position(latLng)

                currentMarker?.remove()
                setIconMarker(option, prefs.strPhotoUri!!)
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

    private fun setIconMarker(options: MarkerOptions, image: String) {
        val view: View = LayoutInflater.from(this).inflate(R.layout.map_marker_image, null)
        view.layout(0, 0, 240, 240);
        val imageCIV = view.findViewById(R.id.imageCIV) as CircleImageView

        Glide.with(this)
            .asBitmap()
            .load(image)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                    imageCIV.setImageBitmap(resource)
                    val bitmap = getViewBitmap(view)
                    options.icon(BitmapDescriptorFactory.fromBitmap(bitmap))

                    currentMarker = hMap?.addMarker(options)
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
        hMap = huaweiMap
        //hMap?.isMyLocationEnabled = true
        hMap!!.uiSettings?.isCompassEnabled = false

        if(intent.getStringExtra(KEY_EVENT) == "post"){
            val model = intent.getSerializableExtra("modelPost") as ModelPost

            val lat = model.latitude
            val long = model.longitude
            val latLng = LatLng(lat!!.toDouble(), long!!.toDouble())
            val marker = MarkerOptions().position(latLng)

            hMap!!.addMarker(marker)
            hMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

            showDetailPostBottomSheetDialog(model)

        }

        hMap!!.setOnMarkerClickListener {

            it.startAnimation()
            hMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(it.position, 15f), object : HuaweiMap.CancelableCallback{
                override fun onFinish() {
                    Log.i("fewvdawdwf", "animateCamera onFinish")

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

    private val categoryDisplayList = ArrayList<ModelCategory>()
    private fun addChipCategoryView() {
        var indexSelect = 0

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