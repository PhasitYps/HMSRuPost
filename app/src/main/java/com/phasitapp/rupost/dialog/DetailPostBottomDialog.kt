package com.phasitapp.rupost.dialog

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.MarkerOptions
import com.phasitapp.rupost.R
import com.phasitapp.rupost.Utils
import com.phasitapp.rupost.adapter.AdapImagePost
import com.phasitapp.rupost.adapter.AdapPost
import com.phasitapp.rupost.model.ModelPost
import kotlinx.android.synthetic.main.bottomsheet_detail_post.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.io.File
import java.util.*

class DetailPostBottomDialog(private val activity: Activity, private val modelPost: ModelPost): BottomSheetDialog(activity, R.style.SheetDialog) {

    init {
        val bottomSheetView: View = activity.layoutInflater.inflate(R.layout.bottomsheet_detail_post, null)
        setContentView(bottomSheetView)
        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheetView.parent as View)
        val bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // do something
                Log.i("Jfkdjkfjdfdf","onStateChanged")
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // do something
                Log.i("Jfkdjkfjdfdf","onSlide")
            }
        }

        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback)
        setDetail()
    }

    private fun setDetail() {
        var lat = modelPost.latitude
        var long = modelPost.longitude

        Glide.with(activity).load(modelPost.profile).into(profileIV)
        usernameTV.text =modelPost.username
        createDateTV.text = formatCreateDate(modelPost.createDate.toString().toLong())
        titleTV.text = if(modelPost.title != null) modelPost.title else ""
        descriptionTV.text = if (modelPost.desciption != null) modelPost.desciption else ""

        setAdap()
    }

    private fun setAdap(){
        //set adap image post
        val adapter = AdapImagePost(activity, modelPost.images)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        imageRCV.adapter = adapter
        imageRCV.layoutManager = layoutManager
    }

    private fun formatCreateDate(createDate: Long): String{
        val currentDate = System.currentTimeMillis()
        val pastTime = currentDate - createDate
        //259200000 = 3 day
        //86,400,000 = 1 day
        //3,600,000 = 1 h
        //60,000 = 1 m
        //1,000 = 1 s
        Log.i("fweewfwe", "pastTime: $pastTime")
        if(pastTime > 259200000){
            return Utils.formatDate("dd MMM yyyy HH:mm", Date(createDate))
        }else if(pastTime >= 86400000){
            val day = (pastTime/86400000).toInt()
            return "$day วัน ที่เเล้ว"
        }else if(pastTime >= 3600000){
            val h = (pastTime/3600000).toInt()
            return "$h ชม. ที่เเล้ว"
        }else if(pastTime >= 60000){
            val m = (pastTime/60000).toInt()
            return "$m น. ที่เเล้ว"
        }else{
            val s = (pastTime/1000).toInt()
            return "$s วิ. ที่เเล้ว"
        }
    }
}