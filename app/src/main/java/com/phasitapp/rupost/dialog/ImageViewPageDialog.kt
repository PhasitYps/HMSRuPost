package com.phasitapp.rupost.dialog

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.phasitapp.rupost.R
import kotlinx.android.synthetic.main.dialog_image_view_page.*

class ImageViewPageDialog(
    private var activity: Activity,
    private val dataList: List<String>,
    private val currentImage: Int
) : Dialog(activity) {

    fun setMyEvents(l: (event: String) -> Unit) {

    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_image_view_page)
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT)
        setCancelable(false)

        setAdapPager()

        backIV.setOnClickListener {
            dismiss()
        }

        statusPageTV.text = "${currentImage+1}/${dataList.size}"
    }

    override fun onBackPressed() {
        super.onBackPressed()
        dismiss()
    }

    private fun setAdapPager() {
        val adapter = AdapPagerImagePost(activity, dataList)
        dataViewPager.adapter = adapter
        dataViewPager.setCurrentItem(currentImage, false)
        dataViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.i("hhhhhhh", "position: " + position)

                statusPageTV.text = "${position + 1}/${dataList.size}"
            }
        })
    }

    inner class AdapPagerImagePost(
        private val activity: Activity,
        private val dataList: List<String>
    ) : RecyclerView.Adapter<AdapPagerImagePost.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.listview_paper_image, parent, false)
            )

        override fun getItemCount(): Int = dataList.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            Log.i("hhhhhhhy", "onBindViewHolder")
            Glide.with(activity).load(dataList[position]).into(holder.imagePostIV)

        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val imagePostIV = itemView.findViewById<ImageView>(R.id.imagePostIV)

        }
    }

}