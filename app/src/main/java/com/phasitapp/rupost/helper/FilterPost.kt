package com.phasitapp.rupost.helper

import com.phasitapp.rupost.FILTERDAY_3DAY_LAST
import com.phasitapp.rupost.FILTERDAY_7DAY_LAST
import com.phasitapp.rupost.FILTERDAY_TODAY
import com.phasitapp.rupost.FILTERDAY_YESTERDAY
import com.phasitapp.rupost.model.ModelPost
import java.util.*
import kotlin.collections.ArrayList

class FilterPost {

    fun filter(postList: ArrayList<ModelPost>, currentFilterDay: String, currentCategory: String, l:(postsFilter: ArrayList<ModelPost>)->Unit){
        when(currentFilterDay){
            FILTERDAY_TODAY ->{

                val starCal = Calendar.getInstance()
                val startMs = starCal.timeInMillis

                val endCal = Calendar.getInstance()
                endCal.set(Calendar.HOUR_OF_DAY, 0)
                endCal.set(Calendar.MINUTE, 0)
                endCal.set(Calendar.SECOND, 0)
                endCal.set(Calendar.MILLISECOND, 0)
                val endMs = endCal.timeInMillis

                if(currentCategory == "ทั้งหมด"){
                    val postFilter = postList.filter { it.createDate!!.toLong() in (endMs + 1) until startMs } as ArrayList
                    l(postFilter)
                }else{
                    val postFilter = postList.filter { it.createDate!!.toLong() in (endMs + 1) until startMs && it.category == currentCategory} as ArrayList
                    l(postFilter)
                }

            }
            FILTERDAY_YESTERDAY ->{
                val starCal = Calendar.getInstance()
                starCal.add(Calendar.DATE, -1)
                val startMs = starCal.timeInMillis

                val endCal = Calendar.getInstance()
                endCal.add(Calendar.DATE, -2)
                val endMs = endCal.timeInMillis

                if(currentCategory == "ทั้งหมด"){
                    val postFilter = postList.filter { it.createDate!!.toLong() in (endMs + 1) until startMs } as ArrayList
                    l(postFilter)
                }else{
                    val postFilter = postList.filter { it.createDate!!.toLong() in (endMs + 1) until startMs && it.category == currentCategory} as ArrayList
                    l(postFilter)
                }
            }
            FILTERDAY_3DAY_LAST ->{
                val starCal = Calendar.getInstance()
                val startMs = starCal.timeInMillis

                val endCal = Calendar.getInstance()
                endCal.add(Calendar.DATE, -3)
                val endMs = endCal.timeInMillis

                if(currentCategory == "ทั้งหมด"){
                    val postFilter = postList.filter { it.createDate!!.toLong() in (endMs + 1) until startMs } as ArrayList
                    l(postFilter)
                }else{
                    val postFilter = postList.filter { it.createDate!!.toLong() in (endMs + 1) until startMs && it.category == currentCategory} as ArrayList
                    l(postFilter)
                }
            }
            FILTERDAY_7DAY_LAST ->{
                val starCal = Calendar.getInstance()
                val startMs = starCal.timeInMillis

                val endCal = Calendar.getInstance()
                endCal.add(Calendar.DATE, -7)
                val endMs = endCal.timeInMillis

                if(currentCategory == "ทั้งหมด"){
                    val postFilter = postList.filter { it.createDate!!.toLong() in (endMs + 1) until startMs } as ArrayList
                    l(postFilter)
                }else{
                    val postFilter = postList.filter { it.createDate!!.toLong() in (endMs + 1) until startMs && it.category == currentCategory} as ArrayList
                    l(postFilter)
                }
            }
        }
    }

}