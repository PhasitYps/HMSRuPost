package com.phasitapp.rupost

import android.app.ActionBar
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.huawei.hms.maps.HuaweiMap
import com.phasitapp.rupost.adapter.AdapComments
import com.phasitapp.rupost.helper.Prefs
import com.phasitapp.rupost.model.ModelComment
import com.phasitapp.rupost.model.ModelPost
import com.phasitapp.rupost.model.ModelUser
import com.phasitapp.rupost.repository.RepositoryComment
import com.phasitapp.rupost.repository.RepositoryPost
import com.phasitapp.rupost.repository.RepositoryUser
import kotlinx.android.synthetic.main.activity_comments.*


class CommentsActivity : AppCompatActivity() {

    private val TAG = "CommentsActivityTAG"
    private lateinit var repositoryPost: RepositoryPost
    private lateinit var repositoryUser: RepositoryUser
    private lateinit var repositoryComment: RepositoryComment
    private lateinit var huaweiMap: HuaweiMap
    private lateinit var prefs: Prefs
    private lateinit var model: ModelPost
    private val commentList = ArrayList<ModelComment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        init()
        initDialogLoad()

        setDetail()
        setData()
        setAdap()
        event()
    }

    private fun init() {

        model = intent.getSerializableExtra(KEY_DATA) as ModelPost
        prefs = Prefs(this)
        repositoryPost = RepositoryPost(this)
        repositoryComment = RepositoryComment(this, model.id!!)
        repositoryUser = RepositoryUser(this)

        sendIV.isEnabled = false

        swipeRefreshLayout.setOnRefreshListener {
            setData()
        }
    }

    private lateinit var dialog_load: Dialog
    private fun initDialogLoad() {
        dialog_load = Dialog(this)
        dialog_load!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog_load!!.setContentView(R.layout.dialog_loading)
        dialog_load!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog_load!!.window!!.setLayout(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )

        dialog_load!!.create()
    }

    private fun event() {

        backIV.setOnClickListener {
            finish()
        }

        likeIV.setOnClickListener {
            Log.i("agehaehes", "likeIV click")
            if (prefs.strUid != "") {
                if (likeIV.tag != null) {
                    val currnetLike = likeIV.tag as Boolean
                    when (currnetLike) {
                        true -> {
                            repositoryPost.like(model.id!!, false)
                            likeIV.tag = false
                            Glide.with(this).load(R.drawable.ic_heart).into(likeIV)
                            minusLike()
                        }
                        false -> {
                            repositoryPost.like(model.id!!, true)
                            likeIV.tag = true
                            Glide.with(this).load(R.drawable.ic_heart_red).into(likeIV)
                            plusLike()
                        }
                    }
                }
            }
        }

        messageEDT.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isNotBlank()) {
                    sendIV.isClickable = true
                    sendIV.isEnabled = true
                    Glide.with(this@CommentsActivity).load(R.drawable.ic_send_filled).into(sendIV)
                } else {
                    sendIV.isClickable = false
                    sendIV.isEnabled = false
                    Glide.with(this@CommentsActivity).load(R.drawable.ic_send).into(sendIV)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })

        sendIV.setOnClickListener {

            if (prefs.strUid == "") {
                Toast.makeText(this, "โปรดลงชื่อเข้าใช้", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            dialog_load.show()
            val message = messageEDT.text.toString().trim()
            val modelComment = ModelComment()
            modelComment.profile = prefs.strPhotoUri
            modelComment.username = prefs.strUsername
            modelComment.uid = prefs.strUid
            modelComment.message = message
            modelComment.createDate = System.currentTimeMillis().toString()
            modelComment.tag = "post"

            repositoryPost.comments(model.id!!, modelComment) { comment ->
                if (comment != null) {
                    messageEDT.setText("")
                    commentList.add(comment)

                    dataRCV.adapter!!.notifyDataSetChanged()
                    dataRCV.layoutManager!!.scrollToPosition(commentList.size - 1)
                }
                dialog_load.dismiss()
            }
        }

    }

    private fun setData() {
        commentList.clear()

        Log.i("fewfwe", "Thread")
        repositoryPost.getComments(model.id!!) { comments ->
            Log.i("fewfwe", "getComments")
            repositoryComment.getUserCurrentLike { userLikeComments ->
                Log.i("fewfwe", "getUserCurrentLike")
                swipeRefreshLayout.isRefreshing = false

                comments.forEach { comment ->

                    repositoryComment.getCountLike(comment.id!!) { count ->
                        comment.countLike = count
                        Log.i("fewfwe", "getCountLike")

                        val filter = userLikeComments.filter { it == comment.id }
                        comment.currentUserLike = filter.isNotEmpty()

                        repositoryUser.getByUid(comment.uid!!){ modelUser: ModelUser? ->
                            Log.i("fewfwe", "getByUid")

                            comment.username = modelUser!!.username
                            comment.profile = modelUser!!.profile

                            commentList.add(comment)
                            commentList.sortBy { it.createDate }
                            dataRCV.adapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

    }

    private fun setAdap() {
        val adapter = AdapComments(this, commentList, repositoryComment)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        dataRCV.adapter = adapter
        dataRCV.layoutManager = layoutManager
    }

    private fun setDetail() {
        repositoryPost.getStaticLike(model.id!!) { userLikes ->
            Log.i(TAG, "get staticLike.")
            try {
                setLike(userLikes.size)
                if (userLikes.size != 0) {
                    userLikes.filter { it == prefs.strUid }.apply {
                        if (userLikes.isNotEmpty()) {
                            likeIV.tag = true
                            Glide.with(this@CommentsActivity).load(R.drawable.ic_heart_red)
                                .into(likeIV)
                        } else {
                            likeIV.tag = false
                            Glide.with(this@CommentsActivity).load(R.drawable.ic_heart).into(likeIV)
                        }
                    }

                } else {
                    likeIV.tag = false
                }
            } catch (e: IllegalArgumentException) {
                Log.i(TAG, "error: ${e.message}")
            }
        }
    }

    private var countLike = 0
    private fun plusLike() {
        countLike++
        coundLikeTV.text = "$countLike"
    }

    private fun minusLike() {
        countLike--
        coundLikeTV.text = "$countLike"
    }

    private fun setLike(count: Int) {
        countLike = count
        coundLikeTV.text = "$countLike"
    }

}