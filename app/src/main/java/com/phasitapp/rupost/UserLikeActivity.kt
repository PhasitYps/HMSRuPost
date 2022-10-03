package com.phasitapp.rupost

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.phasitapp.rupost.adapter.AdapUserLike
import com.phasitapp.rupost.repository.RepositoryPost
import com.phasitapp.rupost.repository.RepositoryUser
import kotlinx.android.synthetic.main.activity_user_like.*

class UserLikeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_like)

        setData()
        setAdap()
        event()
    }

    private fun event(){

        backIV.setOnClickListener {
            finish()
        }
    }

    private val dataList = ArrayList<AdapUserLike.ModelUserLike>()
    private fun setData(){

        val postId = intent.getStringExtra(KEY_DATA)

        val repositoryPost = RepositoryPost(this)
        val repositoryUser = RepositoryUser(this)
        repositoryPost.getStaticLike(postId!!){ userIdList->
            userIdList.forEach {
                repositoryUser.getByUid(it){ modelUser->
                    val model = AdapUserLike.ModelUserLike()
                    model.uid = modelUser!!.uid
                    model.username = modelUser!!.username
                    model.profileUrl = modelUser!!.profile

                    dataList.add(model)
                    dataRCV.adapter!!.notifyDataSetChanged()
                }
            }

            coundLikeTV.text = "${userIdList.size}"
        }
    }

    private fun setAdap(){
        val adapter = AdapUserLike(this, dataList)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        dataRCV.adapter = adapter
        dataRCV.layoutManager = layoutManager
    }

}