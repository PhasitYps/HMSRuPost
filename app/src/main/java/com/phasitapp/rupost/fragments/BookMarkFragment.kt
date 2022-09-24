package com.phasitapp.rupost.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.app.ActivityCompat
import com.huawei.hms.maps.*
import com.phasitapp.rupost.R


class BookMarkFragment : Fragment(R.layout.fragment_bookmark)  {


    companion object {
        private const val TAG = "MapFragment"
    }

    private var hMap: HuaweiMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}