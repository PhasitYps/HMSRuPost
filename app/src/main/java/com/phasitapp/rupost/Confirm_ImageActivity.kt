package com.phasitapp.rupost

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import java.io.File
import java.io.FileInputStream

class Confirm_ImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_image)
        val nameImage = intent.getStringExtra("name_image")
        val image = getImage(nameImage.toString())

        findViewById<ImageView>(R.id.imageView).setImageBitmap(image)
    }

    private fun getImage(filename: String): Bitmap {

        val f = File("/data/data/com.phasitapp.rupost/files/", filename)
        val b = BitmapFactory.decodeStream(FileInputStream(f))
        return b
    }

}