package com.example.pleasegod.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pleasegod.R
import com.example.pleasegod.model.RestroomItem
import com.example.pleasegod.view.adapter.RestroomListAdapter
import com.example.pleasegod.viewmodel.RestroomViewModel
import kotlinx.android.synthetic.main.activity_restroom_list.*

class RestroomListActivity : AppCompatActivity() {
    companion object {
        val TAG: String = RestroomListActivity::class.java.simpleName
    }

    private lateinit var mRestroomViewModel: RestroomViewModel
    private lateinit var mRestroomListAdapter: RestroomListAdapter
    private val mRestroomList: MutableList<RestroomItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restroom_list)

        init()
    }

    private fun init() {
        mRestroomListAdapter = RestroomListAdapter(this, mRestroomList)
        mRestroomViewModel = ViewModelProviders.of(this).get(RestroomViewModel::class.java)
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
