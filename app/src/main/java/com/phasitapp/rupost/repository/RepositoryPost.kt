package com.phasitapp.rupost.repository

import android.app.Activity
import android.util.Log
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
import com.phasitapp.rupost.model.ModelComment
import com.phasitapp.rupost.model.ModelPost
import java.io.*


class RepositoryPost(private var activity: Activity) {

    private val TAG = "RepositoryPost"

    private val prefs = Prefs(activity)
    private val firestore = Firebase.firestore
    private val database = Firebase.database
    private val storage = FirebaseStorage.getInstance()

    companion object {
        val RESULT_SUCCESS = "success"
        val RESULT_FAIL = "fail"
    }

    fun post(model: ModelPost, l: (result: String) -> Unit) {
        val uid = prefs.strUid
        val postRef = firestore.collection(KEY_POSTS)
        val imageList = model.images

        val post: MutableMap<String, Any?> = HashMap()
        post[KEY_UID] = uid
        post[KEY_CATEGORY] = model.category
        post[KEY_TARGET_GROUP] = model.targetGroup
        post[KEY_TITLE] = model.title
        post[KEY_DESCIPTION] = model.desciption
        post[KEY_LATITUDE] = model.latitude
        post[KEY_LONGITUDE] = model.longitude
        post[KEY_ADDRESS] = model.address
        post[KEY_CREATEDATE] = model.createDate
        post[KEY_UPDATEDATE] = model.updateDate
        post[KEY_VIEWER] = 0

        postRef.add(post).addOnSuccessListener {
            Log.i(TAG, "post: Post Success.")
            val postid = it.id
            val imageLinkList = ArrayList<String>()

            if (imageList.isNotEmpty()) {
                imageList.forEach { imagePath ->
                    val postStorageRef =
                        storage.getReference(KEY_POSTS).child("${System.currentTimeMillis()}.jpg")
                    val file = File(imagePath)
                    val byteArray = convertFileToByteArray(file)
                    postStorageRef.putBytes(byteArray).addOnSuccessListener {
                        postStorageRef.downloadUrl.addOnSuccessListener {
                            imageLinkList.add(it.toString())

                            if (imagePath.equals(imageList.last())) {
                                postRef.document(postid).update(KEY_IMAGES, imageLinkList)
                                    .addOnSuccessListener {
                                        //when upload image post success
                                        l(RESULT_SUCCESS)

                                    }.addOnFailureListener {
                                        //when upload image post fail
                                        l(RESULT_FAIL)
                                    }
                            }
                        }
                    }
                }
            } else {
                //when upload post success not have image
                l(RESULT_SUCCESS)
            }

        }.addOnFailureListener {
            Toast.makeText(activity, "e: ${it.message}", Toast.LENGTH_SHORT).show()
            Log.i(TAG, "post: ${it.message}")
            l(RESULT_FAIL)
        }
    }

    fun readByUid(uid: String, l: (result: String, post: ArrayList<ModelPost>) -> Unit) {
        val list = ArrayList<ModelPost>()
        firestore.collection(KEY_POSTS).whereEqualTo(KEY_UID, uid).get()
            .addOnSuccessListener { documents ->
                documents.forEach { document ->

                    val model = document.toObject(ModelPost::class.java)
                    model.id = document.id
                    list.add(model)
                }
                l(RESULT_SUCCESS, list)

            }.addOnFailureListener {
                Toast.makeText(activity, "exception: ${it.message}", Toast.LENGTH_SHORT).show()
                Log.i("fwafawf", "e: ${it.message}")
                l(RESULT_FAIL, list)
            }
    }

    fun read(l: (result: String, post: ArrayList<ModelPost>) -> Unit) {
        val list = ArrayList<ModelPost>()
        firestore.collection(KEY_POSTS).get().addOnSuccessListener { documents ->

            documents.forEach { document ->

                Log.i("fwafawf", "address: " + document[KEY_ADDRESS])
                Log.i("fwafawf", "category: " + document[KEY_CATEGORY])
                Log.i("fwafawf", "createDate: " + document[KEY_CREATEDATE])
                Log.i("fwafawf", "desciption: " + document[KEY_DESCIPTION])
                Log.i("fwafawf", "lat: " + document[KEY_LATITUDE])
                Log.i("fwafawf", "long: " + document[KEY_LONGITUDE])
                Log.i("fwafawf", "target: " + document[KEY_TARGET_GROUP])
                Log.i("fwafawf", "title: " + document[KEY_TITLE])
                Log.i("fwafawf", "uid: " + document[KEY_UID])
                Log.i("fwafawf", "updateDate: " + document[KEY_UPDATEDATE])
                Log.i("fwafawf", "viewer: " + document[KEY_VIEWER])

                val model = document.toObject(ModelPost::class.java)
                model.id = document.id

                Log.i("fwafawf", "imagelist: " + model.images.size)
                Log.i("fwafawf", "id: " + model.id)
                list.add(model)
            }
            l(RESULT_SUCCESS, list)

        }.addOnFailureListener {
            Toast.makeText(activity, "exception: ${it.message}", Toast.LENGTH_SHORT).show()
            Log.i("fwafawf", "e: ${it.message}")
            l(RESULT_FAIL, list)
        }
    }

    fun like(postId: String, like: Boolean) {
        when (like) {
            true -> {
                database.getReference(KEY_POSTS).child(postId).child(KEY_LIKES).child(prefs.strUid!!)
                    .setValue(true)
            }
            false -> {
                database.getReference(KEY_POSTS).child(postId).child(KEY_LIKES).child(prefs.strUid!!)
                    .removeValue()
            }
        }
    }

    fun getComments(postId: String, l: (comments: ArrayList<ModelComment>) -> Unit) {
        database.getReference(KEY_POSTS).child(postId).child(KEY_COMMENTS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = ArrayList<ModelComment>()
                    snapshot.children.forEach {
                        val model = ModelComment()
                        model!!.id = it.key
                        model.uid = it.child(KEY_UID).getValue(String::class.java)
                        model.message = it.child(KEY_MESSAGE).getValue(String::class.java)
                        model.createDate = it.child(KEY_CREATEDATE).getValue(String::class.java)
                        model.countLike = it.child(KEY_LIKES).childrenCount.toInt()
                        model.tag = it.child(KEY_TAG).getValue(String::class.java)
                        model.postId = postId

                        list.add(model)
                    }
                    l(list)
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun getCommentsCount(postId: String, l: (count: Int) -> Unit){
        database.getReference(KEY_POSTS).child(postId).child(KEY_COMMENTS).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount.toInt()
                l(count)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun comments(postId: String, model: ModelComment, l: (comment: ModelComment?) -> Unit){
        val comment: MutableMap<String, Any?> = java.util.HashMap()
        comment[KEY_UID] = model.uid
        comment[KEY_MESSAGE] = model.message
        comment[KEY_CREATEDATE] = model.createDate
        comment[KEY_TAG] = model.tag

        val keyRef = database.getReference(KEY_POSTS).child(postId).child(KEY_COMMENTS).push()
        model.id = keyRef.key
        keyRef.setValue(comment).addOnSuccessListener {
           l(model)
        }.addOnFailureListener {
            Toast.makeText(activity, "เกิดข้อผิดพลาด ติดต่อ ${it.message}", Toast.LENGTH_LONG).show()
            l(null)
        }
    }

    fun getStaticLike(postId: String, l: (userLikes: ArrayList<String>) -> Unit) {
        database.getReference(KEY_POSTS).child(postId).child(KEY_LIKES)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataList = ArrayList<String>()
                    snapshot.children.forEach {
                        dataList.add(it.key!!)
                    }
                    l(dataList)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun convertFileToByteArray(file: File): ByteArray {
        val size = file.length().toInt()
        val bytes = ByteArray(size)
        try {
            val buf = BufferedInputStream(FileInputStream(file))
            buf.read(bytes, 0, bytes.size)
            buf.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bytes
    }
}