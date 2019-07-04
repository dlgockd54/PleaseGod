package com.example.pleasegod.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pleasegod.R
import com.example.pleasegod.model.entity.Restroom
import com.example.pleasegod.view.adapter.RestroomListAdapter
import com.example.pleasegod.viewmodel.RestroomViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_restroom_list.*
import kotlinx.android.synthetic.main.bottom_sheet_search_view.view.*

class RestroomListActivity : AppCompatActivity() {
    companion object {
        val TAG: String = RestroomListActivity::class.java.simpleName
    }

    private lateinit var mRestroomViewModel: RestroomViewModel
    private lateinit var mRestroomListAdapter: RestroomListAdapter
    private val mRestroomList: MutableList<Restroom> = mutableListOf()
    private lateinit var mBottomSheetDialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restroom_list)

        init()
        getRestroomList()
    }

    private fun init() {
        mRestroomListAdapter = RestroomListAdapter(this, mRestroomList)
        mRestroomViewModel = ViewModelProviders.of(this).get(RestroomViewModel::class.java).apply {
            mRestroomLiveData.observe(this@RestroomListActivity, Observer {
                Log.d(TAG, "mRestroomLiveData changed()")

                mRestroomList.clear()
                mRestroomList.addAll(it)
                mRestroomListAdapter.copyTotalRestroom()
                mRestroomListAdapter.notifyDataSetChanged()
                loading_progress_bar.visibility = View.GONE
            })
        }
        rv_restroom_list.apply {
            layoutManager = LinearLayoutManager(this@RestroomListActivity)
            adapter = mRestroomListAdapter
        }
        fab_restroom_list.apply {
            setOnClickListener {
                Log.d(TAG, "onClick()")

                val bottomSheetView: View =
                    LayoutInflater.from(this@RestroomListActivity).inflate(R.layout.bottom_sheet_search_view, null)
                        .apply {
                            search_view_restroom.apply {
                                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                                    override fun onQueryTextSubmit(query: String?): Boolean {
                                        Log.d(TAG, query)

                                        query?.let {
                                            mRestroomListAdapter.filter.filter(query)
                                        }

                                        mBottomSheetDialog.dismiss()

                                        return true
                                    }

                                    override fun onQueryTextChange(newText: String?): Boolean {
                                        newText?.let {
                                            mRestroomListAdapter.filter.filter(newText)
                                        }

                                        return false
                                    }
                                })
                            }
                        }
                mBottomSheetDialog = BottomSheetDialog(this@RestroomListActivity).apply {
                    setContentView(bottomSheetView)
                    setOnShowListener {
                        Log.d(TAG, "onShow()")
                    }
                }
                val bottomSheetBehavior: BottomSheetBehavior<View> =
                    BottomSheetBehavior.from(bottomSheetView.parent as View).apply {
                        setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                            }

                            override fun onStateChanged(bottomSheet: View, newState: Int) {

                            }
                        })
                    }

                mBottomSheetDialog.show()
            }
        }
    }

    private fun getRestroomList(pageIndex: Int = 1, pageSize: Int = 1000, sigunName: String = "고양시") {
        loading_progress_bar.visibility = View.VISIBLE
        mRestroomViewModel.getRestroomList(pageIndex, pageSize, sigunName)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        finish()
    }
}
