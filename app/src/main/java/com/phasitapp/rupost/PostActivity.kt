package com.phasitapp.rupost

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.model.*
import com.phasitapp.rupost.adapter.AdapImagePostActivity
import com.phasitapp.rupost.dialog.SetLocationDialog
import com.phasitapp.rupost.helper.GeocodingApi
import com.phasitapp.rupost.model.ModelPost
import com.phasitapp.rupost.repository.RepositoryPost
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_post.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class PostActivity : AppCompatActivity(), OnMapReadyCallback {
    private val PERMISSION_REQUEST: Int = 0
    private val SELECT_IMAGE: Int = 1
    private val CAMERA_IMAGE: Int = 2
    private var model = ModelPost()
    private val CATEGORY = "category"

    private val imageList = arrayListOf<String>()

    val TAG = "Work Task"

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapImagePostActivity: AdapImagePostActivity

    private var hMap: HuaweiMap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        init()
        event()

    }

    private fun initMap() {

        mapView.onCreate(null)
        mapView.getMapAsync(this)
    }

    private fun init(){

        val SELECT_CATEGORY = intent.getStringExtra(CATEGORY)
        Log.i(TAG, "${SELECT_CATEGORY!!}")
        dropdown.setText(SELECT_CATEGORY)

        val items = listOf("คำถาม", "แบ่งปัน", "อีเวนท์")
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        dropdown.setAdapter(adapter)

        updateUI()
    }
    private fun event(){
        Camera_btn.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivityForResult(intent, CAMERA_IMAGE)
        }

        Gallary_Btn.setOnClickListener {
            checkPermissionGallery()
        }

        Close_btn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            for (i in imageList) {
                deletePhotoFromInternalStorage(i)
            }
            finish()
        }

        Post_btn.setOnClickListener {

            val imagePath = arrayListOf<String>()
            for (i in imageList) {
                imagePath.add("$filesDir$i")
            }

            model.category = dropdown.text.toString()
            model.title = TitleEDT.text.toString()
            model.desciption = DesciptionEDT.text.toString()
            model.viewer = 0
            model.createDate = "${System.currentTimeMillis()}"
            model.updateDate = "${System.currentTimeMillis()}"
            model.images = imagePath
            model.targetGroup = "สาธารณะ"


            if (model.latitude == null && model.longitude == null){
                Toast.makeText(this, "โปรดเลือกพิกัดหรือถ่ายรูปจากในแอพ...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (model.images.isEmpty()){
                Toast.makeText(this, "โปรดเลือกรูปก่อน...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val geocodingApi = GeocodingApi(this)
            geocodingApi.getAddressByLatLng(model.latitude!!.toDouble(), model.longitude!!.toDouble()){ address ->
                Log.i("PostActivityTAg", "This is address: " + address)
                model.address = address
                Log.i(TAG, "setOnClickListener: " +
                        "\nCategory: ${model.category} " +
                        "\nTitle: ${model.title} " +
                        "\nDesciption: ${model.desciption} " +
                        "\nLatitude: ${model.latitude} " +
                        "\nLongitude: ${model.longitude} " +
                        "\nAddress: ${model.address}" +
                        "\nCreateDate: ${model.createDate} " +
                        "\nUpdateDate: ${model.updateDate} " +
                        "\nImages: ${model.images.size}")


                val reposiPost = RepositoryPost(this)
                reposiPost.post(model) { result ->
                    when (result) {
                        RepositoryPost.RESULT_SUCCESS -> {
                            Toast.makeText(this, "โพสต์เรียบร้อย", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        RepositoryPost.RESULT_FAIL -> {
                            Toast.makeText(this, "เกิดข้อผิดพลาด", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }

            }
        }

        selectLocationRL.setOnClickListener {
            showSetMapDialog()
        }
    }

    private fun showSetMapDialog(){
        val dialog = SetLocationDialog(this)
        dialog.setMyEvent{ latLng ->
            model.latitude = latLng.latitude.toString()
            model.longitude = latLng.longitude.toString()

            updateUI()
        }
        dialog.show()
    }

    private var currentMarker: Marker? = null
    private fun updateUI(){

        if(model.latitude != null && model.longitude != null){
            bgMapViewCV.visibility = View.VISIBLE
            selectLocationRL.visibility = View.GONE
            if(hMap == null){
                initMap()
            }else{
                currentMarker?.remove()

                val latlng = LatLng(model.latitude!!.toDouble(), model.longitude!!.toDouble())
                hMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17f))
                currentMarker = hMap!!.addMarker(MarkerOptions().position(latlng))

            }
        }else{
            bgMapViewCV.visibility = View.GONE
            selectLocationRL.visibility = View.VISIBLE
        }
    }

    private fun checkPermissionGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),PERMISSION_REQUEST)
            } else {
                openGallery()
            }
        } else {
            openGallery()
        }
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME,true)
        startActivityForResult(intent, SELECT_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            when (requestCode) {
                SELECT_IMAGE-> if (resultCode == Activity.RESULT_OK) {
                    val imageUri = data.data as Uri
                    val imageInByte = contentResolver.openInputStream(imageUri)?.readBytes()

                    val bitmap = byteArrayToBitmap(imageInByte!!)
                    val isSaveSuccessfully = savePhotoToInternalStorage(bitmap)
                    if (isSaveSuccessfully){
                        Log.i(TAG, "Success to save photo from gallery ")
                    } else {
                        Log.i(TAG, "Failed to save photo")
                    }
                }

                CAMERA_IMAGE -> if (resultCode == Activity.RESULT_OK) {

                    val model = data.getSerializableExtra("ModelInfoFromCamera") as ModelPost
                    Log.i(TAG,"From getSerializableExtra (latitude): ${model.latitude}")
                    Log.i(TAG,"From getSerializableExtra (longitude): ${model.longitude}")
                    Log.i(TAG,"From getSerializableExtra (address): ${model.address}")
                    Log.i(TAG,"From getSerializableExtra (images): ${model.images[0]}")

                    Log.i(TAG,"Insert imageList")
                    imageList.add(model.images[0])
                    Log.i(TAG, imageList.toString())

                    this.model.latitude = model.latitude
                    this.model.longitude = model.longitude

                    //set map
                    Log.i(TAG, "Model save latitude, longitude, address successfully")
                    initRecyclerView()
                }
            }
        }
    }

    private fun byteArrayToBitmap(data: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }

    private fun savePhotoToInternalStorage(bmp: Bitmap): Boolean {
        val filename = SimpleDateFormat(PostActivity.FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        return try {
            openFileOutput("$filename.jpg", MODE_PRIVATE).use { stream ->
                if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IOException("Couldn't save bitmap.")
                }
            }
            imageList.add("$filename.jpg")
            Log.i(TAG, imageList.toString())
            initRecyclerView()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun initRecyclerView(){
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.visibility = View.VISIBLE
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        adapImagePostActivity = AdapImagePostActivity(this, imageList)
        recyclerView.adapter = adapImagePostActivity
    }

    private fun deletePhotoFromInternalStorage(filename: String): Boolean{
        val file = File(filesDir, filename)
        Log.i(TAG, "Delete image success")
        return file.delete()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    private var mTileOverlay: TileOverlay? = null
    override fun onMapReady(map: HuaweiMap?) {
        hMap = map
        hMap!!.uiSettings.isCompassEnabled = false
        hMap!!.uiSettings.isZoomControlsEnabled = false
        hMap!!.uiSettings.setAllGesturesEnabled(false)
        hMap!!.uiSettings.isMapToolbarEnabled = false


        val latlng = LatLng(model.latitude!!.toDouble(), model.longitude!!.toDouble())
        hMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17f))
        currentMarker = hMap!!.addMarker(MarkerOptions().position(latlng))

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
            matrix.postTranslate(
                (-x * mDimension).toFloat(),
                (-y * mDimension).toFloat()
            )

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

}