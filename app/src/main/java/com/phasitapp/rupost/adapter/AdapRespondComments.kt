package com.phasitapp.rupost.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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


class AdapRespondComments(private var activity: Activity, private val commentList: ArrayList<ModelComment>, private val repositoryComment: RepositoryComment) :
    RecyclerView.Adapter<AdapRespondComments.ViewHolder>() {

    private val TAG = "AdapImagePost"
    private val prefs = Prefs(activity)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_comment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder")


        setDetail(holder, position)
        event(holder, position)
    }

    private fun setDetail(holder: ViewHolder, position: Int){
        Glide.with(activity).load(commentList[position].profile).into(holder.profileCIV)
        holder.usernameTV.text = commentList[position].username
        holder.messageTV.text = commentList[position].message
        holder.createDateTV.text = formatCreateDate(commentList[position].createDate.toString().toLong())

        //holder.countLikeTV.text = "${commentList[position].countLike}"
        setLike(commentList[position].countLike, holder, position)

        val userlike = commentList[position].currentUserLike
        holder.likeIV.tag = userlike
        if(userlike){
            Glide.with(activity).load(R.drawable.ic_heart_red).into(holder.likeIV)
        }else{
            Glide.with(activity).load(R.drawable.ic_heart).into(holder.likeIV)
        }
    }
    private fun event(holder: ViewHolder, position: Int){

        holder.likeIV.setOnClickListener {
            if (prefs.strUid != "") {
                if (holder.likeIV.tag != null) {
                    val currnetLike = holder.likeIV.tag as Boolean
                    when (currnetLike) {
                        true -> {
                            repositoryComment.like(commentList[position].id!!, false)
                            holder.likeIV.tag = false
                            Glide.with(activity).load(R.drawable.ic_heart).into(holder.likeIV)
                            minusLike(holder, position)
                        }
                        false -> {
                            repositoryComment.like(commentList[position].id!!, true)
                            holder.likeIV.tag = true
                            Glide.with(activity).load(R.drawable.ic_heart_red).into(holder.likeIV)
                            plusLike(holder, position)
                        }
                    }
                }
            }

        }

        holder.commentIV.setOnClickListener {
            val intent = Intent(activity, RespondCommentActivity::class.java)
            intent.putExtra(KEY_DATA, commentList[position])
            activity.startActivity(intent)
        }

        holder.messageTV.setOnClickListener(object : DoubleClickListener() {
            override fun onDoubleClick(v: View?) {
                Log.i("dawgewg", "This is DoubleClickListener: $position")
                if (prefs.strUid != "") {
                    if (holder.likeIV.tag != null) {
                        val currnetLike = holder.likeIV.tag as Boolean
                        if (!currnetLike) {
                            repositoryComment.like(commentList[position].id!!, true)
                            holder.likeIV.tag = true
                            Glide.with(activity).load(R.drawable.ic_heart_red).into(holder.likeIV)
                            plusLike(holder, position)
                        }
                    }
                }
            }

        })
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    private fun plusLike(holder: ViewHolder, position: Int) {
        val model = commentList[position]
        model.countLike++
        holder.countLikeTV.text = "${model.countLike}"
        if (model.countLike != 0) {
            holder.bgLikeCountLL.visibility = View.VISIBLE
        } else {
            holder.bgLikeCountLL.visibility = View.GONE
        }
    }

    private fun minusLike(holder: ViewHolder, position: Int) {
        val model = commentList[position]
        model.countLike--
        holder.countLikeTV.text = "${model.countLike}"
        if (model.countLike != 0) {
            holder.bgLikeCountLL.visibility = View.VISIBLE
        } else {
            holder.bgLikeCountLL.visibility = View.GONE
        }
    }

    private fun setLike(count: Int, holder: ViewHolder, position: Int) {
        val model = commentList[position]
        model.countLike = count
        holder.countLikeTV.text = "${model.countLike}"
        if (model.countLike != 0) {
            holder.bgLikeCountLL.visibility = View.VISIBLE
        } else {
            holder.bgLikeCountLL.visibility = View.GONE
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val profileCIV = itemView.findViewById<CircleImageView>(R.id.profileCIV)
        val createDateTV = itemView.findViewById<TextView>(R.id.createDateTV)
        val usernameTV = itemView.findViewById<TextView>(R.id.usernameTV)
        val messageTV = itemView.findViewById<TextView>(R.id.messageTV)
        val commentIV = itemView.findViewById<ImageView>(R.id.commentIV)
        val likeIV = itemView.findViewById<ImageView>(R.id.likeIV)
        val countLikeTV = itemView.findViewById<TextView>(R.id.countLikeTV)
        val bgLikeCountLL = itemView.findViewById<LinearLayout>(R.id.bgLikeCountLL)

        init {
            bgLikeCountLL.visibility = View.GONE
        }

    }

    abstract class DoubleClickListener : View.OnClickListener {
        var lastClickTime: Long = 0
        override fun onClick(v: View?) {
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                onDoubleClick(v)
            }
            lastClickTime = clickTime
        }

        abstract fun onDoubleClick(v: View?)

        companion object {
            private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds
        }
    }

}