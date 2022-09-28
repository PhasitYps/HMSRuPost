package com.phasitapp.rupost.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapView
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.MarkerOptions
import com.phasitapp.rupost.R
import com.phasitapp.rupost.Utils
import com.phasitapp.rupost.Utils.formatCreateDate
import com.phasitapp.rupost.dialog.ImageViewPageDialog
import com.phasitapp.rupost.model.ModelComment
import com.phasitapp.rupost.model.ModelPost
import de.hdodenhof.circleimageview.CircleImageView

class AdapComments(private var activity: Activity, private val dataList: ArrayList<ModelComment>) :
    RecyclerView.Adapter<AdapComments.ViewHolder>() {

    private val TAG = "AdapImagePost"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_comment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder")
        Glide.with(activity).load(dataList[position].profile).into(holder.profileCIV)
        holder.usernameTV.text = dataList[position].username
        holder.messageTV.text = dataList[position].message
        holder.createDateTV.text = formatCreateDate(dataList[position].createDate.toString().toLong())
        holder.likeIV.setOnClickListener {

        }
        holder.commentIV.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val profileCIV = itemView.findViewById<CircleImageView>(R.id.profileCIV)
        val createDateTV = itemView.findViewById<TextView>(R.id.createDateTV)
        val usernameTV = itemView.findViewById<TextView>(R.id.usernameTV)
        val messageTV = itemView.findViewById<TextView>(R.id.messageTV)
        val commentIV = itemView.findViewById<ImageView>(R.id.commentIV)
        val likeIV = itemView.findViewById<ImageView>(R.id.likeIV)
    }

}