package com.phasitapp.rupost.dialog

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phasitapp.rupost.R

class BottomSheetMenuFilter(
    private val activity: Activity,
    private val modelMenu: ArrayList<ModelMenuBottomSheet>,
    private val title: String,
    val itemClick: SelectListener
) {

    init {
        showBottomSheetDialog()
    }

    private fun showBottomSheetDialog() {
        val bottomSheetView: View = activity.layoutInflater.inflate(R.layout.bottom_sheet_menu_filter, null)
        val bottomSheetDialog = BottomSheetDialog(activity, R.style.SheetDialog)
        bottomSheetDialog.setContentView(bottomSheetView)

        var dataRCV = bottomSheetDialog.findViewById<RecyclerView>(R.id.dataRCV)
        var titleTV = bottomSheetView.findViewById<TextView>(R.id.titleTV)

        titleTV.text = title

        /*val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheetView.parent as View)
        val bottomSheetCallback: BottomSheetCallback = object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // do something
                Log.i("Jfkdjkfjdfdf","onStateChanged")
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // do something
                Log.i("Jfkdjkfjdfdf","onSlide")
            }
        }

        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback)*/

        setDataMenuAdapter(dataRCV, bottomSheetDialog)
        bottomSheetDialog.show()

    }

    private fun setDataMenuAdapter(dataRCV: RecyclerView?, bottomSheetDialog: BottomSheetDialog) {
        dataRCV!!.adapter = DataAdapter(bottomSheetDialog)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        dataRCV!!.layoutManager = layoutManager
    }

    inner class DataAdapter(private val bottomSheetDialog: BottomSheetDialog) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_listview_bottomsheet_menu_filter, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val m  = modelMenu[position]

            holder.menunameTV.text = m.menuname

            holder.itemCV.setOnClickListener {
                itemClick.onMyClick(m, position)
                bottomSheetDialog.dismiss()
            }

        }


        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var itemCV: CardView = itemView.findViewById(R.id.itemCV)
            var menunameTV: TextView = itemView.findViewById(R.id.menunameTV)
        }

        override fun getItemCount(): Int {
            return modelMenu.size
        }

    }

    interface SelectListener {
        fun onMyClick(m: ModelMenuBottomSheet, position: Int)
    }

    data class ModelMenuBottomSheet(
        var menuname:String,
    )
}