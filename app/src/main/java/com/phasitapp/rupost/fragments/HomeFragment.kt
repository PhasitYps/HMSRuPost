package com.phasitapp.rupost.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.chip.Chip
import com.huawei.hms.maps.*
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.MarkerOptions
import com.phasitapp.rupost.MapActivity
import com.phasitapp.rupost.R
import com.phasitapp.rupost.adapter.AdapPost
import com.phasitapp.rupost.model.ModelPost
import com.phasitapp.rupost.utils.MapUtils
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(R.layout.fragment_home) {

    private val postList = ArrayList<ModelPost>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("sadasdas", "HomeFragment onViewCreated: ")

        addChipCategoryView()
        event()
        setData()
        setAdap()
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
            val chip = Chip(requireActivity())
            chip.setChipBackgroundColorResource(R.color.selector_choice_state)
            val colors = ContextCompat.getColorStateList(requireActivity(), R.color.selector_text_state)
            chip.setTextColor(colors)
            chip.text = categoryDisplayList[i].title
            chip.chipIconSize = 50f

            if(i != 0){
                Glide.with(this).asBitmap().apply(RequestOptions.centerCropTransform())
                    .load(resources.getIdentifier(categoryDisplayList[i].icon, "drawable", requireActivity().packageName))
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


    private fun event(){

        mapLL.setOnClickListener {
            val intent = Intent(requireActivity(), MapActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setData(){

        postList.add(ModelPost(latitude = "18.321361304849493", longitude = "99.39492037868074"))
        postList.add(ModelPost(latitude = "18.318336341259233", longitude = "99.40210869880752"))
        postList.add(ModelPost(latitude = "18.31712430395693", longitude = "99.39866474282977"))
        postList.add(ModelPost(latitude = "18.31712430395693", longitude = "99.39866474282977"))
        postList.add(ModelPost(latitude = "18.31712430395693", longitude = "99.39866474282977"))
        postList.add(ModelPost(latitude = "18.31712430395693", longitude = "99.39866474282977"))
    }

    private fun setAdap(){
        val adapter = AdapPost(requireActivity() , postList)
        val layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        postRCV.adapter = adapter
        postRCV.layoutManager = layoutManager
    }

    inner class ModelCategory(
        var title: String? = null,
        var icon: String? = null
    )
}