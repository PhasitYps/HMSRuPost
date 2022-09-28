package com.phasitapp.rupost.darf

import android.app.ActionBar
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phasitapp.rupost.R
import com.phasitapp.rupost.model.ModelComment
import com.phasitapp.rupost.repository.RepositoryPost
import kotlinx.android.synthetic.main.bottom_sheet_comments.*

class CommentsBottomSheetDialog(private val activity: Activity,private val postId: String): BottomSheetDialog(activity, R.style.SheetDialog) {

    private val commentList = ArrayList<ModelComment>()
    init {
        val view: View = activity.layoutInflater.inflate(R.layout.bottom_sheet_comments, null)
        setContentView(view)
        window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT)
        behavior.expandedOffset = 100
        behavior.peekHeight = 250


        setData()
        setAdapComments()
    }

    private fun setData(){
        val repositoryPost = RepositoryPost(activity)
        repositoryPost.getComments(postId){

        }
    }

    private fun setAdapComments() {
        val adapter = DataAdapter(commentList)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        dataRCV.adapter = adapter
        dataRCV.layoutManager = layoutManager
    }

    inner class DataAdapter(private val dataList: ArrayList<ModelComment>) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_listview_bottomsheet_menu_filter, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var itemCV: CardView = itemView.findViewById(R.id.itemCV)
            var menunameTV: TextView = itemView.findViewById(R.id.menunameTV)
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

    }


    data class ModelMenuBottomSheet(
        var menuname:String,
    )
}