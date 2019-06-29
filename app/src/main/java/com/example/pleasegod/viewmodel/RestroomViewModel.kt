package com.example.pleasegod.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel

/**
 * Created by hclee on 2019-06-30.
 */

class RestroomViewModel(private val mApplication: Application) : AndroidViewModel(mApplication) {
    companion object {
        val TAG: String = RestroomViewModel::class.java.simpleName
    }

    override fun onCleared() {
        Log.d(TAG, "onCleard()")

        super.onCleared()
    }
}