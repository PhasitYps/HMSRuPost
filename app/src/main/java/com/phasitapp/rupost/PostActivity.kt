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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.phasitapp.rupost.adapter.AdapImagePost
import com.phasitapp.rupost.adapter.AdapImagePostActivity
import kotlinx.android.synthetic.main.activity_post.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PostActivity : AppCompatActivity() {
    private var PERMISSION_REQUEST: Int? = 0
    private var SELECT_IMAGE: Int? = 1
    private var CAMERA_IMAGE: Int? = 2
    private val imageList = arrayListOf<String>()

    val TAG = "Work Task"

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapImagePostActivity: AdapImagePostActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        Camera_btn.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivityForResult(intent, CAMERA_IMAGE!!)
        }

        Gallary_Btn.setOnClickListener {
            checkPermissionGallery()
        }

        Close_btn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun checkPermissionGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),PERMISSION_REQUEST!!)
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
        startActivityForResult(intent, SELECT_IMAGE!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            when (requestCode) {
                SELECT_IMAGE-> if (resultCode == Activity.RESULT_OK) {
                    val imageUri = data.data as Uri
                    val imageInByte = contentResolver.openInputStream(imageUri)?.readBytes()

                    val bitmap = rotateBitmap(byteArrayToBitmap(imageInByte!!), 90f)
                    val isSaveSuccessfully = savePhotoToInternalStorage(bitmap)
                    if (isSaveSuccessfully){
                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.i(TAG, "Failed to save photo")
                    }
                }

                CAMERA_IMAGE -> if (resultCode == Activity.RESULT_OK) {
                    val imagePath = data.getStringExtra("IMAGE_PATH")

                    Log.i(TAG, "onActivityResult PostActivity: $imagePath")
                    imageList.add(imagePath!!)
                    Log.i(TAG, imageList.toString())

                    initRecyclerView()
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

}