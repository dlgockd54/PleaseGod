package com.example.pleasegod.observer

import android.util.Log
import io.reactivex.observers.DisposableSingleObserver

/**
 * Created by hclee on 2019-07-20.
 */

abstract class DefaultSingleObserver<T> : DisposableSingleObserver<T>() {
    companion object {
        val TAG: String = DefaultSingleObserver::class.java.simpleName
    }

    override fun onError(e: Throwable) {
        Log.d(TAG, "onError()")
        Log.d(TAG, e.message)
    }
}