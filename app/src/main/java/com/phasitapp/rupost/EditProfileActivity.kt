package com.phasitapp.rupost

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.phasitapp.rupost.helper.Prefs
import com.phasitapp.rupost.model.ModelUser
import com.phasitapp.rupost.repository.RepositoryUser
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {
    
    private lateinit var prefs: Prefs
    private lateinit var repositoryUser: RepositoryUser
    private var profileUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        init()
        initDialogLoad()
        event()
    }
    
    private fun init(){
        prefs = Prefs(this)
        repositoryUser = RepositoryUser(this)
        repositoryUser.getByUid(prefs.strUid!!){ model->

            Glide.with(this).load(model!!.profile).into(profileCIV)
            usernameEDT.setText(model!!.username)
            detailEDT.setText(model!!.description)
            profileUrl = model!!.profile

        }

    }


    private var dialog_load: Dialog? = null
    private fun initDialogLoad() {
        dialog_load = Dialog(this)
        dialog_load!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog_load!!.setContentView(R.layout.dialog_loading)
        dialog_load!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog_load!!.window!!.setLayout(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )

        dialog_load!!.create()
    }

    private fun event(){

        saveCV.setOnClickListener {
            if(usernameEDT.text.isBlank()){
                Toast.makeText(this, "โปรดใส่ชื่อของคุณ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val username = usernameEDT.text.toString()
            val description = detailEDT.text.toString()

            val model = ModelUser(
                username = username,
                description = description,
                profile = profileUrl!!,
                updateDate = System.currentTimeMillis()
            )

            repositoryUser.update(model){ result ->
                when(result){
                    RepositoryUser.RESULT_SUCCESS->{
                        setUserCurrent(model)
                        Toast.makeText(this, "เเก้ไขเรียบร้อย", Toast.LENGTH_SHORT).show()

                        val data = Intent()
                        data.putExtra(KEY_PROFILE, profileUrl)
                        data.putExtra(KEY_USERNAME, username)
                        data.putExtra(KEY_DESCIPTION, description)
                        setResult(RESULT_OK, data)

                        dialog_load!!.dismiss()
                        finish()
                    }
                    RepositoryUser.RESULT_FAIL->{
                        Toast.makeText(this, "เกิดข้อผิดพลาดติดต่อ update repository user", Toast.LENGTH_SHORT).show()
                        dialog_load!!.dismiss()
                    }
                }
            }
        }

        editDetailRL.setOnClickListener{
            detailEDT.requestFocus()
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(detailEDT, InputMethodManager.SHOW_IMPLICIT)
        }

        backIV.setOnClickListener {
            finish()
        }

    }

    private fun setUserCurrent(model: ModelUser?) {
        if (model != null) {
            prefs!!.strEmail = model.email
            prefs!!.strUsername = model.username
            prefs!!.strPhotoUri = model.profile
        }
    }

}