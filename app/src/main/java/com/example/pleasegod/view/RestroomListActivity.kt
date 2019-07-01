package com.example.pleasegod.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pleasegod.R
import com.example.pleasegod.model.entity.Restroom
import com.example.pleasegod.view.adapter.RestroomListAdapter
import com.example.pleasegod.viewmodel.RestroomViewModel
import kotlinx.android.synthetic.main.activity_restroom_list.*

class RestroomListActivity : AppCompatActivity() {
    companion object {
        val TAG: String = RestroomListActivity::class.java.simpleName
    }

    private lateinit var mRestroomViewModel: RestroomViewModel
    private lateinit var mRestroomListAdapter: RestroomListAdapter
    private val mRestroomList: MutableList<Restroom> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restroom_list)

        init()
        mRestroomViewModel.getRestroomList(1, 100, "고양시")
    }

    private fun init() {
        mRestroomListAdapter = RestroomListAdapter(this, mRestroomList)
        mRestroomViewModel = ViewModelProviders.of(this).get(RestroomViewModel::class.java).apply {
            mRestroomLiveData.observe(this@RestroomListActivity, Observer {
                Log.d(TAG, "mRestroomLiveData changed()")

                mRestroomList.clear()
                mRestroomList.addAll(it)
                mRestroomListAdapter.notifyDataSetChanged()
            })
        }
        rv_restroom_list.apply {
            layoutManager = LinearLayoutManager(this@RestroomListActivity)
            adapter = mRestroomListAdapter
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        finish()
    }
}
