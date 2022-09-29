package com.phasitapp.rupost.repository

import android.app.Activity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.phasitapp.rupost.KEY_COMMENTS_LIKES
import com.phasitapp.rupost.KEY_USERS_LIKE_COMMENTS
import com.phasitapp.rupost.helper.Prefs
import kotlin.collections.HashMap

class RepositoryComment(private var activity: Activity, private var postId: String) {

    private val TAG = "RepositoryPost"

    private val prefs = Prefs(activity)
    private val firestore = Firebase.firestore
    private val database = Firebase.database
    private val storage = FirebaseStorage.getInstance()

    companion object {
        val RESULT_SUCCESS = "success"
        val RESULT_FAIL = "fail"
    }

    private fun getUid(): String {
        return prefs!!.strUid!!
    }

    fun like(commentId: String, like: Boolean) {
        when (like) {
            true -> {

//                val valueCommentLike: MutableMap<String, Any?> = HashMap()
//                valueCommentLike[getUid()] = true
//
//                val valueUserLikeComment: MutableMap<String, Any?> = HashMap()
//                valueUserLikeComment[commentId] = true

                database.getReference("$KEY_COMMENTS_LIKES/$commentId/${getUid()}").setValue(true)
                database.getReference("$KEY_USERS_LIKE_COMMENTS/${getUid()}/$postId/$KEY_COMMENTS_LIKES/$commentId").setValue(true)
            }
            false -> {

                database.getReference("$KEY_COMMENTS_LIKES/$commentId/${getUid()}").removeValue()
                database.getReference("$KEY_USERS_LIKE_COMMENTS/${getUid()}/$postId/$KEY_COMMENTS_LIKES/$commentId").removeValue()
            }
        }
    }

    fun getCountLike(commentId: String, l: (count: Int) -> Unit) {
        database.getReference(KEY_COMMENTS_LIKES).child(commentId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val count = snapshot.childrenCount.toInt()
                    l(count)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun getUserCurrentLike(l: (userLikeCommentList: ArrayList<String>) -> Unit) {

        database.getReference(KEY_USERS_LIKE_COMMENTS).child(getUid()).child(postId)
            .child(KEY_COMMENTS_LIKES).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<String>()
                snapshot.children.forEach {
                    val commentId = it.key
                    list.add(commentId!!)
                }
                l(list)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}