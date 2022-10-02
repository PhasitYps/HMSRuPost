package com.phasitapp.rupost.fragments

import android.app.ActionBar
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.account.request.AccountAuthParams
import com.huawei.hms.support.account.request.AccountAuthParamsHelper
import com.huawei.hms.support.account.result.AuthAccount
import com.huawei.hms.support.account.service.AccountAuthService
import com.huawei.hms.support.api.entity.common.CommonConstant
import com.phasitapp.rupost.*
import com.phasitapp.rupost.Utils.hasUserCurrent
import com.phasitapp.rupost.adapter.AdapPost
import com.phasitapp.rupost.helper.Prefs
import com.phasitapp.rupost.model.ModelPost
import com.phasitapp.rupost.model.ModelUser
import com.phasitapp.rupost.repository.RepositoryPost
import com.phasitapp.rupost.repository.RepositoryUser
import kotlinx.android.synthetic.main.fragment_user.*


class UserFragment : Fragment(R.layout.fragment_user) {
    // Define the log tag.
    private val TAG = "sadafwgeaggaw"
    private var pref: Prefs? = null
    private val firestore = FirebaseFirestore.getInstance()
    private val userRef = firestore.collection(KEY_USERS)
    private lateinit var repositoryUser: RepositoryUser

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        initDialogLoad()
        event()
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "UserFragment onPause")
    }

    private fun init() {
        pref = Prefs(requireContext())
        repositoryUser = RepositoryUser(requireActivity())
        updateUI()



    }

    private val postList = ArrayList<ModelPost>()
    private fun setDataPost() {
        postList.clear()
        val repositoryPost = RepositoryPost(requireActivity())
        repositoryPost.readByUid(pref!!.strUid!!) { result, post ->
            when (result) {
                RepositoryPost.RESULT_SUCCESS -> {
                    postList.addAll(post)
                    setAdap()
                }
                RepositoryPost.RESULT_FAIL -> {

                }
            }
        }
    }

    private fun setAdap() {
        val adapter = AdapPost(requireActivity(), postList)
        val layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        dataRCV.adapter = adapter
        dataRCV.layoutManager = layoutManager
    }

    private fun event() {

        huaweiBTN.setOnClickListener {
            silentSignInByHwId()
        }

        detailMoreTV.setOnClickListener{
            when(bgDetailLL.visibility){
                View.VISIBLE->{
                    bgDetailLL.visibility = View.GONE
                }
                View.GONE->{
                    bgDetailLL.visibility = View.VISIBLE
                }
            }
        }

        editProfileTV.setOnClickListener {

        }
    }


    private val REQUEST_CODE_SIGN_IN = 1000
    private var mAuthService: AccountAuthService? = null
    private fun silentSignInByHwId() {
        dialog_load?.show()
        // 1. Use AccountAuthParams to specify the user information to be obtained after user authorization, including the user ID (OpenID and UnionID), email address, and profile (nickname and picture).
        // 2. By default, DEFAULT_AUTH_REQUEST_PARAM specifies two items to be obtained, that is, the user ID and profile.
        // 3. If your app needs to obtain the user's email address, call setEmail().
        val mAuthParam = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setEmail()
            .createParams()
        // Use AccountAuthParams to build AccountAuthService.
        mAuthService = AccountAuthManager.getService(requireActivity(), mAuthParam)

        // Sign in with a HUAWEI ID silently.
        mAuthService!!.silentSignIn().addOnSuccessListener { authAccount ->
            dealWithResultOfSignIn(authAccount)
        }.addOnFailureListener { e ->
            if (e is ApiException) {
                val apiException = e
                val signInIntent = mAuthService!!.signInIntent
                signInIntent.putExtra(CommonConstant.RequestParams.IS_FULL_SCREEN, true)
                startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN)
            }
        }
    }

    private fun dealWithResultOfSignIn(authAccount: AuthAccount) {
        // Obtain the HUAWEI ID information.
        Log.i(TAG, "display name:" + authAccount.displayName)
        Log.i(TAG, "photo uri string:" + authAccount.avatarUriString)
        Log.i(TAG, "photo uri:" + authAccount.avatarUri)
        Log.i(TAG, "email:" + authAccount.email)
        Log.i(TAG, "openid:" + authAccount.openId)
        Log.i(TAG, "unionid:" + authAccount.unionId)


        //check user
        repositoryUser.getByOpenId(authAccount.openId){ model: ModelUser? ->
            if(model != null){
                setUserCurrent(model!!)
                updateUI()
            }else{
                userRef.document(KEY_INFORMATION).get().addOnSuccessListener {
                    if (it.exists()) {
                        var usercount = it.get(KEY_USERCOUNT).toString().toInt()

                        val model = ModelUser(
                            openId = authAccount.openId,
                            unionId = authAccount.unionId,
                            username = "สมาชิกหมายเลข $usercount",
                            email = authAccount.email,
                            profile = authAccount.avatarUriString,
                            createDate = System.currentTimeMillis(),
                            updateDate = System.currentTimeMillis()
                        )
                        userRef.document(KEY_INFORMATION).update(KEY_USERCOUNT, ++usercount)
                        createUser(model)
                    }
                }
            }
            dialog_load?.dismiss()
        }

//        userRef.whereEqualTo(KEY_OPENID, authAccount.openId).get()
//            .addOnSuccessListener { querySnapshot ->
//
//                if (!querySnapshot.isEmpty) {
//                    val m = querySnapshot!!.first().toObject(ModelUser::class.java)
//                    setUserCurrent(m!!)
//                    updateUI()
//                } else {
//                    userRef.document(KEY_INFORMATION).get().addOnSuccessListener {
//                        if (it.exists()) {
//                            var usercount = it.get(KEY_USERCOUNT).toString().toInt()
//
//                            val model = ModelUser(
//                                uid = System.currentTimeMillis().toString(),
//                                openId = authAccount.openId,
//                                unionId = authAccount.unionId,
//                                username = "สมาชิกหมายเลข $usercount",
//                                email = authAccount.email,
//                                profile = authAccount.avatarUriString,
//                                displayName = authAccount.displayName,
//                                createDate = System.currentTimeMillis(),
//                                updateDate = System.currentTimeMillis()
//                            )
//
//                            userRef.document(KEY_INFORMATION).update(KEY_USERCOUNT, ++usercount)
//                            createUser(model)
//                        }
//                    }
//                }
//                dialog_load?.dismiss()
//
//            }.addOnFailureListener {
//            Toast.makeText(requireActivity(), "exception: ${it.message}", Toast.LENGTH_SHORT).show()
//            dialog_load!!.dismiss()
//        }

    }

    private fun createUser(model: ModelUser) {
        dialog_load!!.show()

        repositoryUser.create(model){ result ->
            when(result){
                RepositoryUser.RESULT_SUCCESS->{
                    setUserCurrent(model)
                    updateUI()
                }
                RepositoryUser.RESULT_FAIL->{
                    Toast.makeText(
                        requireActivity(),
                        "สร้างชื่อผู้ใช้เกิดข้อผิดพลาด",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            dialog_load!!.dismiss()
        }
    }

    private fun updateUI() {

        when (hasUserCurrent(requireContext())) {
            false -> {
                bgLoginRL.visibility = View.VISIBLE
                bgProfileRL.visibility = View.GONE
                loadPostPB.visibility = View.GONE
            }
            true -> {
                bgLoginRL.visibility = View.GONE
                bgProfileRL.visibility = View.VISIBLE
                loadPostPB.visibility = View.VISIBLE
                bgDetailLL.visibility = View.GONE

                //set deatail user

                repositoryUser.getByUid(pref!!.strUid!!){ model->

                    if(model != null){
                        loadPostPB.visibility = View.GONE
                        Log.i("sadafwgeaggaw", "username: " + model.username)
                        Log.i("sadafwgeaggaw", "description: " + model.description)

                        Glide.with(requireActivity()).load(pref!!.strPhotoUri).centerCrop()
                            .into(imageUrlUserIV)
                        usernameTV.text = model.username
                        menunameTV.text = model.description

                        setDataPost()
                    }else{
                        Toast.makeText(
                            requireActivity(),
                            "เกิดข้อผิดพบพลาด ติดต่อฐานข้อมูล get users",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

//                userRef.document(pref!!.strUid!!).get().addOnSuccessListener {
//                    loadPostPB.visibility = View.GONE
//                    val model = it.toObject(ModelUser::class.java)!!
//
//                    Log.i("sadafwgeaggaw", "username: " + model.username)
//                    Log.i("sadafwgeaggaw", "description: " + model.description)
//
//                    Glide.with(requireActivity()).load(pref!!.strPhotoUri).centerCrop()
//                        .into(imageUrlUserIV)
//                    usernameTV.text = model.username
//                    menunameTV.text = model.description
//
//                    setDataPost()
//
//                }.addOnFailureListener {
//                    Toast.makeText(
//                        requireActivity(),
//                        "exception: ${it.message}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
            }
        }
    }

    private fun setUserCurrent(model: ModelUser?) {
        if (model != null) {
            pref!!.strEmail = model.email
            pref!!.strUsername = model.username
            pref!!.strOpenId = model.openId
            pref!!.strPhotoUri = model.profile
            pref!!.strUid = model.uid
        } else {
            pref!!.strEmail = ""
            pref!!.strUsername = ""
            pref!!.strOpenId = ""
            pref!!.strPhotoUri = ""
            pref!!.strUid = ""
        }
    }

    private var dialog_load: Dialog? = null
    private fun initDialogLoad() {
        dialog_load = Dialog(requireActivity())
        dialog_load!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog_load!!.setContentView(R.layout.dialog_loading)
        dialog_load!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog_load!!.window!!.setLayout(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )

        dialog_load!!.create()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Log.i(TAG, "onActivitResult of sigInInIntent, request code: " + REQUEST_CODE_SIGN_IN)
            val authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data)
            if (authAccountTask.isSuccessful) {
                Log.i(TAG, "sign in success")
                // The sign-in is successful, and the authAccount object that contains the HUAWEI ID information is obtained.
                val authAccount = authAccountTask.result
                dealWithResultOfSignIn(authAccount)
                Log.i(
                    TAG,
                    "onActivitResult of sigInInIntent, request code: " + REQUEST_CODE_SIGN_IN
                )
                dialog_load?.dismiss()
            } else {
                // The sign-in failed. Find the failure cause from the status code. For more information, please refer to Error Codes.
                Log.e(
                    TAG,
                    "sign in failed : " + (authAccountTask.exception as ApiException).statusCode
                )
                Log.e(
                    TAG,
                    "sign in failed : " + (authAccountTask.exception as ApiException).statusCode
                )
                dialog_load?.dismiss()
            }
        }
    }

}