package com.phasitapp.rupost

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.phasitapp.rupost.adapter.AdapPost
import com.phasitapp.rupost.model.ModelPost
import com.phasitapp.rupost.repository.RepositoryPost
import com.phasitapp.rupost.repository.RepositoryUser
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.bgDetailLL
import kotlinx.android.synthetic.main.activity_profile.countFollowerTV
import kotlinx.android.synthetic.main.activity_profile.countFollowingTV
import kotlinx.android.synthetic.main.activity_profile.countPostTV
import kotlinx.android.synthetic.main.activity_profile.dataRCV
import kotlinx.android.synthetic.main.activity_profile.descriptionTV
import kotlinx.android.synthetic.main.activity_profile.detailMoreTV
import kotlinx.android.synthetic.main.activity_profile.imageUrlUserIV
import kotlinx.android.synthetic.main.activity_profile.loadPostPB
import kotlinx.android.synthetic.main.activity_profile.usernameTV
import kotlinx.android.synthetic.main.fragment_user.*

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        init()
        setDataPost()
        setAdap()
        event()
    }

    private fun event(){

        detailMoreTV.setOnClickListener {
            when(bgDetailLL.visibility){
                View.VISIBLE->{
                    bgDetailLL.visibility = View.GONE
                }
                View.GONE->{
                    bgDetailLL.visibility = View.VISIBLE
                }
            }
        }

        backIV.setOnClickListener {
            finish()
        }
    }
    private fun init(){
        bgDetailLL.visibility = View.GONE
    }
    private val postList = ArrayList<ModelPost>()
    private fun setDataPost() {
        postList.clear()
        val uid = intent.getStringExtra(KEY_UID)

        val repositoryPost = RepositoryPost(this)
        val repositoryUser = RepositoryUser(this)

        repositoryUser.getByUid(uid!!){ modelUser->

            Glide.with(this).load(modelUser!!.profile).into(imageUrlUserIV)
            usernameTV.text = "${modelUser.username}"
            descriptionTV.text = "${modelUser.description}"

            repositoryPost.readByUid(uid!!) { result, post ->
                loadPostPB.visibility = View.GONE

                when (result) {
                    RepositoryPost.RESULT_SUCCESS -> {
                        countPostTV.text = "${post.size}"
                        postList.addAll(post)
                        postList.forEachIndexed { index, modelPost ->
                            modelPost.profile = modelUser!!.profile
                            modelPost.username = modelUser!!.username
                        }
                        dataRCV.adapter!!.notifyDataSetChanged()


                    }
                    RepositoryPost.RESULT_FAIL -> {
                    }
                }
            }
        }

        repositoryUser.countFollowingByUid(uid){ count->
            countFollowingTV.text = "$count"
        }
        repositoryUser.countFollowerByUid(uid){ count->
            countFollowerTV.text = "$count"
        }
    }

    private fun setAdap() {
        val adapter = AdapPost(this, postList)
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        dataRCV.adapter = adapter
        dataRCV.layoutManager = layoutManager
    }
}