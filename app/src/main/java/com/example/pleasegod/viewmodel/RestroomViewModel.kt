package com.example.pleasegod.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pleasegod.di.DaggerAppComponent
import com.example.pleasegod.model.entity.Restroom
import com.example.pleasegod.model.repository.RestroomRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by hclee on 2019-06-30.
 */

class RestroomViewModel : ViewModel() {
    @Inject
    lateinit var mRepository: RestroomRepository

    init {
        DaggerAppComponent.create().inject(this@RestroomViewModel)
    }

    fun getRestroomListSingle(
        apiKey: String,
        pageIndex: Int,
        pageSize: Int,
        sigunName: String
    ): Single<List<Restroom>> =
        mRepository.getRestroomList(apiKey, "json", pageIndex, pageSize, sigunName)
            .subscribeOn(Schedulers.io())
            .map { it.getPubltolt()?.get(1)?.getRow() }
}