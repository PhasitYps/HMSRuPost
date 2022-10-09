package com.phasitapp.rupost.dialog

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phasitapp.rupost.*
import com.phasitapp.rupost.adapter.AdapImagePost
import com.phasitapp.rupost.helper.OpenWeatherApi
import com.phasitapp.rupost.helper.Prefs
import com.phasitapp.rupost.model.ModelPost
import com.phasitapp.rupost.model.ModelUser
import com.phasitapp.rupost.repository.RepositoryPost
import com.phasitapp.rupost.repository.RepositoryUser
import kotlinx.android.synthetic.main.bottom_sheet_detail_post.*
import okhttp3.internal.wait
import java.util.*

class DetailPostBottomDialog(private val activity: Activity, private val modelPost: ModelPost) :
    BottomSheetDialog(activity, R.style.SheetDialog) {

    private val repositoryUser = RepositoryUser(activity)
    private val repositoryPost = RepositoryPost(activity)
    private var prefs: Prefs? = Prefs(activity)
    private val openWeatherApi = OpenWeatherApi(activity)

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
        event()

    }
    private fun event(){
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

        likeIV.setOnClickListener {
            if (prefs!!.strUid != "") {
                if (likeIV.tag != null) {
                    val currnetLike = likeIV.tag as Boolean
                    when (currnetLike) {
                        true -> {
                            repositoryPost.like(modelPost.id!!, false)
                            likeIV.tag = false
                            Glide.with(activity).load(R.drawable.ic_heart).into(likeIV)
                            minusLike()
                        }
                        false -> {
                            repositoryPost.like(modelPost.id!!, true)
                            likeIV.tag = true
                            Glide.with(activity).load(R.drawable.ic_heart_red).into(likeIV)
                            plusLike()
                        }
                    }
                }
            }
        }

        bookmarkIV.setOnClickListener {
            val uid = prefs!!.strUid
            if(uid != null){
                val isBookmark = bookmarkIV.tag as Boolean
                if(isBookmark){
                    bookmarkIV.tag = false
                    repositoryUser.bookmark(uid, modelPost.id!!, false)
                    Glide.with(activity).load(R.drawable.ic_bookmark).into(bookmarkIV)
                }else{
                    bookmarkIV.tag = true
                    repositoryUser.bookmark(uid, modelPost.id!!, true)
                    Glide.with(activity).load(R.drawable.ic_bookmark_filled).into(bookmarkIV)
                    Toast.makeText(activity, "บันทึกโพสต์เรียบร้อย", Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(activity, "กรุณาเข้าสู่ระบบ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun plusLike() {
        modelPost.countLike++

        countLikeTV.text = "${modelPost.countLike}"
        if (modelPost.countLike != 0) {
            bgLikeCountLL.visibility = View.VISIBLE
        } else {
            bgLikeCountLL.visibility = View.GONE
        }
    }

    private fun minusLike() {
        modelPost.countLike--

        countLikeTV.text = "${modelPost.countLike}"
        if (modelPost.countLike != 0) {
            bgLikeCountLL.visibility = View.VISIBLE
        } else {
            bgLikeCountLL.visibility = View.GONE
        }
    }

    private fun setDetail() {
        var lat = modelPost.latitude
        var long = modelPost.longitude
        likeIV.tag = false

        openWeatherApi.setLang("th")
        openWeatherApi.getCurrentByLatLng(lat!!.toDouble(), long!!.toDouble()){
            tempTV.text = it.temp
            weatherDescriptionTV.text = it.description
            windTV.text = "${it.wind} กม./ชม."
            Glide.with(activity).load(it.iconBitmap).into(weatherIconIV)
        }

        repositoryUser.getByUid(modelPost.uid!!) { modelUser: ModelUser? ->
            modelPost.username = modelUser!!.username
            modelPost.profile = modelUser!!.profile

            usernameTV.text = modelPost.username
            Glide.with(activity).load(modelPost.profile).into(profileIV)
        }

        repositoryPost.getCommentsCount(modelPost.id!!){ count->
            countCommentTV.text = "$count"
        }

        repositoryPost.getStaticLike(modelPost.id!!){ userlikeList ->
            countLikeTV.text = "${userlikeList.size}"
            modelPost.countLike = userlikeList.size

            if(prefs!!.strUid != null){
                val filter = userlikeList.filter { it == prefs!!.strUid }
                if(filter.isNotEmpty()){
                    Glide.with(activity).load(R.drawable.ic_heart_red).into(likeIV)
                    likeIV.tag = true
                }else{
                    Glide.with(activity).load(R.drawable.ic_heart).into(likeIV)
                    likeIV.tag = false
                }
            }
        }

        if(prefs!!.strUid != null){
            val uid = prefs!!.strUid!!
            repositoryUser.getBookmarkPost(uid, modelPost.id!!){ isBookmark->
                bookmarkIV.tag = isBookmark
                if(isBookmark){
                    Glide.with(activity).load(R.drawable.ic_bookmark_filled).into(bookmarkIV)
                }else{
                    Glide.with(activity).load(R.drawable.ic_bookmark).into(bookmarkIV)
                }
            }
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