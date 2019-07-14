package com.example.pleasegod.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pleasegod.model.entity.Restroom
import com.example.pleasegod.model.repository.RestroomRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by hclee on 2019-06-30.
 */

class RestroomViewModel : ViewModel() {
    companion object {
        val TAG: String = RestroomViewModel::class.java.simpleName
    }

    val mRestroomLiveData: MutableLiveData<List<Restroom>> = MutableLiveData<List<Restroom>>()
    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable()
    private val mRepository: RestroomRepository = RestroomRepository()

    fun getRestroomList(apiKey: String, pageIndex: Int, pageSize: Int, sigunName: String) {
        Log.d(TAG, "getRestroomList()")

        mCompositeDisposable.add(
            mRepository.getRestroomList(
                apiKey,
                "json",
                pageIndex,
                pageSize,
                sigunName
            )
                .map {
                    it.getPubltolt()?.get(1)?.getRow()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ restroomList ->
                    Log.d(TAG, "onSuccess()")
                    Log.d(TAG, "restroomList size: ${restroomList?.size}")

                    restroomList?.let {
                        mRestroomLiveData.postValue(it)
                    }
                }, {
                    Log.d(TAG, "onError()")
                    Log.d(TAG, it.message)
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