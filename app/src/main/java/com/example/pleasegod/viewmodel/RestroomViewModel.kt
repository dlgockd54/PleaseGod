package com.example.pleasegod.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pleasegod.di.DaggerAppComponent
import com.example.pleasegod.model.entity.Restroom
import com.example.pleasegod.model.repository.RestroomRepository
import com.example.pleasegod.observer.DefaultSingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by hclee on 2019-06-30.
 */

class RestroomViewModel : ViewModel() {
    companion object {
        val TAG: String = RestroomViewModel::class.java.simpleName
    }

    val mRestroomLiveData: MutableLiveData<List<Restroom>> = MutableLiveData<List<Restroom>>()
    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable()

    @Inject
    lateinit var mRepository: RestroomRepository

    init {
        DaggerAppComponent.create().inject(this@RestroomViewModel)
    }

    fun getRestroomList(apiKey: String, pageIndex: Int, pageSize: Int, sigunName: String) {
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
                .subscribeWith(object : DefaultSingleObserver<List<Restroom>>() {
                    override fun onSuccess(restroomList: List<Restroom>) {
                        restroomList.let {
                            mRestroomLiveData.postValue(it)
                        }
                    }
                })
        )
    }

    fun clearDisposable() {
        mCompositeDisposable.clear()
    }

    override fun onCleared() {
        super.onCleared()
        clearDisposable()
    }
}