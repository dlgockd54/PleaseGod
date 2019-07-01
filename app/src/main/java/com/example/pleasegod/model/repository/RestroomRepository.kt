package com.example.pleasegod.model.repository

import com.example.pleasegod.model.ApiManager
import com.example.pleasegod.model.entity.ApiResponse
import com.example.pleasegod.model.source.RestroomApi
import io.reactivex.Single

/**
 * Created by hclee on 2019-07-01.
 */

class RestroomRepository {
    companion object {
        val TAG: String = RestroomRepository::class.java.simpleName
    }

    private val mApi: RestroomApi = ApiManager.mRestroomApi

    fun getRestroomList(
        key: String,
        type: String,
        pageIndex: Int,
        pageSize: Int,
        sigunName: String
    ): Single<ApiResponse> = mApi.getRestroomList(key, type, pageIndex, pageSize, sigunName)
}