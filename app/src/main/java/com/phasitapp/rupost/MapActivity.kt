package com.phasitapp.rupost

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.chip.Chip
import com.huawei.hms.maps.*
import com.huawei.hms.maps.model.MarkerOptions
import com.phasitapp.rupost.fragments.HomeFragment
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.categoryCG

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = "MapActivity"

    private var mSupportMapFragment: SupportMapFragment? = null

    private var hMap: HuaweiMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Initialize the SDK.
        addChipCategoryView()
        initMap()
        event()
    }

    private fun event(){

        backIV.setOnClickListener {
            finish()
        }
    }

    private fun initMap(){
        mSupportMapFragment = supportFragmentManager.findFragmentById(R.id.mapfragment_mapfragmentdemo) as SupportMapFragment?
        mSupportMapFragment?.getMapAsync(this)
    }

    override fun onMapReady(huaweiMap: HuaweiMap) {
        Log.d(TAG, "onMapReady: ")
        hMap = huaweiMap
        //hMap?.isMyLocationEnabled = true
        hMap?.uiSettings?.isCompassEnabled = false
        hMap?.mapType = HuaweiMap.MAP_TYPE_NORMAL
        huaweiMap.setOnMapClickListener {
            huaweiMap.addMarker(MarkerOptions().position(it))
        }
    }

    private val categoryDisplayList = ArrayList<ModelCategory>()
    private fun addChipCategoryView() {
        var indexSelect = 0

        categoryCG?.removeAllViews()
        categoryDisplayList.clear()
        categoryDisplayList.add(ModelCategory("ทั้งหมด", ""))
        categoryDisplayList.add(ModelCategory("คำถาม", "ic_question_mark"))
        categoryDisplayList.add(ModelCategory("เเบ่งปัน", "ic_share"))
        categoryDisplayList.add(ModelCategory("อีเว้นท์", "ic_star"))

        for (i in categoryDisplayList.indices) {
            val chip = Chip(this)
            chip.setChipBackgroundColorResource(R.color.selector_choice_state)
            val colors = ContextCompat.getColorStateList(this, R.color.selector_text_state)
            chip.setTextColor(colors)
            chip.text = categoryDisplayList[i].title
            chip.chipIconSize = 50f

            if(i != 0){
                Glide.with(this).asBitmap().apply(RequestOptions.centerCropTransform())
                    .load(resources.getIdentifier(categoryDisplayList[i].icon, "drawable", packageName))
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                            val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, resource)
                            circularBitmapDrawable.isCircular = false
                            chip.chipIcon = circularBitmapDrawable
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            }
            chip.isCheckedIconVisible = false
            chip.isCloseIconVisible = false
            chip.isClickable = true
            chip.isCheckable = true
            chip.elevation = 5f
            chip.tag = i
            categoryCG.addView(chip)

            chip.setOnClickListener {
                Log.i("dsafawfa", "This Tag: $i")
            }

            if (i == 0){
                chip.isChecked = true
            }
        }
    }
    inner class ModelCategory(
        var title: String? = null,
        var icon: String? = null
    )

}