package com.phasitapp.rupost.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.phasitapp.rupost.KEY_DATA
import com.phasitapp.rupost.R
import com.phasitapp.rupost.RespondCommentActivity
import com.phasitapp.rupost.Utils.formatCreateDate
import com.phasitapp.rupost.helper.Prefs
import com.phasitapp.rupost.model.ModelComment
import com.phasitapp.rupost.repository.RepositoryComment
import de.hdodenhof.circleimageview.CircleImageView


class AdapUserLike(private var activity: Activity, private val dataList: ArrayList<ModelUserLike>) :
    RecyclerView.Adapter<AdapUserLike.ViewHolder>() {

    private val TAG = "AdapUserLike"
    private val prefs = Prefs(activity)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_user_like, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder")

        Glide.with(activity).load(dataList[position].profileUrl).into(holder.profileCIV)
        holder.usernameTV.text = dataList[position].username
        holder.itemLL.setOnClickListener {

        }

    }


    override fun getItemCount(): Int {
        return dataList.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val profileCIV = itemView.findViewById<CircleImageView>(R.id.profileCIV)
        val usernameTV = itemView.findViewById<TextView>(R.id.usernameTV)
        val itemLL = itemView.findViewById<LinearLayout>(R.id.itemLL)
    }

    open class ModelUserLike(
        var uid: String? = null,
        var username: String? = null,
        var profileUrl: String? = null
    )
}