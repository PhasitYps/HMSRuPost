package com.phasitapp.rupost

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.location.Location
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.huawei.hms.maps.*
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.MarkerOptions
import com.phasitapp.rupost.Utils.convertViewToBitmap
import com.phasitapp.rupost.Utils.formatDate
import com.phasitapp.rupost.helper.GeocodingApi
import com.phasitapp.rupost.model.ModelPost
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_camera.addressTV
import kotlinx.android.synthetic.main.activity_camera.bgCurrentLocationLL
import kotlinx.android.synthetic.main.activity_camera.createDateTV
import kotlinx.android.synthetic.main.activity_camera.imageMapIV
import kotlinx.android.synthetic.main.activity_camera.latitudeTV
import kotlinx.android.synthetic.main.activity_camera.longitudeTV
import kotlinx.android.synthetic.main.activity_camera.mapView
import kotlinx.android.synthetic.main.activity_camera.status_image
import kotlinx.android.synthetic.main.activity_camera.status_text
import kotlinx.android.synthetic.main.activity_camera.temp_text
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.IOException
import java.net.URL
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity(), OnMapReadyCallback {
    var address_image: String? = null
    var name_image: String?= null

    val API = "b0400551b9c2882592551bfdf9978798"
    val TAG = "CameraActivityTAG"

    private var gpsManage: GPSManage? = null
    private var imageCapture: ImageCapture? = null

    private var lat: Double? = null
    private var long: Double? = null

    private var currentDate: Date = Date()

    private var SELECT_IMAGE: Int? = 1
    private var ModelInfo: ModelPost? = null

    private lateinit var cameraExecutor: ExecutorService
    private var hMap: HuaweiMap? = null
    private lateinit var geocodingApi: GeocodingApi

    private val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(this)
        setContentView(R.layout.activity_camera)
        Log.i(TAG, "CameraActivity start")
        init()
        event()
        cameraExecutor = Executors.newSingleThreadExecutor()
        Log.i(TAG, "CameraActivity end")
    }

    private fun initMap() {
        mapView.onCreate(null)
        mapView.getMapAsync(this)
    }

    private val T = Timer()
    private var isInitMap = false
    private fun init(){
        // Request camera permissions
        createDateTV.text = formatDate("", currentDate)

        bgCurrentLocationLL.visibility = View.GONE
        bgWeatherLL.visibility = View.INVISIBLE

        if (allPermissionsGranted()) {
            startCamera()

        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        gpsManage = GPSManage(this)
        gpsManage!!.requestGPS()
        gpsManage!!.setMyEvent(object : GPSManage.MyEvent{
            override fun onLocationChanged(currentLocation: Location) {
                Log.i(TAG, "location: ${currentLocation.latitude}, ${currentLocation.longitude}")

                bgCurrentLocationLL.visibility = View.VISIBLE
                lat = currentLocation.latitude
                long = currentLocation.longitude

                if(hMap == null && !isInitMap){
                    isInitMap = true
                    initMap()
                }else if(hMap != null){

                    try {

                        latitudeTV.text = "${String.format("%.7f", lat)}"
                        longitudeTV.text = "${String.format("%.7f", long)}"

                        val latLng = LatLng(lat!!, long!!)

                        hMap!!.clear()
                        hMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        hMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f), 1000,object : HuaweiMap.CancelableCallback{
                            override fun onFinish() {
                                hMap!!.snapshot {
                                    Glide.with(this@CameraActivity).load(it).into(imageMapIV)
                                }
                            }
                            override fun onCancel() {}
                        })
                        hMap!!.addMarker(MarkerOptions().position(latLng))

                        Thread{
                            try{
                                Log.i(TAG, "start Api")

                                val response = URL("https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$long&units=metric&appid=$API").readText(Charsets.UTF_8)
                                Log.i(TAG, "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$long&units=metric&appid=$API")

                                val jsonObj = JSONObject(response)
                                val main = jsonObj.getJSONObject("main")
                                val sys = jsonObj.getJSONObject("sys")
                                val wind = jsonObj.getJSONObject("wind")
                                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                                val updatedAt: Long = jsonObj.getLong("dt")
                                val updatedAtText = "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                                    Date(updatedAt*1000)
                                )
                                val temp = String.format("%.0f°C", main.getDouble("temp"))
                                val tempMin = "Min Temp: %.0f°C".format(main.getDouble("temp_min"))
                                val tempMax = "Max Temp: %.0f°C".format(main.getDouble("temp_max"))
                                val pressure = main.getString("pressure")
                                val humidity = main.getString("humidity")

                                val sunrise:Long = sys.getLong("sunrise")
                                val sunset:Long = sys.getLong("sunset")
                                val windSpeed = wind.getString("speed")
                                val weatherDescription = weather.getString("description")
                                val address = jsonObj.getString("name")
                                val icon = weather.getString("icon")
                                //val address = jsonObj.getString("name")+", "+sys.getString("country")



                                runOnUiThread {
                                    // Stuff that updates the UI
                                    bgWeatherLL.visibility = View.VISIBLE
                                    temp_text.text = temp
                                    status_text.text = weatherDescription

                                    geocodingApi.getAddressByLatLng(lat!!, long!!){ address->
                                        Log.i(TAG, "getAddressByLatLng: " + address)
                                        try{
                                            addressTV.text = address

                                        }catch (e: Exception){
                                            Log.i(TAG, "getAddressByLatLng error: " + e.message)
                                        }
                                    }

                                    when(icon){
                                        "01d" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.day_1)
                                        "01n" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.night_1)
                                        "02d" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.day_2)
                                        "02n" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.night_2)
                                        "03d" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.day_3)
                                        "03n" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.night_3)
                                        "04d" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.day_4)
                                        "04n" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.night_4)
                                        "09d" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.day_9)
                                        "09n" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.night_9)
                                        "10d" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.day_10)
                                        "10n" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.night_10)
                                        "11d" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.day_11)
                                        "11n" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.night_11)
                                        "13d" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.day_13)
                                        "13n" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.night_13)
                                        "50d" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.day_50)
                                        "50n" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.night_50)
                                    }
                                }
                            }catch (e: Exception){
                                Log.i(TAG, "error: ${e.message}")
                            }

                        }.start()

                    }catch (e:Exception){
                        Toast.makeText(this@CameraActivity, "เกิดข้อผิดพลาดติดต่อ ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

            }

            override fun onDissAccessGPS() {

            }

        })

        T.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    currentDate = Date()
                    createDateTV.text = formatDate("dd MMM yyyy HH:mm:ss", Date())
                }
            }
        }, 1000, 1000)

        geocodingApi = GeocodingApi(this)

    }

    override fun onMapReady(map: HuaweiMap) {
        Log.i(TAG, "This is MapReady")
        hMap = map
        hMap!!.uiSettings.isMapToolbarEnabled = false
        hMap!!.uiSettings.isCompassEnabled = false
        hMap!!.uiSettings.isZoomControlsEnabled = false
        hMap!!.uiSettings.setAllGesturesEnabled(false)

        try {

            latitudeTV.text = "${String.format("%.7f", lat)}"
            longitudeTV.text = "${String.format("%.7f", long)}"

            val latLng = LatLng(lat!!, long!!)
            hMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            hMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f), 1500,object : HuaweiMap.CancelableCallback{
                override fun onFinish() {
                    hMap!!.snapshot {
                        Glide.with(this@CameraActivity).load(it).into(imageMapIV)
                    }
                }
                override fun onCancel() {}
            })
            hMap!!.addMarker(MarkerOptions().position(latLng))

            Thread{
                try{
                    Log.i(TAG, "start Api")

                    val response = URL("https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$long&units=metric&appid=$API").readText(Charsets.UTF_8)
                    Log.i(TAG, "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$long&units=metric&appid=$API")

                    val jsonObj = JSONObject(response)
                    val main = jsonObj.getJSONObject("main")
                    val sys = jsonObj.getJSONObject("sys")
                    val wind = jsonObj.getJSONObject("wind")
                    val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                    val updatedAt: Long = jsonObj.getLong("dt")
                    val updatedAtText = "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                        Date(updatedAt*1000)
                    )
                    val temp = String.format("%.0f°C", main.getDouble("temp"))
                    val tempMin = "Min Temp: %.0f°C".format(main.getDouble("temp_min"))
                    val tempMax = "Max Temp: %.0f°C".format(main.getDouble("temp_max"))
                    val pressure = main.getString("pressure")
                    val humidity = main.getString("humidity")

                    val sunrise:Long = sys.getLong("sunrise")
                    val sunset:Long = sys.getLong("sunset")
                    val windSpeed = wind.getString("speed")
                    val weatherDescription = weather.getString("description")
                    val address = jsonObj.getString("name")
                    val icon = weather.getString("icon")
                    //val address = jsonObj.getString("name")+", "+sys.getString("country")



                    runOnUiThread {
                        // Stuff that updates the UI
                        temp_text.text = temp
                        status_text.text = weatherDescription
                        bgWeatherLL.visibility = View.VISIBLE

                        geocodingApi.getAddressByLatLng(lat!!, long!!){ address->
                            Log.i(TAG, "getAddressByLatLng: " + address)
                            try{
                                addressTV.text = address

                            }catch (e: Exception){
                                Log.i(TAG, "getAddressByLatLng error: " + e.message)
                            }
                        }

                        when(icon){
                            "01d" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.day_1)
                            "01n" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.night_1)
                            "02d" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.day_2)
                            "02n" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.night_2)
                            "03d" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.day_3)
                            "03n" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.night_3)
                            "04d" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.day_4)
                            "04n" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.night_4)
                            "09d" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.day_9)
                            "09n" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.night_9)
                            "10d" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.day_10)
                            "10n" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.night_10)
                            "11d" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.day_11)
                            "11n" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.night_11)
                            "13d" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.day_13)
                            "13n" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.night_13)
                            "50d" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.day_50)
                            "50n" -> findViewById<ImageView>(R.id.status_image).setImageResource(R.drawable.night_50)
                        }
                    }
                }catch (e: Exception){
                    Log.i(TAG, "error: ${e.message}")
                }

            }.start()

        }catch (e:Exception){
            Toast.makeText(this@CameraActivity, "เกิดข้อผิดพลาดติดต่อ ${e.message}", Toast.LENGTH_SHORT).show()
        }


    }

    private fun event() {

        imageCaptureIV.setOnClickListener {
            T.cancel()
            takePhoto()
        }
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())

        // Set up image capture listener, which is triggered after photo has
        // been taken

        imageCapture.takePicture(cameraExecutor, object :
            ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                Log.i(TAG, "takePicture")
                //get bitmap from image
                var bitmap = imageProxyToBitmap(image)
                bitmap = rotateBitmap(bitmap, 90f)
                bitmap = resizeBitmap(bitmap, 1500)

                Log.i(TAG, "width: " + bitmap.width)
                Log.i(TAG, "height: " + bitmap.height)

                val view: View = LayoutInflater.from(this@CameraActivity).inflate(R.layout.view_camera_image, null)
                view.layout(0, 0, bitmap.width,  bitmap.height);
                view.findViewById<ImageView>(R.id.imageIV).setImageBitmap(bitmap)
                view.findViewById<TextView>(R.id.createDateTV).text = formatDate("dd MMM yyyy HH:mm:ss", currentDate)

                if(lat != null && long != null){
                    view.findViewById<TextView>(R.id.latitudeTV).text = "${String.format("%.7f", lat)}"
                    view.findViewById<TextView>(R.id.longitudeTV).text = "${String.format("%.7f", long)}"
                    view.findViewById<TextView>(R.id.temp_text).text = temp_text.text
                    view.findViewById<TextView>(R.id.status_text).text = status_text.text
                    view.findViewById<ImageView>(R.id.status_image).setImageBitmap(status_image.drawable.toBitmap())
                    view.findViewById<TextView>(R.id.addressTV).text = addressTV.text
                    view.findViewById<ImageView>(R.id.imageMapIV).setImageDrawable(imageMapIV.drawable)

                    view.findViewById<LinearLayout>(R.id.bgCurrentLocationLL).visibility = View.VISIBLE
                    view.findViewById<LinearLayout>(R.id.bgWeatherLL).visibility = View.VISIBLE
                }else{
                    view.findViewById<LinearLayout>(R.id.bgCurrentLocationLL).visibility = View.GONE
                    view.findViewById<LinearLayout>(R.id.bgWeatherLL).visibility = View.INVISIBLE
                }

                val fullImageBitmap = convertViewToBitmap(view)


                Log.i(TAG, "fullImageBitmap: " + fullImageBitmap)

                val isSaveSuccessfully = savePhotoToInternalStorage(name, fullImageBitmap!!)
                if (isSaveSuccessfully) {
                    Log.i(TAG, "Photo saved successfully")
                    image.close();
                    openConfirm()
                } else {
                    Log.i(TAG, "Failed to save photo")
                }
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
            }

        })
    }

    fun resizeBitmap(source: Bitmap, maxLength: Int): Bitmap {
        try {
            if (source.height >= source.width) {
                if (source.height <= maxLength) { // if image height already smaller than the required height
                    return source
                }

                val aspectRatio = source.width.toDouble() / source.height.toDouble()
                val targetWidth = (maxLength * aspectRatio).toInt()
                val result = Bitmap.createScaledBitmap(source, targetWidth, maxLength, false)

                return result
            } else {
                if (source.width <= maxLength) { // if image width already smaller than the required width
                    return source
                }

                val aspectRatio = source.height.toDouble() / source.width.toDouble()
                val targetHeight = (maxLength * aspectRatio).toInt()

                val result = Bitmap.createScaledBitmap(source, maxLength, targetHeight, false)
                return result
            }
        } catch (e: Exception) {
            return source
        }
    }

    private fun viewToBitmap(v: View): Bitmap? {
        var bitmap: Bitmap? = null

        try {
            bitmap = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.i(TAG, "Failed because:" + e.message)
        }
        return bitmap
    }

    private fun openConfirm() {
        val intent = Intent(this@CameraActivity, Confirm_ImageActivity::class.java)
        intent.putExtra("name_image", name_image)
        Log.i(TAG, "Go to Confirm Image Activity")

        startActivityForResult(intent, SELECT_IMAGE!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            when (requestCode) {
                SELECT_IMAGE-> if (resultCode == Activity.RESULT_OK) {
                    val imagePath = data.getStringExtra("IMAGE_PATH")
                    Log.i(TAG, "onActivityResult CameraActivity: $imagePath")

                    ModelInfo = ModelPost(
                        latitude = lat.toString(),
                        longitude = long.toString(),
                        address = address_image,
                        images = arrayListOf(imagePath!!)
                    )

                    setResult(RESULT_OK, Intent().putExtra("ModelInfoFromCamera", ModelInfo))
                    Log.i(TAG, "Send Data to Post Activity...")
                    Log.i(TAG, "Go to Post Activity")
                    finish()
                }
            }
        }
    }

    fun rotateBitmap(source: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height, matrix, true
        )
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun savePhotoToInternalStorage(filename: String, bmp: Bitmap): Boolean {
        return try {
            openFileOutput("$filename.jpg", MODE_PRIVATE).use { stream ->
                if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IOException("Couldn't save bitmap.")
                }
            }
            name_image = "$filename.jpg"
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        cameraExecutor.shutdown()
        gpsManage?.close()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = mutableListOf(Manifest.permission.CAMERA).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1001){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                gpsManage?.requestGPS()
            }else{
                gpsManage?.requestGPS()
            }
        }
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}