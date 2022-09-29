package com.phasitapp.rupost

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.phasitapp.rupost.adapter.AdapComments
import com.phasitapp.rupost.helper.Prefs
import com.phasitapp.rupost.model.ModelComment
import com.phasitapp.rupost.repository.RepositoryComment
import kotlinx.android.synthetic.main.activity_respond_comment.*

class RespondCommentActivity : AppCompatActivity() {

    private lateinit var repositoryComment: RepositoryComment
    private lateinit var prefs: Prefs
    private var countLike = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_respond_comment)

        init()
        setDetail()
        event()
    }

    private fun init(){
        val model = intent.getSerializableExtra(KEY_DATA) as ModelComment

        prefs = Prefs(this)
        repositoryComment = RepositoryComment(this, model.postId!!)
    }
    private fun setDetail(){
        val model = intent.getSerializableExtra(KEY_DATA) as ModelComment


        Glide.with(this).load(model.profile).into(profileCIV)
        usernameTV.text = model.username
        messageTV.text = model.message
        createDateTV.text =
            Utils.formatCreateDate(model.createDate.toString().toLong())

        //holder.countLikeTV.text = "${commentList[position].countLike}"
        setLike(model.countLike!!)

        val userlike = model.currentUserLike
        likeIV.tag = userlike
        if(userlike){
            Glide.with(this).load(R.drawable.ic_heart_red).into(likeIV)
        }else{
            Glide.with(this).load(R.drawable.ic_heart).into(likeIV)
        }
    }

    private fun event(){
        val model = intent.getSerializableExtra(KEY_DATA) as ModelComment

        backIV.setOnClickListener { finish() }

        likeIV.setOnClickListener {
            if (prefs.strUid != "") {
                if (likeIV.tag != null) {
                    val currnetLike = likeIV.tag as Boolean
                    when (currnetLike) {
                        true -> {
                            repositoryComment.like(model.id!!, false)
                            likeIV.tag = false
                            Glide.with(this).load(R.drawable.ic_heart).into(likeIV)
                            minusLike()
                        }
                        false -> {
                            repositoryComment.like(model.id!!, true)
                            likeIV.tag = true
                            Glide.with(this).load(R.drawable.ic_heart_red).into(likeIV)
                            plusLike()
                        }
                    }
                }
            }

        }
        commentIV.setOnClickListener {

        }

        messageTV.setOnClickListener(object : AdapComments.DoubleClickListener() {
            override fun onDoubleClick(v: View?) {
                Log.i("dawgewg", "This is DoubleClickListener: $")
                if (prefs.strUid != "") {
                    if (likeIV.tag != null) {
                        val currnetLike = likeIV.tag as Boolean
                        if (!currnetLike) {
                            repositoryComment.like(model.id!!, true)
                            likeIV.tag = true
                            Glide.with(this@RespondCommentActivity).load(R.drawable.ic_heart_red).into(likeIV)
                            plusLike()
                        }
                    }
                }
            }

        })
    }

    private fun plusLike() {
        countLike++
        countLikeTV.text = "$countLike"
        if (countLike != 0) {
            bgLikeCountLL.visibility = View.VISIBLE
        } else {
            bgLikeCountLL.visibility = View.GONE
        }
    }

    private fun minusLike() {
        countLike--
        countLikeTV.text = "$countLike"
        if (countLike != 0) {
            bgLikeCountLL.visibility = View.VISIBLE
        } else {
            bgLikeCountLL.visibility = View.GONE
        }
    }

    private fun setLike(count: Int) {
        countLike= count
        countLikeTV.text = "$countLike"
        if (countLike != 0) {
            bgLikeCountLL.visibility = View.VISIBLE
        } else {
            bgLikeCountLL.visibility = View.GONE
        }
    }
}