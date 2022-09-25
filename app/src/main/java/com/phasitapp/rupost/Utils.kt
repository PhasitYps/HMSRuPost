package com.phasitapp.rupost

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.TypedValue
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
}