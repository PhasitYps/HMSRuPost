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
import com.google.firebase.firestore.FirebaseFirestore
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.account.request.AccountAuthParams
import com.huawei.hms.support.account.request.AccountAuthParamsHelper
import com.huawei.hms.support.account.result.AuthAccount
import com.huawei.hms.support.api.entity.common.CommonConstant
import com.phasitapp.rupost.KEY_OPENID
import com.phasitapp.rupost.KEY_USERS
import com.phasitapp.rupost.R
import com.phasitapp.rupost.Utils
import com.phasitapp.rupost.Utils.hasUserCurrent
import com.phasitapp.rupost.helper.Prefs
import com.phasitapp.rupost.model.ModelUser
import kotlinx.android.synthetic.main.fragment_user.*


class UserFragment : Fragment(R.layout.fragment_user) {
    // Define the log tag.
    private val TAG = "sadafwgeaggaw"
    private var pref: Prefs? = null
    private val firestore = FirebaseFirestore.getInstance()
    private val userRef = firestore.collection(KEY_USERS)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        initDialogLoad()
        event()

    }

    private fun init() {
        pref = Prefs(requireContext())
        updateUI()
        Log.i(TAG, "strOpenId: " + pref!!.strOpenId)
    }

    private fun event() {

        huaweiBTN.setOnClickListener {
            silentSignInByHwId()
        }
    }


    private val REQUEST_CODE_SIGN_IN = 1000
    private fun silentSignInByHwId() {
        dialog_load?.show()
        // 1. Use AccountAuthParams to specify the user information to be obtained after user authorization, including the user ID (OpenID and UnionID), email address, and profile (nickname and picture).
        // 2. By default, DEFAULT_AUTH_REQUEST_PARAM specifies two items to be obtained, that is, the user ID and profile.
        // 3. If your app needs to obtain the user's email address, call setEmail().
        var mAuthParam = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setEmail()
            .createParams()
        // Use AccountAuthParams to build AccountAuthService.
        val mAuthService = AccountAuthManager.getService(requireActivity(), mAuthParam)

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

        val model = ModelUser(
            openId = authAccount.openId,
            unionId = authAccount.unionId,
            username = "สมาชิกหมายเลข 00000001",
            email = authAccount.email,
            photoUri = authAccount.avatarUriString,
            displayName = authAccount.displayName,
            createDate = System.currentTimeMillis(),
            updateDate = System.currentTimeMillis()
        )

        firestore.collection(KEY_USERS).whereEqualTo(KEY_OPENID, authAccount.openId)
            .addSnapshotListener { querySnapshot, error ->
                when (querySnapshot!!.size()) {
                    0 -> {
                        createUser(model)
                    }
                    1 -> {
                        setUserCurrent(model)
                        updateUI()
                    }
                }
                dialog_load?.dismiss()
            }

    }

    private fun createUser(model: ModelUser) {
        dialog_load!!.show()
        firestore.collection(KEY_USERS).add(model).addOnCompleteListener {
            if(it.isSuccessful){
                setUserCurrent(model)
                updateUI()
            }else{
                Toast.makeText(requireActivity(), "สร้างชื่อผู้ใช้เกิดข้อผิดพลาด", Toast.LENGTH_SHORT).show()
            }
            dialog_load!!.dismiss()
        }
    }

    private fun updateUI() {

        when(hasUserCurrent(requireContext())){
            false -> {
                bgLoginRL.visibility = View.VISIBLE
                bgProfileRL.visibility = View.GONE
            }
            true->{
                bgLoginRL.visibility = View.GONE
                bgProfileRL.visibility = View.VISIBLE

                //set deatail user
            }

        }
    }

    private fun setUserCurrent(model: ModelUser){
        pref!!.strEmail = model.email
        pref!!.strUsername = model.username
        pref!!.strOpenId = model.openId
        pref!!.strPhotoUri = model.photoUri
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