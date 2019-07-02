package com.example.pleasegod.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.pleasegod.R
import com.example.pleasegod.model.entity.Restroom
import kotlinx.android.synthetic.main.item_restroom_information.view.*

/**
 * Created by hclee on 2019-07-02.
 */

class RestroomInformationAdapter(private val mContext: Context, private val mRestroom: Restroom) : BaseAdapter() {
    override fun getView(position: Int, converView: View?, parent: ViewGroup?): View {
        var view: View? = converView

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_restroom_information, parent, false)

            view.tv_restroom_info_name.text = mRestroom.pbctlt_plc_nm
            view.tv_restroom_info_open_time.text = mRestroom.open_tm_info
        }

        return view!!
    }

    override fun getItem(position: Int): Any = position

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = 1
}