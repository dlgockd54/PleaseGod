package com.example.pleasegod.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pleasegod.R
import com.example.pleasegod.model.entity.Restroom
import kotlinx.android.synthetic.main.item_restroom.view.*

/**
 * Created by hclee on 2019-06-30.
 */

class RestroomListAdapter(private val mContext: Context, private val mRestoomList: MutableList<Restroom>) :
    RecyclerView.Adapter<RestroomListAdapter.RestroomViewHolder>() {
    companion object {
        val TAG: String = RestroomListAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestroomViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.item_restroom, parent, false)

        return RestroomViewHolder(view)
    }

    override fun getItemCount(): Int = mRestoomList.size

    override fun onBindViewHolder(holder: RestroomViewHolder, position: Int) {
        holder.let {
            val restroom: Restroom = mRestoomList[position]

            it.mRestroomNameTextView.text = restroom.pbctlt_plc_nm
            it.mRoadNameAddressTextView.text = restroom.refine_roadnm_addr
            it.mRegularTimeTextView.text = restroom.open_tm_info
        }
    }

    class RestroomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mRestroomNameTextView: TextView = itemView.tv_restroom_name
        val mRoadNameAddressTextView: TextView = itemView.tv_road_name_address
        val mRegularTimeTextView: TextView = itemView.tv_regular_time
    }
}