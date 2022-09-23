package com.phasitapp.rupost.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.phasitapp.rupost.R
import com.phasitapp.rupost.model.ModelPost
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(R.layout.fragment_home) {

    private val postList = ArrayList<ModelPost>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setData()
        setAdap()
    }

    private fun setData(){
        postList.add(ModelPost())
        postList.add(ModelPost())
        postList.add(ModelPost())
        postList.add(ModelPost())
        postList.add(ModelPost())
        postList.add(ModelPost())
        postList.add(ModelPost())
        postList.add(ModelPost())
    }

    private fun setAdap(){
        val adapter = AdapPost(requireActivity() , postList)
        val layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        postRCV.adapter = adapter
        postRCV.layoutManager = layoutManager
    }

    inner class AdapPost(private var activity: Activity, private val dataList: ArrayList<ModelPost>): RecyclerView.Adapter<AdapPost.ViewHolder>(){


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_post, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            event(holder)
            setAnimateIcon(holder)
        }

        private fun event(holder: ViewHolder){

            holder.likeIV.setOnClickListener {

            }
            holder.commentIV.setOnClickListener {

            }
            holder.shareIV.setOnClickListener {

            }
            holder.bookmarkIV.setOnClickListener {

            }
        }

        private fun setAnimateIcon(holder: ViewHolder){
            val defaultIcon = holder.likeIV.layoutParams.width
            val smallIcon = (defaultIcon * 0.85).toInt()

            Log.i("dasdas", "widthDefault: " + defaultIcon)

            holder.likeIV.setOnTouchListener { view, motionEvent ->
                when(motionEvent.action){
                    MotionEvent.ACTION_DOWN->{
                        holder.likeIV.layoutParams.width = smallIcon
                        holder.likeIV.layoutParams.height = smallIcon
                        holder.likeIV.requestLayout()
                    }
                    MotionEvent.ACTION_UP->{
                        holder.likeIV.layoutParams.width = defaultIcon
                        holder.likeIV.layoutParams.height = defaultIcon
                        holder.likeIV.requestLayout()
                    }
                }

                false
            }
            holder.commentIV.setOnTouchListener { view, motionEvent ->
                when(motionEvent.action){
                    MotionEvent.ACTION_DOWN->{
                        holder.commentIV.layoutParams.width = smallIcon
                        holder.commentIV.layoutParams.height = smallIcon
                        holder.commentIV.requestLayout()
                    }
                    MotionEvent.ACTION_UP->{
                        holder.commentIV.layoutParams.width = defaultIcon
                        holder.commentIV.layoutParams.height = defaultIcon
                        holder.commentIV.requestLayout()
                    }
                }

                false
            }
            holder.shareIV.setOnTouchListener { view, motionEvent ->
                when(motionEvent.action){
                    MotionEvent.ACTION_DOWN->{
                        holder.shareIV.layoutParams.width = smallIcon
                        holder.shareIV.layoutParams.height = smallIcon
                        holder.shareIV.requestLayout()
                    }
                    MotionEvent.ACTION_UP->{
                        holder.shareIV.layoutParams.width = defaultIcon
                        holder.shareIV.layoutParams.height = defaultIcon
                        holder.shareIV.requestLayout()
                    }
                }

                false
            }
            holder.bookmarkIV.setOnTouchListener { view, motionEvent ->
                when(motionEvent.action){
                    MotionEvent.ACTION_DOWN->{
                        holder.bookmarkIV.layoutParams.width = smallIcon
                        holder.bookmarkIV.layoutParams.height = smallIcon
                        holder.bookmarkIV.requestLayout()
                    }
                    MotionEvent.ACTION_UP->{
                        holder.bookmarkIV.layoutParams.width = defaultIcon
                        holder.bookmarkIV.layoutParams.height = defaultIcon
                        holder.bookmarkIV.requestLayout()
                    }
                }

                false
            }
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val likeIV = itemView.findViewById<ImageView>(R.id.likeIV)
            val commentIV = itemView.findViewById<ImageView>(R.id.commentIV)
            val shareIV = itemView.findViewById<ImageView>(R.id.shareIV)
            val bookmarkIV = itemView.findViewById<ImageView>(R.id.bookmarkIV)

        }
    }
}