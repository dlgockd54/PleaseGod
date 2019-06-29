package com.example.pleasegod.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.pleasegod.R
import com.example.pleasegod.viewmodel.RestroomViewModel

class RestroomListActivity : AppCompatActivity() {
    companion object {
        val TAG: String = RestroomListActivity::class.java.simpleName
    }

    private lateinit var mRestroomViewModel: RestroomViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restroom_list)

        init()
    }

    private fun init() {
        mRestroomViewModel = ViewModelProviders.of(this).get(RestroomViewModel::class.java)
    }
}
