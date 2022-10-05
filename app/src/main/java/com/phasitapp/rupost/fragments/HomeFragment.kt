package com.phasitapp.rupost.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.chip.Chip
import com.phasitapp.rupost.*
import com.phasitapp.rupost.adapter.AdapPost
import com.phasitapp.rupost.dialog.BottomSheetMenuFilter
import com.phasitapp.rupost.helper.FilterPost
import com.phasitapp.rupost.model.ModelPost
import com.phasitapp.rupost.repository.RepositoryPost
import com.phasitapp.rupost.repository.RepositoryUser
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*


class HomeFragment : Fragment(R.layout.fragment_home) {

    private val postList = ArrayList<ModelPost>()

    private lateinit var repositoryUser: RepositoryUser
    private lateinit var repositoryPost: RepositoryPost

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("sadasdas", "HomeFragment onViewCreated: ")


        init()
        setData()
        setAdap()
        event()
    }

    override fun onDestroy() {
        super.onDestroy()

        //postRCV.adapter?
    }

    private val categoryDisplayList = ArrayList<ModelCategory>()
    private var currentCategory = "ทั้งหมด"
    private fun addChipCategoryView() {

        categoryCG?.removeAllViews()
        categoryDisplayList.clear()
        categoryDisplayList.add(ModelCategory("ทั้งหมด", ""))
        categoryDisplayList.add(ModelCategory(CATEGORY_QUESTION, "ic_question_mark"))
        categoryDisplayList.add(ModelCategory(CATEGORY_SHARE, "ic_share"))
        categoryDisplayList.add(ModelCategory(CATEGORY_EVENT, "ic_star"))

        for (i in categoryDisplayList.indices) {
            val chip = Chip(requireActivity())
            chip.setChipBackgroundColorResource(R.color.selector_choice_state)
            val colors =
                ContextCompat.getColorStateList(requireActivity(), R.color.selector_text_state)
            chip.setTextColor(colors)
            chip.text = categoryDisplayList[i].title
            chip.chipIconSize = 50f

            if (i != 0) {
                Glide.with(this).asBitmap().apply(RequestOptions.centerCropTransform())
                    .load(
                        resources.getIdentifier(
                            categoryDisplayList[i].icon,
                            "drawable",
                            requireActivity().packageName
                        )
                    )
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            val circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(resources, resource)
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
                if (currentCategory == categoryDisplayList[i].title) {

                } else {
                    currentCategory = categoryDisplayList[i].title!!
                    updateFilterPost()
                }
            }

            if (i == 0) {
                chip.isChecked = true
            }
        }
    }

    private var currentFilterDay = FILTERDAY_7DAY_LAST
    private var currentFilterActivity = FILTERACTIVITY_LAST
    private fun showFilterDayBottomSheetDialog() {

        val menuList = ArrayList<BottomSheetMenuFilter.ModelMenuBottomSheet>()
        menuList.add(BottomSheetMenuFilter.ModelMenuBottomSheet(FILTERDAY_TODAY))
        menuList.add(BottomSheetMenuFilter.ModelMenuBottomSheet(FILTERDAY_YESTERDAY))
        menuList.add(BottomSheetMenuFilter.ModelMenuBottomSheet(FILTERDAY_3DAY_LAST))
        menuList.add(BottomSheetMenuFilter.ModelMenuBottomSheet(FILTERDAY_7DAY_LAST))

        BottomSheetMenuFilter(
            requireActivity(),
            menuList,
            "เลือกช่วงเวลา",
            object : BottomSheetMenuFilter.SelectListener {
                override fun onMyClick(
                    m: BottomSheetMenuFilter.ModelMenuBottomSheet,
                    position: Int
                ) {
                    currentFilterDay = m.menuname
                    when (position) {
                        0 -> {
                            filterDayTV.text = m.menuname
                            updateFilterPost()
                        }
                        1 -> {
                            filterDayTV.text = m.menuname
                            updateFilterPost()
                        }
                        2 -> {
                            filterDayTV.text = m.menuname
                            updateFilterPost()
                        }
                        3 -> {
                            filterDayTV.text = m.menuname
                            updateFilterPost()
                        }
                    }
                }
            })
    }

    private fun showFilterActivityBottomSheetDialog() {

        val menuList = ArrayList<BottomSheetMenuFilter.ModelMenuBottomSheet>()
        menuList.add(BottomSheetMenuFilter.ModelMenuBottomSheet(FILTERACTIVITY_UPDATELAST))
        menuList.add(BottomSheetMenuFilter.ModelMenuBottomSheet(FILTERACTIVITY_LAST))
        menuList.add(BottomSheetMenuFilter.ModelMenuBottomSheet(FILTERACTIVITY_POPULAR))

        BottomSheetMenuFilter(
            requireActivity(),
            menuList,
            "เรียงลำดับ",
            object : BottomSheetMenuFilter.SelectListener {
                override fun onMyClick(
                    m: BottomSheetMenuFilter.ModelMenuBottomSheet,
                    position: Int
                ) {
                    currentFilterActivity = m.menuname
                    when (position) {
                        0 -> {
                            filterActivityTV.text = m.menuname
                            updateFilterPost()
                        }
                        1 -> {
                            filterActivityTV.text = m.menuname
                            updateFilterPost()
                        }
                        2 -> {
                            filterActivityTV.text = m.menuname
                            updateFilterPost()
                        }

                    }
                }
            })
    }


    private fun updateFilterPost() {
        postFilter.clear()

        FilterPost().filter(
            postList,
            currentCategory = currentCategory,
            currentFilterDay = currentFilterDay
        ) { postsFilter ->
            postFilter.addAll(postsFilter)
            //postFilter.add(ModelPost())
            postRCV.adapter!!.notifyDataSetChanged()
        }
    }

    private fun init() {

        repositoryUser = RepositoryUser(requireActivity())
        repositoryPost = RepositoryPost(requireActivity())
        bgNotPostLL.visibility = View.GONE
        addChipCategoryView()

        swipeRefreshLayout.setOnRefreshListener {
            setData()
        }

    }


    private fun event() {

        mapLL.setOnClickListener {
            val intent = Intent(requireActivity(), MapActivity::class.java)
            startActivity(intent)
        }

        filterDayRL.setOnClickListener {
            showFilterDayBottomSheetDialog()
        }

        filterIV.setOnClickListener {
            when(bgFilterLL.visibility){
                View.VISIBLE->{
                    bgFilterLL.visibility = View.GONE
                }
                View.GONE->{
                    bgFilterLL.visibility = View.VISIBLE
                }
            }
        }

        filterActivityRL.setOnClickListener {
            showFilterActivityBottomSheetDialog()
        }
    }


    private fun setData() {
        postList.clear()

        val calStart = Calendar.getInstance()
        calStart.add(Calendar.DATE, -7)
        val startAt = calStart.timeInMillis

        Log.i("fewfweg", "start: " + startAt)

        repositoryPost.read(startAt, 5) { result, post ->
            swipeRefreshLayout.isRefreshing = false
            loadPostPB.visibility = View.GONE
            when (result) {
                RepositoryPost.RESULT_SUCCESS -> {

                    Log.i("fewfweg", "post: " + post.size)
                    post.forEachIndexed { index, model ->

                        repositoryUser.getByUid(model.uid!!){ modelUser->
                            model.profile = modelUser!!.profile
                            model.username = modelUser!!.username
                            postList.add(model)

                            Log.i("fewfweg", "postList: " + postList.size)
                            //postRCV.adapter!!.notifyItemInserted(postList.size -1)
                            if(index == post.lastIndex){
                                updateFilterPost()
                            }
                        }
                    }

                    if (post.size != 0) {
                        bgNotPostLL.visibility = View.GONE
                    } else {
                        bgNotPostLL.visibility = View.VISIBLE
                        Glide.with(requireActivity()).load(R.drawable.gif_not_data).into(notFoundIV)
                    }
                }
                RepositoryPost.RESULT_FAIL -> {
                    //bgNotPostLL.visibility = View.VISIBLE
                    //Glide.with(requireActivity()).load(R.drawable.gif_not_data).into(notFoundIV)
                }
            }

        }
    }

    private val postFilter = ArrayList<ModelPost>()

    private var isLoading = false
    private fun setAdap() {
        val adapter = AdapPost(requireActivity(), postFilter)
        val layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        postRCV.adapter = adapter
        postRCV.layoutManager = layoutManager

        postRCV.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            private var top = true

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

//                if (top) {
//                    bgFilterLL.visibility = View.GONE
//                } else {
//                    bgFilterLL.visibility = View.VISIBLE
//                }

//                if (!isLoading) {
//                    if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == postList.size - 1) {
//                        //bottom of list!
//                        loadMore();
//                        isLoading = true;
//                    }
//                }

//                val visibleItemCount: Int = layoutManager.getChildCount()
//                val totalItemCount: Int = layoutManager.getItemCount()
//                val pastVisibleItems: Int = layoutManager.findFirstVisibleItemPosition()
////                Log.d("-----","visibleItemCount: " + visibleItemCount);
////                Log.d("-----","totalItemCount: " + totalItemCount);
////                Log.d("-----","pastVisibleItems: " + pastVisibleItems);
//
//                if ((limitPost.toInt() - pastVisibleItems) >= 2) {
//                    //End of list
//                    Log.d("-----","setData");
//                    limitPost+=2
//                    setData()
//                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.i("wgfafwaga", "state: ${recyclerView.scrollState} dx: " + dx + ", dy: " + dy)
                top = dy > 0

            }
        })
    }

    private fun loadMore() {
        postList.add(ModelPost())
        postRCV.adapter!!.notifyItemInserted(postList.size - 1)

        val scrollPosition: Int = postList.size

        var currentSize = scrollPosition - 1
        val nextLimit = currentSize + 10

        val calStart = Calendar.getInstance()
        calStart.add(Calendar.DATE, -7)
        val startAt = calStart.timeInMillis

        repositoryPost.read(startAt, nextLimit.toLong()) { result, post ->
            postList.removeAt(postList.size - 1)
            postRCV.adapter!!.notifyItemRemoved(scrollPosition)

            when (result) {
                RepositoryPost.RESULT_SUCCESS -> {

                    Log.i("fewfweg", "post: " + post.size)
                    if(post.size == postList.size){
                        return@read
                    }

                    post.forEach { model ->

                        val hasItem = postList.filter { it.id == model.id }.isNotEmpty()

                        if(!hasItem){

                            repositoryUser.getByUid(model.uid!!){ modelUser->
                                model.profile = modelUser!!.profile
                                model.username = modelUser!!.username
                                postList.add(model)

                                Log.i("fewfweg", "postList: " + postList.size)
                                //postRCV.adapter!!.notifyItemInserted(postList.size -1)
                                updateFilterPost()
                            }
                        }

                    }
                    if (post.size != 0) {
                        bgNotPostLL.visibility = View.GONE
                    } else {
                        bgNotPostLL.visibility = View.VISIBLE
                        Glide.with(requireActivity()).load(R.drawable.gif_not_data).into(notFoundIV)
                    }
                }
                RepositoryPost.RESULT_FAIL -> {
                    //bgNotPostLL.visibility = View.VISIBLE
                    //Glide.with(requireActivity()).load(R.drawable.gif_not_data).into(notFoundIV)
                }
            }
        }

        isLoading = false


    }


    inner class ModelCategory(
        var title: String? = null,
        var icon: String? = null
    )
}