package com.phasitapp.rupost


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.huawei.hms.maps.MapsInitializer
import com.phasitapp.rupost.dialog.BottomSheetMenu
import com.phasitapp.rupost.fragments.HomeFragment
import com.phasitapp.rupost.fragments.NotifyFragment
import com.phasitapp.rupost.fragments.BookMarkFragment
import com.phasitapp.rupost.fragments.UserFragment
import com.phasitapp.rupost.model.ModelPost
import com.phasitapp.rupost.repository.RepositoryPost
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(this)
        setContentView(R.layout.activity_main)

        //addFragment()
        changeMenu("home")

        event()
        setAnimaleWhenClickIcon()

        //addPostTest()

    }

    private fun addPostTest(){

        val imageDir = File(filesDir, FOLDER_IMAGES)
        val image1 = File(imageDir, "4464.jpg")
        val image2 = File(imageDir, "4465.jpg")


        val repositoryPost = RepositoryPost(this)

        val model1 = ModelPost(
            uid = "1664106197257",
            title = "เหตุน้ำท่วม",
            category = "เเบ่งปัน",
            targetGroup = "สาธารณะ",
            desciption = "มีน้ำท่วมถนนในซอย เอกประจิม 2 ถึงข้อเท้า",
            latitude = "13.966143430294952",
            longitude = "100.589037936369",
            address = "ซ. เอกประจิม 2/2 ตำบล หลักหก อำเภอเมืองปทุมธานี ปทุมธานี 12000",
            viewer = 0,
            createDate = "${System.currentTimeMillis()}",
            updateDate = "${System.currentTimeMillis()}",
            images = arrayListOf(image1.path)
        )
        val model2 = ModelPost(
            uid = "1664106197257",
            title = "เหตุน้ำท่วม",
            category = "เเบ่งปัน",
            targetGroup = "สาธารณะ",
            desciption = "มีน้ำท่วมถนนในซอย เอกประจิม 1 ครับระวังกันด้วยนะครับ",
            latitude = "13.965370321057081",
            longitude = "100.58946797134053",
            address = "999 ซ. เอกประจิม 2/1 ตำบล หลักหก อำเภอเมืองปทุมธานี ปทุมธานี 12000",
            viewer = 0,
            createDate = "${System.currentTimeMillis()}",
            updateDate = "${System.currentTimeMillis()}",
            images = arrayListOf(image2.path, image1.path)
        )

        val model3 = ModelPost(
            uid = "1664106197257",
            title = "เหตุน้ำท่วม",
            category = "เเบ่งปัน",
            targetGroup = "สาธารณะ",
            desciption = "หน้ามอรังสิตมีน้ำท่วมสูงมากๆ ช่วยด้วย!",
            latitude = "13.965158008078607",
            longitude = "100.58834249685813",
            address = "ตำบล หลักหก อำเภอเมืองปทุมธานี ปทุมธานี 12000",
            viewer = 0,
            createDate = "${System.currentTimeMillis()}",
            updateDate = "${System.currentTimeMillis()}",
            images = arrayListOf(image2.path)
        )

        repositoryPost.post(model1) { result ->
            when (result) {
                RepositoryPost.RESULT_SUCCESS -> {
                    Toast.makeText(this, "Add Post Success", Toast.LENGTH_SHORT).show()
                }
                RepositoryPost.RESULT_FAIL -> {
                    Toast.makeText(this, "Add Post Fail", Toast.LENGTH_SHORT).show()
                }
            }
        }
        repositoryPost.post(model2) { result ->
            when (result) {
                RepositoryPost.RESULT_SUCCESS -> {
                    Toast.makeText(this, "Add Post Success", Toast.LENGTH_SHORT).show()
                }
                RepositoryPost.RESULT_FAIL -> {
                    Toast.makeText(this, "Add Post Fail", Toast.LENGTH_SHORT).show()
                }
            }
        }
        repositoryPost.post(model3) { result ->
            when (result) {
                RepositoryPost.RESULT_SUCCESS -> {
                    Toast.makeText(this, "Add Post Success", Toast.LENGTH_SHORT).show()
                }
                RepositoryPost.RESULT_FAIL -> {
                    Toast.makeText(this, "Add Post Fail", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun event() {

        menuHomeRL.setOnClickListener {
            changeMenu("home")
        }
        menuMapRL.setOnClickListener {
            changeMenu("bookmark")
        }
        menuNotifyRL.setOnClickListener {
            changeMenu("notify")
        }
        menuUserRL.setOnClickListener {
            changeMenu("user")
        }
        postLL.setOnClickListener {
            showMenuPostBottomDialog()
        }

    }

    private fun setAnimaleWhenClickIcon() {

        val animateIcon = { icon: ImageView, motionEvent: MotionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    icon.layoutParams.height = 50
                    icon.layoutParams.width = 50

                    icon.requestLayout()
                }
                MotionEvent.ACTION_MOVE -> {
                }
                MotionEvent.ACTION_UP -> {
                    icon.layoutParams.height = 69
                    icon.layoutParams.width = 69

                    icon.requestLayout()
                }
            }
        }

        menuHomeRL.setOnTouchListener { view, motionEvent ->
            animateIcon(menuHomeIV, motionEvent)
            false
        }

        menuMapRL.setOnTouchListener { view, motionEvent ->
            animateIcon(menuMapIV, motionEvent)
            false
        }

        menuNotifyRL.setOnTouchListener { view, motionEvent ->
            animateIcon(menuNotifyIV, motionEvent)
            false
        }

        menuUserRL.setOnTouchListener { view, motionEvent ->
            animateIcon(menuUserIV, motionEvent)
            false
        }


    }

    private var fragmentCurrent = ""
    private fun addFragment() {
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragment_container, HomeFragment(), "home")
            add(R.id.fragment_container, BookMarkFragment(), "bookmark")
            add(R.id.fragment_container, NotifyFragment(), "notify")
            add(R.id.fragment_container, UserFragment(), "user")
            commitNow()
        }

    }

    private fun changeMenu(menu: String) {
        fragmentCurrent = menu
        val fm = supportFragmentManager

        when (menu) {
            "home" -> {

                if (fm.findFragmentByTag("home") == null) {
                    supportFragmentManager.beginTransaction().apply {
                        add(R.id.fragment_container, HomeFragment(), "home")
                        commitNow()
                    }
                }
                menuHomeIV.setBackgroundResource(R.drawable.ic_home_filled)
                menuMapIV.setBackgroundResource(R.drawable.ic_bookmark)
                menuNotifyIV.setBackgroundResource(R.drawable.ic_notification)
                menuUserIV.setBackgroundResource(R.drawable.ic_user)

                //set color image
                menuHomeIV.background.setTint(ContextCompat.getColor(this, R.color.colorBlack))
                menuMapIV.background.setTint(
                    ContextCompat.getColor(
                        this,
                        R.color.colorWhiteDarkDark
                    )
                )
                menuNotifyIV.background.setTint(
                    ContextCompat.getColor(
                        this,
                        R.color.colorWhiteDarkDark
                    )
                )
                menuUserIV.background.setTint(
                    ContextCompat.getColor(
                        this,
                        R.color.colorWhiteDarkDark
                    )
                )

                //set color text
//                menuHomeTV.setTextColor(themeColor(androidx.appcompat.R.attr.colorAccent))
//                menuSearchTV.setTextColor(ContextCompat.getColor(this, R.color.colorWhiteDarkDark))
//                menuNotifyTV.setTextColor(ContextCompat.getColor(this, R.color.colorWhiteDarkDark))
//                menuUserTV.setTextColor(ContextCompat.getColor(this, R.color.colorWhiteDarkDark))

                //supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()

                supportFragmentManager.beginTransaction().apply {
                    fm.findFragmentByTag("home")?.let { show(it) }
                    fm.findFragmentByTag("bookmark")?.let { hide(it) }
                    fm.findFragmentByTag("notify")?.let { hide(it) }
                    fm.findFragmentByTag("user")?.let { hide(it) }
                    commit()
                }
                Log.i("dsadawd", "THis is Home")
                Log.i("dsadawd", "HomeFragment: " + fm.findFragmentByTag("home"))

            }
            "bookmark" -> {
                if (fm.findFragmentByTag("bookmark") == null) {
                    supportFragmentManager.beginTransaction().apply {
                        add(R.id.fragment_container, BookMarkFragment(), "bookmark")
                        commitNow()
                    }
                }

                menuHomeIV.setBackgroundResource(R.drawable.ic_home)
                menuMapIV.setBackgroundResource(R.drawable.ic_bookmark_filled)
                menuNotifyIV.setBackgroundResource(R.drawable.ic_notification)
                menuUserIV.setBackgroundResource(R.drawable.ic_user)


                menuHomeIV.background.setTint(
                    ContextCompat.getColor(
                        this,
                        R.color.colorWhiteDarkDark
                    )
                )
                menuMapIV.background.setTint(ContextCompat.getColor(this, R.color.colorBlack))
                menuNotifyIV.background.setTint(
                    ContextCompat.getColor(
                        this,
                        R.color.colorWhiteDarkDark
                    )
                )
                menuUserIV.background.setTint(
                    ContextCompat.getColor(
                        this,
                        R.color.colorWhiteDarkDark
                    )
                )

                //supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MapFragment()).commit()


                supportFragmentManager.beginTransaction().apply {
                    fm.findFragmentByTag("home")?.let { hide(it) }
                    fm.findFragmentByTag("bookmark")?.let { show(it) }
                    fm.findFragmentByTag("notify")?.let { hide(it) }
                    fm.findFragmentByTag("user")?.let { hide(it) }
                    commit()
                }


            }
            "notify" -> {

                if (fm.findFragmentByTag("notify") == null) {
                    supportFragmentManager.beginTransaction().apply {
                        add(R.id.fragment_container, NotifyFragment(), "notify")
                        commitNow()
                    }
                }

                menuHomeIV.setBackgroundResource(R.drawable.ic_home)
                menuMapIV.setBackgroundResource(R.drawable.ic_bookmark)
                menuNotifyIV.setBackgroundResource(R.drawable.ic_notification_filled)
                menuUserIV.setBackgroundResource(R.drawable.ic_user)

                menuHomeIV.background.setTint(
                    ContextCompat.getColor(
                        this,
                        R.color.colorWhiteDarkDark
                    )
                )
                menuMapIV.background.setTint(
                    ContextCompat.getColor(
                        this,
                        R.color.colorWhiteDarkDark
                    )
                )
                menuNotifyIV.background.setTint(ContextCompat.getColor(this, R.color.colorBlack))
                menuUserIV.background.setTint(
                    ContextCompat.getColor(
                        this,
                        R.color.colorWhiteDarkDark
                    )
                )

                //supportFragmentManager.beginTransaction().replace(R.id.fragment_container, NotifyFragment()).commit()

                supportFragmentManager.beginTransaction().apply {
                    fm.findFragmentByTag("home")?.let { hide(it) }
                    fm.findFragmentByTag("bookmark")?.let { hide(it) }
                    fm.findFragmentByTag("notify")?.let { show(it) }
                    fm.findFragmentByTag("user")?.let { hide(it) }
                    commit()
                }

            }
            "user" -> {

                if (fm.findFragmentByTag("user") == null) {
                    supportFragmentManager.beginTransaction().apply {
                        add(R.id.fragment_container, UserFragment(), "user")
                        commitNow()
                    }
                }

                menuHomeIV.setBackgroundResource(R.drawable.ic_home)
                menuMapIV.setBackgroundResource(R.drawable.ic_bookmark)
                menuNotifyIV.setBackgroundResource(R.drawable.ic_notification)
                menuUserIV.setBackgroundResource(R.drawable.ic_user_filled)


                menuHomeIV.background.setTint(
                    ContextCompat.getColor(
                        this,
                        R.color.colorWhiteDarkDark
                    )
                )
                menuMapIV.background.setTint(
                    ContextCompat.getColor(
                        this,
                        R.color.colorWhiteDarkDark
                    )
                )
                menuNotifyIV.background.setTint(
                    ContextCompat.getColor(
                        this,
                        R.color.colorWhiteDarkDark
                    )
                )
                menuUserIV.background.setTint(ContextCompat.getColor(this, R.color.colorBlack))

                //supportFragmentManager.beginTransaction().replace(R.id.fragment_container, UserFragment()).commit()

                supportFragmentManager.beginTransaction().apply {
                    fm.findFragmentByTag("home")?.let { hide(it) }
                    fm.findFragmentByTag("bookmark")?.let { hide(it) }
                    fm.findFragmentByTag("notify")?.let { hide(it) }
                    fm.findFragmentByTag("user")?.let { show(it) }
                    commit()
                }
            }
        }
    }

    private val CATEGORY = "category"
    private fun showMenuPostBottomDialog() {
        val menuPostList = ArrayList<BottomSheetMenu.ModelMenuBottomSheet>()
        menuPostList.add(
            BottomSheetMenu.ModelMenuBottomSheet(
                "คำถาม",
                "ตั้งคำถาม เกี่ยวกับปัญหาการเดินทางที่คุณต้องการให้ชาว rupost ช่วยเหลือ",
                R.drawable.ic_question_mark
            )
        )
        menuPostList.add(
            BottomSheetMenu.ModelMenuBottomSheet(
                "เเบ่งปัน",
                "เเบ่งปันเหตุการณ์ต่างๆ เช่น รถติด อุบัติเหตุ น้ำท่วม ให้เพื่อนๆชาว RuPost ได้รับรู้ถึงสถานการณ์ปัจจุบันที่นั่นเพื่อช่วยให้เพื่อนๆ เลือกเส้นทางที่สะดวกรวดเร็วในการเดินทางได้",
                R.drawable.ic_share
            )
        )
        menuPostList.add(
            BottomSheetMenu.ModelMenuBottomSheet(
                "อีเว้นท์",
                "บอกเทศกาลในท้องถิ่น เพื่อเชิญชวนให้เพื่อนๆที่ต้องการเดินทางมาได้รับรู้ถึง สถานการณ์ปัจจุบันที่นั่นว่ามันน่าสนใจเเค่ไหน",
                R.drawable.ic_star
            )
        )

        BottomSheetMenu(this, menuPostList, "สร้างโพสต์", object : BottomSheetMenu.SelectListener {
            override fun onMyClick(m: BottomSheetMenu.ModelMenuBottomSheet, position: Int) {
                when (position) {
                    0 -> {
                        val intent = Intent(this@MainActivity, PostActivity::class.java)
                        intent.putExtra(CATEGORY, "คำถาม")
                        startActivity(intent)
                    }
                    1 -> {
                        val intent = Intent(this@MainActivity, PostActivity::class.java)
                        intent.putExtra(CATEGORY, "เเบ่งปัน")
                        startActivity(intent)
                    }
                    2 -> {
                        val intent = Intent(this@MainActivity, PostActivity::class.java)
                        intent.putExtra(CATEGORY, "อีเว้นท์")
                        startActivity(intent)
                    }
                }
            }
        })

    }

}