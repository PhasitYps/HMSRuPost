package com.phasitapp.rupost.repository

import android.app.Activity
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.phasitapp.rupost.*
import com.phasitapp.rupost.helper.Prefs
import com.phasitapp.rupost.model.ModelUser


class RepositoryUser(private var activity: Activity) {

    private val TAG = "RepositoryPost"

    private val prefs = Prefs(activity)
    private val firestore = Firebase.firestore
    private val database = Firebase.database
    private val storage = FirebaseStorage.getInstance()

    companion object {
        val RESULT_SUCCESS = "success"
        val RESULT_FAIL = "fail"
    }

    fun create(model: ModelUser, l: (result: String) -> Unit) {

        val uid = database.getReference(KEY_USERS).ref.push().key

        if (uid == null) {
            Toast.makeText(activity, "เกิดข้อผิดพลาดติดต่อคีย์", Toast.LENGTH_SHORT).show()
        } else {

            val user: MutableMap<String, Any?> = HashMap()
            user[KEY_UID] = uid
            user[KEY_OPENID] = model.openId
            user[KEY_EMAIL] = model.email
            user[KEY_PROFILE] = model.profile
            user[KEY_USERNAME] = model.username
            user[KEY_UNION_ID] = model.unionId
            user[KEY_DESCIPTION] = model.description

            user[KEY_CREATEDATE] = model.createDate
            user[KEY_UPDATEDATE] = model.updateDate

            database.getReference(KEY_USERS).child(uid).setValue(user).addOnSuccessListener {

                l(RESULT_SUCCESS)
            }.addOnFailureListener {
                l(RESULT_FAIL)
            }
        }
    }

    fun getByOpenId(openId: String, l: (model: ModelUser?) -> Unit) {

        database.getReference(KEY_USERS).orderByChild(KEY_OPENID).equalTo(openId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = ArrayList<ModelUser>()
                    snapshot.children.forEach {
                        val model = ModelUser()
                        model.openId = it.child(KEY_OPENID).getValue(String::class.java)
                        model.uid = it.child(KEY_UID).getValue(String::class.java)
                        model.createDate = it.child(KEY_CREATEDATE).getValue(Long::class.java)
                        model.email = it.child(KEY_EMAIL).getValue(String::class.java)
                        model.username = it.child(KEY_USERNAME).getValue(String::class.java)
                        model.description = it.child(KEY_DESCIPTION).getValue(String::class.java)
                        model.profile = it.child(KEY_PROFILE).getValue(String::class.java)

                        list.add(model)
                    }
                    if(list.size > 0){
                        l(list[0])
                    }else{
                        l(null)
                    }

                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun getByUid(uid: String, l: (model: ModelUser?) -> Unit) {

        database.getReference(KEY_USERS).child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = ArrayList<ModelUser>()
                    snapshot.children.forEach {
                        val model = ModelUser()
                        model.openId = it.child(KEY_OPENID).getValue(String::class.java)
                        model.uid = it.child(KEY_UID).getValue(String::class.java)
                        model.createDate = it.child(KEY_CREATEDATE).getValue(Long::class.java)
                        model.email = it.child(KEY_EMAIL).getValue(String::class.java)
                        model.username = it.child(KEY_USERNAME).getValue(String::class.java)
                        model.description = it.child(KEY_DESCIPTION).getValue(String::class.java)
                        model.profile = it.child(KEY_PROFILE).getValue(String::class.java)

                        list.add(model)
                    }
                    if(list.size > 0){
                        l(list[0])
                    }else{
                        l(null)
                    }

                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

}