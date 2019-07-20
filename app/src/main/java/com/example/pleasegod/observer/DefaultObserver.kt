package com.example.pleasegod.observer

import android.util.Log
import io.reactivex.observers.DisposableObserver

/**
 * Created by hclee on 2019-07-20.
 */

abstract class DefaultObserver<T> : DisposableObserver<T>() {
    companion object {
        val TAG: String = DefaultObserver::class.java.simpleName
    }

    override fun onError(e: Throwable) {
        Log.d(TAG, "onError()")
        Log.d(TAG, e.message)
    }

    override fun onComplete() {
        Log.d(TAG, "onComplete()")
    }
}
