package com.phasitapp.rupost.dialog

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phasitapp.rupost.*
import com.phasitapp.rupost.adapter.AdapImagePost
import com.phasitapp.rupost.model.ModelPost
import com.phasitapp.rupost.model.ModelUser
import com.phasitapp.rupost.repository.RepositoryPost
import com.phasitapp.rupost.repository.RepositoryUser
import kotlinx.android.synthetic.main.bottom_sheet_detail_post.*
import java.util.*

class DetailPostBottomDialog(private val activity: Activity, private val modelPost: ModelPost) :
    BottomSheetDialog(activity, R.style.SheetDialog) {

    private val repositoryUser = RepositoryUser(activity)
    private val repositoryPost = RepositoryPost(activity)

    init {
        val bottomSheetView: View =
            activity.layoutInflater.inflate(R.layout.bottom_sheet_detail_post, null)
        setContentView(bottomSheetView)
        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(bottomSheetView.parent as View)
        val bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback =
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    // do something
                    Log.i("Jfkdjkfjdfdf", "onStateChanged")
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // do something
                    Log.i("Jfkdjkfjdfdf", "onSlide")
                }
            }

        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback)
        setDetail()

        commentIV.setOnClickListener {
            val intent = Intent(activity, CommentsActivity::class.java)
            intent.putExtra(KEY_DATA, modelPost)
            activity.startActivity(intent)
        }

        bgCommentCountLL.setOnClickListener{
            val intent = Intent(activity, CommentsActivity::class.java)
            intent.putExtra(KEY_DATA, modelPost)
            activity.startActivity(intent)
        }

        bgLikeCountLL.setOnClickListener {
            val intent = Intent(activity, UserLikeActivity::class.java)
            intent.putExtra(KEY_DATA, modelPost.id)
            activity.startActivity(intent)
        }

    }

    private fun setDetail() {
        var lat = modelPost.latitude
        var long = modelPost.longitude

        repositoryUser.getByUid(modelPost.uid!!) { modelUser: ModelUser? ->
            modelPost.username = modelUser!!.username
            modelPost.profile = modelUser!!.profile

            usernameTV.text = modelPost.username
            Glide.with(activity).load(modelPost.profile).into(profileIV)
        }

        repositoryPost.getCommentsCount(modelPost.id!!){ count->
            countCommentTV.text = "$count"
        }

        repositoryPost.getCountLike(modelPost.id!!){ count ->
            countLikeTV.text = "$count"
        }
        createDateTV.text = formatCreateDate(modelPost.createDate.toString().toLong())
        titleTV.text = if (modelPost.title != null) modelPost.title else ""
        descriptionTV.text = if (modelPost.desciption != null) modelPost.desciption else ""

        setAdap()
    }

    private fun setAdap() {
        //set adap image post
        val adapter = AdapImagePost(activity, modelPost.images)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        imageRCV.adapter = adapter
        imageRCV.layoutManager = layoutManager
    }

    private fun formatCreateDate(createDate: Long): String {
        val currentDate = System.currentTimeMillis()
        val pastTime = currentDate - createDate
        //259200000 = 3 day
        //86,400,000 = 1 day
        //3,600,000 = 1 h
        //60,000 = 1 m
        //1,000 = 1 s
        Log.i("fweewfwe", "pastTime: $pastTime")
        if (pastTime > 259200000) {
            return Utils.formatDate("dd MMM yyyy HH:mm", Date(createDate))
        } else if (pastTime >= 86400000) {
            val day = (pastTime / 86400000).toInt()
            return "$day วัน ที่เเล้ว"
        } else if (pastTime >= 3600000) {
            val h = (pastTime / 3600000).toInt()
            return "$h ชม. ที่เเล้ว"
        } else if (pastTime >= 60000) {
            val m = (pastTime / 60000).toInt()
            return "$m น. ที่เเล้ว"
        } else {
            val s = (pastTime / 1000).toInt()
            return "$s วิ. ที่เเล้ว"
        }
    }
}