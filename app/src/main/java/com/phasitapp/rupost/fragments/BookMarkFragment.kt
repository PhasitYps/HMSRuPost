package com.phasitapp.rupost.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.huawei.hms.maps.*
import com.phasitapp.rupost.R
import com.phasitapp.rupost.adapter.AdapPost
import com.phasitapp.rupost.helper.Prefs
import com.phasitapp.rupost.model.ModelPost
import com.phasitapp.rupost.repository.RepositoryPost
import com.phasitapp.rupost.repository.RepositoryPost.Companion.RESULT_SUCCESS
import com.phasitapp.rupost.repository.RepositoryUser
import kotlinx.android.synthetic.main.fragment_bookmark.*


class BookMarkFragment : Fragment(R.layout.fragment_bookmark)  {

    companion object {
        private const val TAG = "BookMarkFragment"
    }

    private lateinit var prefs: Prefs
    private lateinit var repositoryUser: RepositoryUser
    private lateinit var repositoryPost: RepositoryPost
    private val postList = ArrayList<ModelPost>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setData()
        setAdap()
    }

    private fun init(){

        prefs = Prefs(requireActivity())
        repositoryUser = RepositoryUser(requireActivity())
        repositoryPost = RepositoryPost(requireActivity())

        swipeRefreshLayout.setOnRefreshListener {
            setData()
        }
    }

    private fun setData(){

        val uid = prefs.strUid
        if(uid != null){
            repositoryUser.getBookmark(uid){ postIdList->

                postIdList.forEachIndexed { index, postId->
                    Log.i("gewgewgw", "postId: " + postId)

                    repositoryPost.readById(postId){ result, post ->

                        if (result == RESULT_SUCCESS && post != null){
                            postList.add(post)
                        }
                        if(index == postIdList.lastIndex){
                            setAdap()

                            postList.forEach { model->
                                repositoryUser.getByUid(model.uid!!){ modelUser->
                                    model.profile = modelUser!!.profile
                                    model.username = modelUser!!.username

                                    dataRCV.adapter!!.notifyDataSetChanged()
                                }
                            }


                        }
                    }
                }

                loadPostPB.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun setAdap() {
        val adapter = AdapPost(requireActivity(), postList)
        val layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        dataRCV.adapter = adapter
        dataRCV.layoutManager = layoutManager
    }


}