package com.phasitapp.rupost

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.phasitapp.rupost.adapter.AdapPost
import com.phasitapp.rupost.helper.Prefs
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

    private lateinit var prefs: Prefs
    private lateinit var repositoryUser: RepositoryUser
    private lateinit var repositoryPost: RepositoryPost

    override fun onCreate(savedInstanceState:
                          Bundle?) {
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

        followTV.setOnClickListener {

        }

        editTV.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            resultForEditProfile.launch(intent)
        }
    }

    var resultForEditProfile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            val profile = data!!.getStringExtra(KEY_PROFILE)
            val username = data!!.getStringExtra(KEY_USERNAME)
            val description = data!!.getStringExtra(KEY_DESCIPTION)

            usernameTV.text = username
            Glide.with(this).load(profile).centerCrop().into(imageUrlUserIV)
            descriptionTV.text = if(description == "") "ไม่คำอธิบาย" else description
        }
    }

    private fun init(){
        bgDetailLL.visibility = View.GONE
        prefs = Prefs(this)
        repositoryUser = RepositoryUser(this)
        repositoryPost = RepositoryPost(this)
    }
    private val postList = ArrayList<ModelPost>()
    private fun setDataPost() {
        postList.clear()
        val uidProfile = intent.getStringExtra(KEY_UID)

        val repositoryPost = RepositoryPost(this)
        val repositoryUser = RepositoryUser(this)

        val uid = prefs.strUid!!
        if(uid != ""){
            if(uid == uidProfile){
                bgFollowCV.visibility = View.GONE
                bgEditCV.visibility = View.VISIBLE
            }else{
                bgFollowCV.visibility = View.VISIBLE
                bgEditCV.visibility = View.GONE
            }
        }

        repositoryUser.getByUid(uidProfile!!){ modelUser->

            Glide.with(this).load(modelUser!!.profile).into(imageUrlUserIV)
            usernameTV.text = "${modelUser.username}"
            descriptionTV.text = "${modelUser.description}"

            repositoryPost.readByUid(uidProfile!!) { result, post ->
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

        repositoryUser.countFollowingByUid(uidProfile){ count->
            countFollowingTV.text = "$count"
        }
        repositoryUser.countFollowerByUid(uidProfile){ count->
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