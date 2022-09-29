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
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.huawei.hms.location.GetFromLocationRequest
import com.huawei.hms.location.HWLocation
import com.huawei.hms.location.LocationServices
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapView
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_camera.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URL
import java.net.URLEncoder
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    var latitude: Double?= null
    var longitude: Double?= null
    var address_image: String? = null
    var name_image: String?= null
    var getFromLocationRequest: GetFromLocationRequest? = null

    val API = "b0400551b9c2882592551bfdf9978798"
    val TAG = "Work Task"

    private var gpsManage: GPSManage? = null
    private var imageCapture: ImageCapture? = null
    private var SELECT_IMAGE: Int? = 1

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        supportActionBar?.hide()

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()

        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // GPS Setup
        gpsManage = GPSManage(this)
        gpsManage!!.requestGPS()
        gpsManage!!.setMyEvent(object : GPSManage.MyEvent{
            override fun onLocationChanged(currentLocation: Location) {
                Log.i(TAG, "location: $currentLocation")
                latitude = currentLocation.latitude
                longitude = currentLocation.longitude
                findViewById<TextView>(R.id.latitude).text = String.format("%f", latitude)
                findViewById<TextView>(R.id.longitude).text = String.format("%f", longitude)

                weatherTask().execute()

                val locale = Locale("en", "US")
                val geocoderService = LocationServices.getGeocoderService(this@CameraActivity, locale)
                getFromLocationRequest = GetFromLocationRequest(latitude!!, longitude!!, 5)
                geocoderService.getFromLocation(getFromLocationRequest)
                    .addOnSuccessListener{
                        findViewById<TextView>(R.id.address).text = HWLocation().city
                        Toast.makeText(this@CameraActivity, HWLocation().city.toString(), Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Log.i(TAG, "geocoderService Exception! $it")
                        Toast.makeText(this@CameraActivity, "geocoderService Exception! $it", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onDissAccessGPS() {
            }
        })

        //Huawei Map
        val map = HuaweiMapCamera().huaweiMap
        if (latitude != null && longitude !=null){
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude!!, longitude!!), 14f))
            map?.addMarker(MarkerOptions().position(LatLng(latitude!!, longitude!!)))
            map?.mapType = HuaweiMap.MAP_TYPE_NORMAL

            HuaweiMapCamera().mapView.tag = LatLng(latitude!!, longitude!!)
        }

        // Set up the listeners for take photo and video capture buttons
        event()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun event() {
        imageCaptureIV.setOnClickListener {
            takePhoto()
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())

        imageCapture.takePicture(cameraExecutor, object :
            ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                var bitmap = imageProxyToBitmap(image)
                bitmap = rotateBitmap(bitmap, 90f)

                // Get view to draw image
                var image_temp = viewToBitmap(findViewById(R.id.status_image))
                var temp = viewToBitmap(findViewById(R.id.temp_text))
                var status = viewToBitmap(findViewById(R.id.status_text))

                // Resize view
                image_temp = resizeBitmap(image_temp!!, 180)
                temp = resizeBitmap(temp!!, 100)
                status = resizeBitmap(status!!, 230)

                // Add view to image
                bitmap = combineImages(bitmap, image_temp, temp, status)!!

                // Save image
                val isSaveSuccessfully = savePhotoToInternalStorage(name, bitmap)
                if (isSaveSuccessfully) {
                    Log.i(TAG, "Photo saved successfully")
                    image.close()
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
        comboImage.drawBitmap(image_temp, 0f, 60f, null)
        comboImage.drawBitmap(temp, 170f, 100f, null)
        comboImage.drawBitmap(status, 170f, 150f, null)
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
            Log.i(TAG, "Failed because: " + e.message)
        }
        return bitmap
    }

    private fun openConfirm() {
        val intent = Intent(this@CameraActivity, Confirm_ImageActivity::class.java)
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        intent.putExtra("address", address_image)
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

                    setResult(RESULT_OK, Intent().putExtra("IMAGE_PATH", imagePath))
//                    setResult(RESULT_OK, Intent().putExtra("latitude", latitude))
//                    setResult(RESULT_OK, Intent().putExtra("longitude", longitude))
//                    setResult(RESULT_OK, Intent().putExtra("address", address_image))

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

    inner class HuaweiMapCamera() : OnMapReadyCallback {
        var huaweiMap: HuaweiMap? = null

        val mapView = findViewById<MapView>(R.id.mapView)

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

            Log.i(TAG, "This is MapReady")
        }

    }

    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            Log.i(TAG, "onPreExecute")
        }

        override fun doInBackground(vararg params: String?): String? {

            Log.i(TAG, "doInBackground")
            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&units=metric&appid=$API").readText(
                    Charsets.UTF_8
                )
                Log.i(TAG, "https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&units=metric&appid=$API")
            }catch (e: Exception){
                Log.i(TAG, "doInBackground: $e")
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            Log.i(TAG, "onPostExecute")

            try {
                val jsonObj = JSONObject(result)
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

                findViewById<TextView>(R.id.temp_text).text = temp
                findViewById<TextView>(R.id.status_text).text = weatherDescription
                findViewById<TextView>(R.id.address).text = address
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
            catch (e: java.lang.Exception) {
            }
        }
    }

}