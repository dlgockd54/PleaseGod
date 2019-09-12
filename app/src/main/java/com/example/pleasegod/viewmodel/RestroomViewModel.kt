package com.example.pleasegod.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import com.example.pleasegod.di.DaggerAppComponent
import com.example.pleasegod.model.entity.Restroom
import com.example.pleasegod.model.repository.RestroomRepository
import com.example.pleasegod.observer.DefaultSingleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by hclee on 2019-06-30.
 */

class RestroomViewModel : ViewModel() {
    companion object {
        const val RETRY_MAX: Int = 2
    }

    @Inject
    lateinit var mRepository: RestroomRepository

    private val mCompositeDisposable = CompositeDisposable()
    private var mClickedLocationName: StringBuilder = StringBuilder()
    val mRestroomList: ObservableArrayList<Restroom> = ObservableArrayList<Restroom>()
    var mIsLoadingFinish: ObservableBoolean = ObservableBoolean(false)
    var mClickedLocationIndex: Int = 0

    init {
        DaggerAppComponent.create().inject(this@RestroomViewModel)
    }

    fun requestPublishRestroomList(
        apiKey: String,
        pageIndex: Int,
        pageSize: Int,
        sigunName: String
    ) {
        if (sigunName != mClickedLocationName.toString()) {
            mClickedLocationName.replace(0, mClickedLocationName.length, sigunName)
            mIsLoadingFinish.set(false)
            mCompositeDisposable.add(
                mRepository.getRestroomList(apiKey, "json", pageIndex, pageSize, sigunName)
                    .map { it.getPubltolt()?.get(1)?.getRow() }
                    .subscribeOn(Schedulers.io())
                    .retry { retryCnt, e ->
                        retryCnt < RETRY_MAX
                    }
                    .subscribeWith(object : DefaultSingleObserver<List<Restroom>>() {
                        override fun onSuccess(t: List<Restroom>) {
                            mRestroomList.clear()
                            mRestroomList.addAll(t)
                            mIsLoadingFinish.set(true)
                        }
                    })
            )
        }
    }

    override fun onCleared() {
        mCompositeDisposable.clear()

        super.onCleared()
    }
}