package com.phasitapp.rupost.fragments

import android.app.ActionBar
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.Window
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.CollectionReference
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.account.request.AccountAuthParams
import com.huawei.hms.support.account.request.AccountAuthParamsHelper
import com.huawei.hms.support.account.result.AuthAccount
import com.huawei.hms.support.account.service.AccountAuthService
import com.huawei.hms.support.api.entity.common.CommonConstant
import com.phasitapp.rupost.R
import com.phasitapp.rupost.dialog.BottomSheetMenu
import kotlinx.android.synthetic.main.bottom_sheet_menu.*
import kotlinx.android.synthetic.main.fragment_user.*


class UserFragment : Fragment(R.layout.fragment_user) {
    // Define the log tag.
    private val TAG = "sadafwgeaggaw"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signOut()
        init()
        initDialogLoad()
        event()
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private fun signOut() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(requireActivity().getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        googleSignInClient.signOut().addOnCompleteListener {
            if(it.isSuccessful){
                auth.signOut()
            }
        }
    }

    private fun init(){
        auth = FirebaseAuth.getInstance()
    }

    private fun event(){

        googleLL.setOnClickListener {
            signInGoogle()
        }

        huaweiBTN.setOnClickListener {
            silentSignInByHwId()
        }
    }

    private var mAuthService: AccountAuthService? = null
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
        mAuthService = AccountAuthManager.getService(requireActivity(), mAuthParam)

        // Sign in with a HUAWEI ID silently.
        val task = mAuthService!!.silentSignIn()
        task.addOnSuccessListener { authAccount -> // The silent sign-in is successful. Process the returned AuthAccount object to obtain the HUAWEI ID information.
            dealWithResultOfSignIn(authAccount)
        }
        task.addOnFailureListener { e -> // The silent sign-in fails. Your app will call getSignInIntent() to show the authorization or sign-in screen.
            if (e is ApiException) {
                val apiException = e
                val signInIntent = mAuthService!!.getSignInIntent()
                // If your app appears in full screen mode when a user tries to sign in, that is, with no status bar at the top of the device screen, add the following parameter in the intent:
                // intent.putExtra(CommonConstant.RequestParams.IS_FULL_SCREEN, true)
                // Check the details in this FAQ.
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
        Log.i(TAG, "email:" + authAccount.getEmail())
        Log.i(TAG, "openid:" + authAccount.getOpenId())
        Log.i(TAG, "unionid:" + authAccount.getUnionId())

    }

    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleApiClient: GoogleSignInClient
    private val RC_SIGN_IN = 1002
    private fun signInGoogle() {
        dialog_load?.show()
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        //  .requestScopes(Scope(Scopes.PROFILE), Scope("https://www.googleapis.com/auth/drive.file"))
        //  .requestScopes(Scope(Scopes.DRIVE_APPFOLDER), Scope(Scopes.DRIVE_FILE)) //Scope(Scopes.DRIVE_FILE)

        mGoogleApiClient = GoogleSignIn.getClient(requireActivity(), signInOptions)
        startActivityForResult(mGoogleApiClient.signInIntent, RC_SIGN_IN)
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                Log.d("test", "signInWithCredential:success")
                val user = auth.currentUser
                val email = user?.email
                val name = user?.displayName
                val phoneNumber = user?.phoneNumber
                val profile = user?.photoUrl


            } else {

                Log.w("test", "signInWithCredential:failure", task.exception)
//                showToast("Authentication Failed.")
//                updateUI(null)
            }
        }
    }

    private var dialog_load: Dialog? = null
    private fun initDialogLoad(){
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
                Log.i(TAG,"sign in success")
                // The sign-in is successful, and the authAccount object that contains the HUAWEI ID information is obtained.
                val authAccount = authAccountTask.result
                dealWithResultOfSignIn(authAccount)
                Log.i(TAG, "onActivitResult of sigInInIntent, request code: " + REQUEST_CODE_SIGN_IN)
                dialog_load?.dismiss()
            } else {
                // The sign-in failed. Find the failure cause from the status code. For more information, please refer to Error Codes.
                Log.e(TAG,"sign in failed : " + (authAccountTask.exception as ApiException).statusCode)
                Log.e(TAG, "sign in failed : " + (authAccountTask.exception as ApiException).statusCode)
                dialog_load?.dismiss()
            }
        }

        if (requestCode == RC_SIGN_IN) {

            GoogleSignIn.getSignedInAccountFromIntent(data).addOnCompleteListener {
                dialog_load!!.show()

                if(it.isSuccessful){
                    val account = it.result
                    firebaseAuthWithGoogle(account.idToken!!)

                    Log.d("test", "firebaseAuthWithGoogle:" + account.id)
                    dialog_load?.dismiss()
                }else{
                    Log.w("test", "Google sign in failed: ", it.exception)
                    dialog_load!!.dismiss()
                    // ...
                }
            }
        }
    }

}