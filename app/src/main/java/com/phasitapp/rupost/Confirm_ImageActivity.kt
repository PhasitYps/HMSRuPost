package com.phasitapp.rupost

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_confirm_image.*
import java.io.File
import java.io.FileInputStream

class Confirm_ImageActivity : AppCompatActivity() {
    private val dir = "/data/data/com.phasitapp.rupost/files/"
    private val TAG = "Work Task"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_image)
        val nameImage = intent.getStringExtra("name_image")
        val image = getImage(nameImage.toString())

        findViewById<ImageView>(R.id.imageView).setImageBitmap(image)

        Cancel_btn.setOnClickListener {
            startActivity(Intent(this, PostActivity::class.java))
            deletePhotoFromInternalStorage(nameImage.toString())
            finish()
        }
    }

    private fun getImage(filename: String): Bitmap {
        val f = File(dir, filename)
        return BitmapFactory.decodeStream(FileInputStream(f))
    }

    private fun deletePhotoFromInternalStorage(filename: String): Boolean{
        val file = File(dir, filename)
        android.util.Log.i(TAG, "Delete image success")
        return file.delete()
    }

}