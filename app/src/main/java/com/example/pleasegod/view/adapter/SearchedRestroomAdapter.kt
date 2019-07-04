package com.example.pleasegod.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pleasegod.R
import com.example.pleasegod.model.entity.Restroom
import kotlinx.android.synthetic.main.item_restroom_searched.view.*

/**
 * Created by hclee on 2019-07-04.
 */

class SearchedRestroomAdapter(
    private val mContext: Context,
    val mRestroomList: MutableList<Restroom>,
    private val mRestroomClickListener: RestroomClickListener
) :
    RecyclerView.Adapter<SearchedRestroomAdapter.SearchedRestroomViewHolder>() {
    companion object {
        val TAG: String = SearchedRestroomAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchedRestroomViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.item_restroom_searched, parent, false)

        return SearchedRestroomViewHolder(view)
    }

    override fun getItemCount(): Int = mRestroomList.size

    override fun onBindViewHolder(holder: SearchedRestroomViewHolder, position: Int) {
        holder.let {
            it.mRestroomNameTextView.text = mRestroomList[position].pbctlt_plc_nm
            it.mRestroomRoadNameAddressTextView.text = mRestroomList[position].refine_roadnm_addr
            it.itemView.setOnClickListener {
                mRestroomClickListener.onRestroomClick(mRestroomList[position])
            }
        }
    }

    class SearchedRestroomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mRestroomNameTextView: TextView = itemView.tv_searched_restroom_name
        val mRestroomRoadNameAddressTextView: TextView = itemView.tv_searched_restroom_road_name_address
    }

    interface RestroomClickListener {
        fun onRestroomClick(restroom: Restroom)
    }
}