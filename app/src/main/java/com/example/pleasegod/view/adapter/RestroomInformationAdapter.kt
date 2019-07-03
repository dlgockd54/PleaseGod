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
            view.tv_restroom_info_open_time.text = "개방 시간: ${mRestroom.open_tm_info}"
            view.tv_restroom_info_road_name_address.text = mRestroom.refine_roadnm_addr

            if (mRestroom.male_female_toilet_yn == null) {
                view.tv_restroom_info_male_female_toilet.visibility = View.GONE
            } else {
                view.tv_restroom_info_male_female_toilet.text = "남녀 공용화장실 여부: ${mRestroom.male_female_toilet_yn}"
            }

            if (mRestroom.manage_inst_nm == null) {
                view.tv_restroom_info_manage_inst_name.visibility = View.GONE
            } else {
                view.tv_restroom_info_manage_inst_name.text = "관리기관: ${mRestroom.manage_inst_nm}"
            }

            if (mRestroom.manage_inst_telno == null) {
                view.tv_restroom_info_manage_inst_tel_number.visibility = View.GONE
            } else {
                view.tv_restroom_info_manage_inst_tel_number.text = "전화번호: ${mRestroom.manage_inst_telno}"
            }

            if (mRestroom.male_dspsn_wtrcls_cnt == null) {
                view.tv_restroom_info_male_dspsn_wtrcls_cnt.visibility = View.GONE
            } else {
                view.tv_restroom_info_male_dspsn_wtrcls_cnt.text = "남성용-장애인용 대변기 수: ${mRestroom.male_dspsn_wtrcls_cnt}"
            }

            if (mRestroom.male_dspsn_uil_cnt == null) {
                view.tv_restroom_info_male_dspsn_uil_cnt.visibility = View.GONE
            } else {
                view.tv_restroom_info_male_dspsn_uil_cnt.text = "남성용-장애인용 소변기 수: ${mRestroom.male_dspsn_uil_cnt}"
            }

            if (mRestroom.female_dspsn_wtrcls_cnt == null) {
                view.tv_restroom_info_female_dspsn_wtrcls_cnt.visibility = View.GONE
            } else {
                view.tv_restroom_info_female_dspsn_wtrcls_cnt.text = "여성용-장애인용 대변기 수: ${mRestroom.female_dspsn_wtrcls_cnt}"
            }
        }

        return view!!
    }

    override fun getItem(position: Int): Any = position

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = 1
}