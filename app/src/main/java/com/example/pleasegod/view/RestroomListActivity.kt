package com.example.pleasegod.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pleasegod.R

class RestroomListActivity : AppCompatActivity() {
    companion object {
        val TAG: String = RestroomListActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restroom_list)
    }
}
