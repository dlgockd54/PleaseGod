package com.example.pleasegod.model

import com.example.pleasegod.model.source.RestroomApi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by hclee on 2019-07-01.
 */

object ApiManager {
    private val mApiAdapter: Retrofit = Retrofit.Builder()
        .baseUrl("https://openapi.gg.go.kr/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
    val mRestroomApi: RestroomApi = mApiAdapter.create(RestroomApi::class.java)
}