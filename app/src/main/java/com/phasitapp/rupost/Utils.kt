package com.phasitapp.rupost

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.core.app.ActivityCompat
import com.phasitapp.rupost.helper.Prefs
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    fun Context.themeColor(@AttrRes attrRes: Int): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute (attrRes, typedValue, true)
        return typedValue.data
    }

    fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    fun hasUserCurrent(context: Context): Boolean{
        val prefs = Prefs(context)
        return prefs.strOpenId != ""
    }

    fun formatDate(format:String, date: Date): String{
        val formatdate = SimpleDateFormat(format)
        val formatStr = formatdate.format(date)
        return formatStr
    }

    fun convertViewToBitmap(view: View): Bitmap? {
        //Get the dimensions of the view so we can re-layout the view at its current size
        //and create a bitmap of the same size
        val width = view.width
        val height = view.height
        val measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

        //Cause the view to re-layout
        view.measure(measuredWidth, measuredHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        //Create a bitmap backed Canvas to draw the view into
        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)

        //Now that the view is laid out and we have a canvas, ask the view to draw itself into the canvas
        view.draw(c)
        return b
    }

    fun formatCreateDate(createDate: Long): String {
        val currentDate = System.currentTimeMillis()
        val pastTime = currentDate - createDate
        //259200000 = 3 day
        //86,400,000 = 1 day
        //3,600,000 = 1 h
        //60,000 = 1 m
        //1,000 = 1 s
        Log.i("fweewfwe", "pastTime: $pastTime")
        if (pastTime > 259200000) {
            return Utils.formatDate("dd MMM yyyy HH:mm", Date(createDate))
        } else if (pastTime >= 86400000) {
            val day = (pastTime / 86400000).toInt()
            return "$day วัน ที่เเล้ว"
        } else if (pastTime >= 3600000) {
            val h = (pastTime / 3600000).toInt()
            return "$h ชม. ที่เเล้ว"
        } else if (pastTime >= 60000) {
            val m = (pastTime / 60000).toInt()
            return "$m น. ที่เเล้ว"
        } else {
            val s = (pastTime / 1000).toInt()
            return "$s วิ. ที่เเล้ว"
        }
    }



}