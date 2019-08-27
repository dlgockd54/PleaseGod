package com.example.pleasegod.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.pleasegod.R
import com.example.pleasegod.databinding.ItemRestroomBinding
import com.example.pleasegod.model.entity.Restroom
import com.example.pleasegod.observer.DefaultObserver
import com.example.pleasegod.view.RestroomMapActivity
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.item_restroom.view.*
import java.util.concurrent.TimeUnit

/**
 * Created by hclee on 2019-06-30.
 */

class RestroomListAdapter(
    private val mActivity: Activity,
    private val mGlideRequestManager: RequestManager
) : RecyclerView.Adapter<RestroomListAdapter.RestroomViewHolder>(), Filterable {
    companion object {
        val TAG: String = RestroomListAdapter::class.java.simpleName
        val INTENT_KEY: String = "selected_restroom"

        @JvmStatic
        @BindingAdapter("bind:item")
        fun bindItem(recyclerView: RecyclerView, restroomList: ObservableArrayList<Restroom>) {
            recyclerView.adapter?.let {
                (it as RestroomListAdapter).setRestroomList(restroomList)
            }
        }
    }

    private val mRestroomList: MutableList<Restroom> = mutableListOf()
    private var mTotalRestroomList: MutableList<Restroom> = mutableListOf()
    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable()

    private fun setRestroomList(restroomList: List<Restroom>) {
        mRestroomList.clear()
        mRestroomList.addAll(restroomList)

        copyTotalRestroom()
        notifyDataSetChanged()
    }

    private fun copyTotalRestroom() {
        mTotalRestroomList.clear()
        mTotalRestroomList.addAll(mRestroomList)
    }

    fun restoreTotalRestroomData() {
        setRestroomList(mTotalRestroomList)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestroomViewHolder {
        val binding: ItemRestroomBinding =
            ItemRestroomBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return RestroomViewHolder(binding)
    }

    override fun getItemCount(): Int = mRestroomList.size

    override fun onBindViewHolder(holder: RestroomViewHolder, position: Int) {
        mRestroomList[position].let { restroom ->
            holder.bind(restroom)

            mCompositeDisposable.add(
                holder.itemView.clicks()
                    .debounce(200, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DefaultObserver<Unit>() {
                        override fun onNext(t: Unit) {
                            val intent: Intent = Intent(mActivity, RestroomMapActivity::class.java).apply {
                                putExtra(INTENT_KEY, restroom.refine_roadnm_addr)
                            }

                            mActivity.startActivity(intent)
                            mActivity.overridePendingTransition(
                                R.anim.animation_slide_from_right,
                                R.anim.animation_slide_to_left
                            )
                        }
                    })
            )
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val query: String = charSequence.toString()

                return FilterResults().apply {
                    if (query.isEmpty()) {
                        count = mTotalRestroomList.size
                        values = mTotalRestroomList
                    } else {
                        mTotalRestroomList.filter {
                            (it.pbctlt_plc_nm != null && it.pbctlt_plc_nm.contains(query)) ||
                                    (it.refine_roadnm_addr != null && it.refine_roadnm_addr.contains(query))
                        }.let {
                            count = it.size
                            values = it
                        }
                    }
                }
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

    fun clearDisposable() {
        mCompositeDisposable.clear()
    }

    inner class RestroomViewHolder(val binding: ItemRestroomBinding) : RecyclerView.ViewHolder(binding.root) {
        val mPrayImageView: ImageView by lazy {
            itemView.iv_pray
        }
        val mRestroomNameTextView: TextView by lazy {
            itemView.tv_restroom_name
        }
        val mRoadNameAddressTextView: TextView by lazy {
            itemView.tv_road_name_address
        }
        val mRegularTimeTextView: TextView by lazy {
            itemView.tv_regular_time
        }

        fun bind(restroom: Restroom) {
            mGlideRequestManager
                .load(R.drawable.pray)
                .into(mPrayImageView)

            binding.restroom = restroom
        }
    }
}