package com.example.pleasegod.model.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by hclee on 2019-07-01.
 */

class ApiResponse {
    @SerializedName("Publtolt")
    @Expose
    private var publtolt: List<Publtolt>? = null

    fun getPubltolt(): List<Publtolt>? = publtolt

    fun setPubltolt(publtolt: List<Publtolt>) {
        this.publtolt = publtolt
    }
}

class Head {
    @SerializedName("list_total_count")
    @Expose
    private var listTotalCount: Int = 0

    @SerializedName("RESULT")
    @Expose
    private lateinit var rESULT: RESULT

    @SerializedName("api_version")
    @Expose
    private lateinit var apiVersion: String

    fun getListTotalCount(): Int = listTotalCount

    fun setListTotalCount(listTotalCount: Int) {
        this.listTotalCount = listTotalCount
    }

    fun getRESULT(): RESULT = rESULT

    fun setRESULT(rESULT: RESULT) {
        this.rESULT = rESULT
    }

    fun getApiVersion(): String = apiVersion

    fun setApiVersion(apiVersion: String) {
        this.apiVersion = apiVersion;
    }
}

class Publtolt {
    @SerializedName("head")
    @Expose
    private var head: List<Head>? = null

    @SerializedName("row")
    @Expose
    private var row: List<Restroom>? = null

    fun getHead(): List<Head>? = head

    fun setHead(head: List<Head>) {
        this.head = head
    }

    fun getRow(): List<Restroom>? = row

    fun setRow(row: List<Restroom>) {
        this.row = row
    }
}

class RESULT {
    @SerializedName("CODE")
    @Expose
    lateinit var cODE: String
    @SerializedName("MESSAGE")
    @Expose
    lateinit var mESSAGE: String
}