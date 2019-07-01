package com.example.pleasegod.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.pleasegod.R
import com.example.pleasegod.model.entity.Restroom
import com.example.pleasegod.model.repository.RestroomRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by hclee on 2019-06-30.
 */

class RestroomViewModel(private val mApplication: Application) : AndroidViewModel(mApplication) {
    companion object {
        val TAG: String = RestroomViewModel::class.java.simpleName
    }

    val mRestroomLiveData: MutableLiveData<List<Restroom>> = MutableLiveData<List<Restroom>>()
    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable()
    private val mRepository: RestroomRepository = RestroomRepository()

    fun getRestroomList(pageIndex: Int, pageSize: Int, sigunName: String) {
        Log.d(TAG, "getRestroomList()")

        mCompositeDisposable.add(
            mRepository.getRestroomList(
                mApplication.getString(R.string.api_key),
                "json",
                pageIndex,
                pageSize,
                sigunName
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d(TAG, "onSuccess()")
                    Log.d(TAG, "restroomList size: ${it.getPubltolt()?.get(1)?.getRow()?.size}")

                    it.getPubltolt()?.get(1)?.getRow().let { restroomList ->
                        mRestroomLiveData.postValue(restroomList)
                    }
                }, {
                    Log.d(TAG, "onError()")
                    Log.d(TAG, it.message)
                }, {
                    Log.d(TAG, "onComplete()")
                    Log.d(TAG, "returned value is null")
                })
        )
    }

    fun clearDisposable() {
        mCompositeDisposable.clear()
    }

    override fun onCleared() {
        Log.d(TAG, "onCleared()")

        super.onCleared()
        clearDisposable()
    }
}