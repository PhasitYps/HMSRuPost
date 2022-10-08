package com.phasitapp.rupost.helper

import android.app.Activity
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class GeocodingApi(private var activity: Activity) {

    private val key = "DAEDAAKnuLAxWrziHR3AcRMvuKUBgtbKkUR%2FIQN1U6Nbc89O2pF%2FvCJw1IRW5sj%2BIIhOwp30kEOPq6CdUo5fyqWIfeBkjDHy0lxmGg%3D%3D"
    private val url = "https://siteapi.cloud.huawei.com/mapApi/v1/siteService/reverseGeocode?key=$key"

    fun getAddressByLatLng(lat: Double, lng: Double, l:(address: String)->Unit){

        Thread{

            val jsonObject = JSONObject("{\n" +
                    "    \"location\": {\n" +
                    "        \"lng\": $lng,\n" +
                    "        \"lat\": $lat\n" +
                    "    },\n" +
                    "    \"language\": \"th\",\n" +
                    "    \"radius\": 10\n" +
                    "}")

            val client = OkHttpClient()
            val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
            // put your json here
            // put your json here
            val body = RequestBody.create(JSON, jsonObject.toString())
            val request: Request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            var response: Response? = null
            try {
                response = client.newCall(request).execute()
                val resStr = response.body!!.string()
                Log.i("dawfaf", "resStr: " + resStr)


                val json = JSONObject(resStr)
                val address = json.getJSONArray("sites").getJSONObject(0).getString("formatAddress")
                l(address)

                Log.i("dawfaf", "address: " + address)

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }.start()

    }
}