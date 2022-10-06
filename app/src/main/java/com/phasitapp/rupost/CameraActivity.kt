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
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.SupportMapFragment
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.MarkerOptions
import com.phasitapp.rupost.Utils.convertViewToBitmap
import com.phasitapp.rupost.Utils.formatDate
import com.phasitapp.rupost.model.ModelPost
import kotlinx.android.synthetic.main.activity_camera.*
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity(), OnMapReadyCallback {
    var latitude: String?= null
    var longitude: String?= null
    var address_image: String? = null
    var name_image: String?= null

    val API = "b0400551b9c2882592551bfdf9978798"
    val TAG = "Work Task"

    private var gpsManage: GPSManage? = null
    private var imageCapture: ImageCapture? = null

    private var lat: Double? = null
    private var long: Double? = null

    private var SELECT_IMAGE: Int? = 1
    private var ModelInfo: ModelPost? = null

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var hMap: HuaweiMap

    private val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        init()
        initMap()
        event()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun initMap() {
        mapView.onCreate(null)
        mapView.getMapAsync(this)
    }

    private val T = Timer()
    private lateinit var currentDate: Date
    private fun init(){
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()

        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        T.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    currentDate = Date()
                    createDateTV.text = formatDate("dd MMM yyyy HH:mm:ss", Date())
                }
            }
        }, 1000, 1000)

    }

    override fun onMapReady(map: HuaweiMap) {
        Log.i(TAG, "This is MapReady")
        hMap = map
        hMap.uiSettings.isMapToolbarEnabled = false
        hMap.uiSettings.isCompassEnabled = false
        hMap.uiSettings.isZoomControlsEnabled = false
        hMap.uiSettings.setAllGesturesEnabled(false)

        gpsManage = GPSManage(this)
        gpsManage!!.requestGPS()
        gpsManage!!.setMyEvent(object : GPSManage.MyEvent{
            override fun onLocationChanged(currentLocation: Location) {
                Log.i(TAG, "location: $currentLocation")

                bgCurrentLocationLL.visibility = View.VISIBLE
                lat = currentLocation.latitude
                long = currentLocation.longitude

                latitudeTV.text = "${String.format("%.7f", lat)}"
                longitudeTV.text = "${String.format("%.7f", long)}"

                val latLng = LatLng(lat!!, long!!)

                hMap.clear()
                hMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f), 2000,object : HuaweiMap.CancelableCallback{
                    override fun onFinish() {
                        hMap.snapshot {
                            Glide.with(this@CameraActivity).load(it).into(imageMapIV)
                        }
                    }
                    override fun onCancel() {}
                })
                hMap.addMarker(MarkerOptions().position(latLng))

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
                            addressTV.text = address
                            address_image = address

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

                //weatherTask().execute()
            }

            override fun onDissAccessGPS() {

            }

        })

        bgCurrentLocationLL.visibility = View.GONE


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
                //get bitmap from image
                var bitmap = imageProxyToBitmap(image)
                bitmap = rotateBitmap(bitmap, 90f)

                //อย่าลืมลบ comment ก่อน commit (Note: Ford)
                //var image_temp = viewToBitmap(findViewById(R.id.status_image))
                //var temp = viewToBitmap(findViewById(R.id.temp_text))
                //var status = viewToBitmap(findViewById(R.id.status_text))

                //image_temp = resizeBitmap(image_temp!!, 180)
                //temp = resizeBitmap(temp!!, 100)
                //status = resizeBitmap(status!!, 230)

                //bitmap = combineImages(bitmap, image_temp, temp, status)!!

                val isSaveSuccessfully = savePhotoToInternalStorage(name, bitmap)
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

    fun combineImages(picture: Bitmap,
                      image_temp: Bitmap, temp: Bitmap, status: Bitmap): Bitmap? {
        val bmp = Bitmap.createBitmap(picture.width, picture.height, Bitmap.Config.ARGB_8888)
        val comboImage = Canvas(bmp)
        comboImage.drawBitmap(picture, 0f, 0f, null)
        comboImage.drawBitmap(image_temp, 770f, 60f, null)
        comboImage.drawBitmap(temp, 550f, 100f, null)
        comboImage.drawBitmap(status, 550f, 150f, null)
        return bmp
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

                    //อย่าลืมลบ Unit test ก่อน commit (Note: Ford)
                    latitude = "Unit test latitude..."
                    longitude = "Unit test longitude..."
                    address_image = "Unit test address image..."
                    ModelInfo = ModelPost(
                        latitude = latitude,
                        longitude = longitude,
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

    override fun onDestroy() {
        super.onDestroy()
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