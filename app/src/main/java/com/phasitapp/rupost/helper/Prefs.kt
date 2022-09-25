package com.phasitapp.rupost.helper

import android.content.Context
import android.content.SharedPreferences

class Prefs (private var context: Context) {

    private var preferences: SharedPreferences = context.getSharedPreferences("Setting", Context.MODE_PRIVATE)

    private val APP_PREF_STR_UID = "strUid"
    private val APP_PREF_STR_USERNAME = "strUsername"
    private val APP_PREF_STR_EMAIL = "strEmail"
    private val APP_PREF_STR_OPEN_ID = "strOpenId"
    private val APP_PREF_STR_PHOTO_URI = "strPhotoUri"

    var strUid: String?
        get() = preferences.getString(APP_PREF_STR_UID, "")
        set(value) = preferences.edit().putString(APP_PREF_STR_UID, value).apply()

    var strUsername: String?
        get() = preferences.getString(APP_PREF_STR_USERNAME, "")
        set(value) = preferences.edit().putString(APP_PREF_STR_USERNAME, value).apply()

    var strEmail: String?
        get() = preferences.getString(APP_PREF_STR_EMAIL, "")
        set(value) = preferences.edit().putString(APP_PREF_STR_EMAIL, value).apply()

    var strOpenId: String?
        get() = preferences.getString(APP_PREF_STR_OPEN_ID, "")
        set(value) = preferences.edit().putString(APP_PREF_STR_OPEN_ID, value).apply()

    var strPhotoUri: String?
        get() = preferences.getString(APP_PREF_STR_PHOTO_URI, "")
        set(value) = preferences.edit().putString(APP_PREF_STR_PHOTO_URI, value).apply()



}