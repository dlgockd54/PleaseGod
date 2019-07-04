package com.example.pleasegod.view.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pleasegod.R
import com.example.pleasegod.model.entity.Restroom
import com.example.pleasegod.view.RestroomMapActivity
import kotlinx.android.synthetic.main.item_restroom.view.*

/**
 * Created by hclee on 2019-06-30.
 */

class RestroomListAdapter(private val mActivity: Activity, private val mRestroomList: MutableList<Restroom>) :
    RecyclerView.Adapter<RestroomListAdapter.RestroomViewHolder>(), Filterable {
    companion object {
        val TAG: String = RestroomListAdapter::class.java.simpleName
        val INTENT_KEY: String = "selected_restroom"
    }

    private var mTotalRestroomList: MutableList<Restroom> = mutableListOf()

    fun copyTotalRestroom() {
        mTotalRestroomList.addAll(mRestroomList)

        Log.d(TAG, "mTotalRestroomList.size: ${mTotalRestroomList.size}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestroomViewHolder {
        val view: View =
            LayoutInflater.from(mActivity.applicationContext).inflate(R.layout.item_restroom, parent, false)

        return RestroomViewHolder(view)
    }

    override fun getItemCount(): Int = mRestroomList.size

    override fun onBindViewHolder(holder: RestroomViewHolder, position: Int) {
        mRestroomList[position].let { restroom ->
            holder.mRestroomNameTextView.text = restroom.pbctlt_plc_nm
            holder.mRoadNameAddressTextView.text = restroom.refine_roadnm_addr
            holder.mRegularTimeTextView.text = restroom.open_tm_info
            holder.itemView.setOnClickListener {
                Log.d(TAG, "onClick()")

                val intent: Intent = Intent(mActivity, RestroomMapActivity::class.java).apply {
                    putExtra(INTENT_KEY, restroom.refine_roadnm_addr)
                }

                mActivity.startActivity(intent)
                mActivity.overridePendingTransition(R.anim.animation_slide_from_right, R.anim.animation_slide_to_left)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val query: String = charSequence.toString()

                Log.d(TAG, "query: $query")

                var filteredList: MutableList<Restroom> = mutableListOf()
                val filterResults: FilterResults = FilterResults()

                Log.d(TAG, "mTotalRestroomList.size: ${mTotalRestroomList.size}")

                if (query.isEmpty()) {
                    filteredList = mTotalRestroomList
                } else {
                    for (restroom in mTotalRestroomList) {
                        if (restroom.pbctlt_plc_nm != null) {
                            if (restroom.pbctlt_plc_nm.contains(query)) {
                                filteredList.add(restroom)
                            }
                        } else if (restroom.refine_roadnm_addr != null) {
                            if (restroom.refine_roadnm_addr.contains(query)) {
                                filteredList.add(restroom)
                            }
                        } else if (restroom.pbctlt_plc_nm == null) {
                            Log.d(TAG, "name is null!")
                        } else if (restroom.refine_roadnm_addr == null) {
                            Log.d(TAG, "road addr is null!")
                        }
                    }
                }

                Log.d(TAG, "filteredList size: ${filteredList.size}")

                filterResults.count = filteredList.size
                filterResults.values = filteredList

                return filterResults
            }

            override fun publishResults(charSequence: CharSequence?, results: FilterResults?) {
                results?.values?.let {
                    mRestroomList.clear()
                    mRestroomList.addAll(it as MutableList<Restroom>)

                    notifyDataSetChanged()
                }
            }
        }
    }

    class RestroomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mRestroomNameTextView: TextView = itemView.tv_restroom_name
        val mRoadNameAddressTextView: TextView = itemView.tv_road_name_address
        val mRegularTimeTextView: TextView = itemView.tv_regular_time
    }
}