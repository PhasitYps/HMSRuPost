package com.phasitapp.rupost.helper


import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.phasitapp.rupost.R
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class OpenWeatherApi(private val activity: Activity) {

    private val TAG = "OpenWeatherApiTag"
    private val API = "a2c401562789fe8a2e058f0d5556019b"
    private var lang = "en"

    fun setLang(lang: String){
        this.lang = lang
//        af Afrikaans
//    al Albanian
//    ar Arabic
//    az Azerbaijani
//    bg Bulgarian
//    ca Catalan
//    cz Czech
//    da Danish
//    de German
//    el Greek
//    en English
//    eu Basque
//    fa Persian (Farsi)
//    fi Finnish
//    fr French
//    gl Galician
//    he Hebrew
//    hi Hindi
//    hr Croatian
//    hu Hungarian
//    id Indonesian
//    it Italian
//    ja Japanese
//    kr Korean
//    la Latvian
//    lt Lithuanian
//    mk Macedonian
//    no Norwegian
//    nl Dutch
//    pl Polish
//    pt Portuguese
//    pt_br Português Brasil
//    ro Romanian
//    ru Russian
//    sv, se Swedish
//    sk Slovak
//    sl Slovenian
//    sp, es Spanish
//    sr Serbian
//    th Thai
//    tr Turkish
//    ua, uk Ukrainian
//    vi Vietnamese
//    zh_cn Chinese Simplified
//    zh_tw Chinese Traditional
//    zu Zulu
    }

    fun getCurrentByLatLng(lat: Double, lng: Double, l:(model: Model)->Unit){
        Thread{
            try{
                Log.i(TAG, "start Api")

                val response = URL("https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lng&units=metric&lang=$lang&appid=$API").readText(Charsets.UTF_8)
                Log.i(TAG, "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lng&units=metric&lang=$lang&appid=$API")

                val jsonObj = JSONObject(response)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                    Date(updatedAt*1000)
                )
                val temp = String.format("%.0f°C", main.getDouble("temp"))
                val tempMin = "Min Temp: %.0f°C".format(main.getDouble("temp_min"))
                val tempMax = "Max Temp: %.0f°C".format(main.getDouble("temp_max"))
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getDouble("speed")
                val weatherDescription = weather.getString("description")
                val address = jsonObj.getString("name")
                val icon = weather.getString("icon")
                //val address = jsonObj.getString("name")+", "+sys.getString("country")



                activity.runOnUiThread {
                    // Stuff that updates the UI

                    var bitmap: Bitmap? = when(icon){
                        "01d" -> BitmapFactory.decodeResource(activity.resources, R.drawable.day_1)
                        "01n" -> BitmapFactory.decodeResource(activity.resources, R.drawable.night_1)
                        "02d" -> BitmapFactory.decodeResource(activity.resources, R.drawable.day_2)
                        "02n" -> BitmapFactory.decodeResource(activity.resources, R.drawable.night_2)
                        "03d" -> BitmapFactory.decodeResource(activity.resources, R.drawable.day_3)
                        "03n" -> BitmapFactory.decodeResource(activity.resources, R.drawable.night_3)
                        "04d" -> BitmapFactory.decodeResource(activity.resources, R.drawable.day_4)
                        "04n" -> BitmapFactory.decodeResource(activity.resources, R.drawable.night_4)
                        "09d" -> BitmapFactory.decodeResource(activity.resources, R.drawable.day_9)
                        "09n" -> BitmapFactory.decodeResource(activity.resources, R.drawable.night_9)
                        "10d" -> BitmapFactory.decodeResource(activity.resources, R.drawable.day_10)
                        "10n" -> BitmapFactory.decodeResource(activity.resources, R.drawable.night_10)
                        "11d" -> BitmapFactory.decodeResource(activity.resources, R.drawable.day_11)
                        "11n" -> BitmapFactory.decodeResource(activity.resources, R.drawable.night_11)
                        "13d" -> BitmapFactory.decodeResource(activity.resources, R.drawable.day_13)
                        "13n" -> BitmapFactory.decodeResource(activity.resources, R.drawable.night_13)
                        "50d" -> BitmapFactory.decodeResource(activity.resources, R.drawable.day_50)
                        "50n" -> BitmapFactory.decodeResource(activity.resources, R.drawable.night_50)
                        else-> null
                    }
                    val model = Model(
                        city = address,
                        temp = temp,
                        description = weatherDescription,
                        iconBitmap = bitmap,
                        wind = "%.0f".format(windSpeed)
                    )
                    l(model)

                }
            }catch (e: Exception){
                Log.i(TAG, "error: ${e.message}")
            }

        }.start()
    }



    open class Model(
        var city: String? = null,
        var temp: String? = null,
        var description: String? = null,
        var iconBitmap: Bitmap? = null,
        var wind: String? = null
    )
}