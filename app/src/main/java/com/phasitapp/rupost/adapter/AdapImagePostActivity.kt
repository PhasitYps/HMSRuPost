package com.phasitapp.rupost.adapter

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.phasitapp.rupost.R
import com.phasitapp.rupost.dialog.ImageViewPageDialog
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.FileInputStream

class AdapImagePostActivity(private var activity: Activity, private val imageList: ArrayList<String>) :
    RecyclerView.Adapter<AdapImagePostActivity.ViewHolder>(){

    private val TAG = "AdapImagePostActivity"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_act, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder")
        val image = getImage(imageList[position])
        holder.imageView.setImageBitmap(image)

        holder.imageView.setOnClickListener {
            val images = ArrayList<String>()
            imageList.forEach {
                images.add(File(activity.filesDir, it).path)
            }
            val dialog = ImageViewPageDialog(activity, images, position)
            dialog.show()
        }

        holder.deleteCIV.setOnClickListener {
            imageList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.imageView)
        val deleteCIV = itemView.findViewById<CircleImageView>(R.id.deleteCIV)
    }

    private fun getImage(filename: String): Bitmap {
        val f = File(activity.filesDir, filename)
        return BitmapFactory.decodeStream(FileInputStream(f))
    }
}