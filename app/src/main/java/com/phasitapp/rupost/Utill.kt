package com.phasitapp.rupost

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes

object Utill {

    fun Context.themeColor(@AttrRes attrRes: Int): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute (attrRes, typedValue, true)
        return typedValue.data
    }
}