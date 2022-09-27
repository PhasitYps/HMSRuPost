package com.phasitapp.rupost

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_confirm_image.*
import java.io.File
import java.io.FileInputStream

class Confirm_ImageActivity : AppCompatActivity() {

    private val dir = "/data/data/com.phasitapp.rupost/files/"
    private val TAG = "Work Task"

    private var nameImage: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_image)
        nameImage = intent.getStringExtra("name_image")
        val image = getImage(nameImage.toString())

        findViewById<ImageView>(R.id.imageView).setImageBitmap(image)

        Cancel_btn.setOnClickListener {
            Log.i(TAG, "Cancel")
            deletePhotoFromInternalStorage(nameImage.toString())
            finish()
        }

        Select_btn.setOnClickListener {
            sendImagePathtoCamera()
        }
    }

    private fun sendImagePathtoCamera(){
        setResult(RESULT_OK, Intent().putExtra("IMAGE_PATH", nameImage))
        Log.i(TAG, "Send IMAGE_PATH to Camera $nameImage")
        finish()
    }

    private fun getImage(filename: String): Bitmap {
        val f = File(dir, filename)
        return BitmapFactory.decodeStream(FileInputStream(f))
    }

    private fun deletePhotoFromInternalStorage(filename: String): Boolean{
        val file = File(dir, filename)
        Log.i(TAG, "Delete image success")
        return file.delete()
    }

}