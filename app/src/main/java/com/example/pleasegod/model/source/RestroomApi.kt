package com.example.pleasegod.model.source

import com.example.pleasegod.model.entity.ApiResponse
import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by hclee on 2019-07-01.
 */

interface RestroomApi {
    @GET("Publtolt")
    fun getRestroomList(
        @Query("KEY") key: String, @Query("Type") type: String, @Query("pIndex") pageIndex: Int, @Query(
            "pSize"
        ) pageSize: Int, @Query("SIGUN_NM") sigunName: String
    ): Maybe<ApiResponse>
}