package com.phasitapp.rupost.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.phasitapp.rupost.PostActivity
import com.phasitapp.rupost.R
import com.phasitapp.rupost.dialog.BottomSheetMenu
import kotlinx.android.synthetic.main.fragment_user.*


class UserFragment : Fragment(R.layout.fragment_user) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        event()
    }

    private fun event(){

        selectLoginTV.setOnClickListener {
            showMenuBottomDialog()
        }
    }

    private fun showMenuBottomDialog(){
        val menuPostList = ArrayList<BottomSheetMenu.ModelMenuBottomSheet>()
        menuPostList.add(BottomSheetMenu.ModelMenuBottomSheet("หมายเลขโทรศัพท์", "", R.drawable.ic_phone_number))
        menuPostList.add(BottomSheetMenu.ModelMenuBottomSheet("เเบ่งปัน", "เเบ่งปันเหตุการณ์ต่างๆ เช่น รถติด อุบัติเหตุ น้ำท่วม ให้เพื่อนๆชาว RuPost ได้รับรู้ถึงสถานการณ์ปัจจุบันที่นั่นเพื่อช่วยให้เพื่อนๆ เลือกเส้นทางที่สะดวกรวดเร็วในการเดินทางได้", R.drawable.ic_share))
        menuPostList.add(BottomSheetMenu.ModelMenuBottomSheet("อีเว้นท์", "บอกเทศกาลในท้องถิ่น เพื่อเชิญชวนให้เพื่อนๆที่ต้องการเดินทางมาได้รับรู้ถึง สถานการณ์ปัจจุบันที่นั่นว่ามันน่าสนใจเเค่ไหน", R.drawable.ic_star))

        BottomSheetMenu(requireActivity(), menuPostList, "เลือกเมนูล็อกอิน", object: BottomSheetMenu.SelectListener {
            override fun onMyClick(m: BottomSheetMenu.ModelMenuBottomSheet, position: Int) {
                when(position){

                }
            }
        })

    }

}