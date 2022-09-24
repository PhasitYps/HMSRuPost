package com.phasitapp.rupost


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.huawei.hms.maps.MapsInitializer
import com.phasitapp.rupost.dialog.BottomSheetMenu
import com.phasitapp.rupost.fragments.HomeFragment
import com.phasitapp.rupost.fragments.NotifyFragment
import com.phasitapp.rupost.fragments.BookMarkFragment
import com.phasitapp.rupost.fragments.UserFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(this)
        setContentView(R.layout.activity_main)
        Log.d("sadasdas", "MainActivity onCreate: ")


        addFragment()
        changeMenu("home")

        event()
        setAnimaleWhenClickIcon()
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

    private fun setAnimaleWhenClickIcon(){

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
    private fun addFragment(){
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
                menuHomeIV.setBackgroundResource(R.drawable.ic_home_filled)
                menuMapIV.setBackgroundResource(R.drawable.ic_bookmark)
                menuNotifyIV.setBackgroundResource(R.drawable.ic_notification)
                menuUserIV.setBackgroundResource(R.drawable.ic_user)

                //set color image
                menuHomeIV.background.setTint(ContextCompat.getColor(this, R.color.colorBlack))
                menuMapIV.background.setTint(ContextCompat.getColor(this, R.color.colorWhiteDarkDark))
                menuNotifyIV.background.setTint(ContextCompat.getColor(this, R.color.colorWhiteDarkDark))
                menuUserIV.background.setTint(ContextCompat.getColor(this, R.color.colorWhiteDarkDark))

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
                menuHomeIV.setBackgroundResource(R.drawable.ic_home)
                menuMapIV.setBackgroundResource(R.drawable.ic_bookmark_filled)
                menuNotifyIV.setBackgroundResource(R.drawable.ic_notification)
                menuUserIV.setBackgroundResource(R.drawable.ic_user)


                menuHomeIV.background.setTint(ContextCompat.getColor(this, R.color.colorWhiteDarkDark))
                menuMapIV.background.setTint(ContextCompat.getColor(this, R.color.colorBlack))
                menuNotifyIV.background.setTint(ContextCompat.getColor(this, R.color.colorWhiteDarkDark))
                menuUserIV.background.setTint(ContextCompat.getColor(this, R.color.colorWhiteDarkDark))

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
                menuHomeIV.setBackgroundResource(R.drawable.ic_home)
                menuMapIV.setBackgroundResource(R.drawable.ic_bookmark)
                menuNotifyIV.setBackgroundResource(R.drawable.ic_notification_filled)
                menuUserIV.setBackgroundResource(R.drawable.ic_user)

                menuHomeIV.background.setTint(ContextCompat.getColor(this, R.color.colorWhiteDarkDark))
                menuMapIV.background.setTint(ContextCompat.getColor(this, R.color.colorWhiteDarkDark))
                menuNotifyIV.background.setTint(ContextCompat.getColor(this, R.color.colorBlack))
                menuUserIV.background.setTint(ContextCompat.getColor(this, R.color.colorWhiteDarkDark))

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
                menuHomeIV.setBackgroundResource(R.drawable.ic_home)
                menuMapIV.setBackgroundResource(R.drawable.ic_bookmark)
                menuNotifyIV.setBackgroundResource(R.drawable.ic_notification)
                menuUserIV.setBackgroundResource(R.drawable.ic_user_filled)


                menuHomeIV.background.setTint(ContextCompat.getColor(this, R.color.colorWhiteDarkDark))
                menuMapIV.background.setTint(ContextCompat.getColor(this, R.color.colorWhiteDarkDark))
                menuNotifyIV.background.setTint(ContextCompat.getColor(this, R.color.colorWhiteDarkDark))
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
    private fun showMenuPostBottomDialog(){
        val menuPostList = ArrayList<BottomSheetMenu.ModelMenuBottomSheet>()
        menuPostList.add(BottomSheetMenu.ModelMenuBottomSheet("คำถาม", "ตั้งคำถาม เกี่ยวกับปัญหาการเดินทางที่คุณต้องการให้ชาว rupost ช่วยเหลือ", R.drawable.ic_question_mark))
        menuPostList.add(BottomSheetMenu.ModelMenuBottomSheet("เเบ่งปัน", "เเบ่งปันเหตุการณ์ต่างๆ เช่น รถติด อุบัติเหตุ น้ำท่วม ให้เพื่อนๆชาว RuPost ได้รับรู้ถึงสถานการณ์ปัจจุบันที่นั่นเพื่อช่วยให้เพื่อนๆ เลือกเส้นทางที่สะดวกรวดเร็วในการเดินทางได้", R.drawable.ic_share))
        menuPostList.add(BottomSheetMenu.ModelMenuBottomSheet("อีเว้นท์", "บอกเทศกาลในท้องถิ่น เพื่อเชิญชวนให้เพื่อนๆที่ต้องการเดินทางมาได้รับรู้ถึง สถานการณ์ปัจจุบันที่นั่นว่ามันน่าสนใจเเค่ไหน", R.drawable.ic_star))

        BottomSheetMenu(this, menuPostList, "สร้างโพสต์", object: BottomSheetMenu.SelectListener {
            override fun onMyClick(m: BottomSheetMenu.ModelMenuBottomSheet, position: Int) {
                when(position){
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