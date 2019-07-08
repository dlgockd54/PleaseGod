package com.example.pleasegod.model.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by hclee on 2019-06-30.
 */

data class Restroom(
    @SerializedName("DATA_STD_DE")
    @Expose
    val data_std_de: String,

    @SerializedName("SIGUN_NM")
    @Expose
    val signu_nm: String,

    @SerializedName("SIGUN_CD")
    @Expose
    val sigun_cd: String,

    @SerializedName("PUBLFACLT_DIV_NM")
    @Expose
    val publifaclt_div_nm: String,

    @SerializedName("PBCTLT_PLC_NM")
    @Expose
    val pbctlt_plc_nm: String,

    @SerializedName("MALE_FEMALE_TOILET_YN")
    @Expose
    val male_female_toilet_yn: String,

    @SerializedName("MALE_WTRCLS_CNT")
    @Expose
    val male_wtrcls_cnt: Int,

    @SerializedName("MALE_UIL_CNT")
    @Expose
    val male_uil_cnt: Int,

    @SerializedName("MALE_DSPSN_WTRCLS_CNT")
    @Expose
    val male_dspsn_wtrcls_cnt: Int,

    @SerializedName("MALE_DSPSN_UIL_CNT")
    @Expose
    val male_dspsn_uil_cnt: Int,

    @SerializedName("MALE_CHILDUSE_WTRCLS_CNT")
    @Expose
    val male_childuse_wtrcls_cnt: Int,

    @SerializedName("MALE_CHILDUSE_UIL_CNT")
    @Expose
    val male_childuse_uil_cnt: Int,

    @SerializedName("FEMALE_WTRCLS_CNT")
    @Expose
    val female_wtrcls_cnt: Int,

    @SerializedName("FEMALE_DSPSN_WTRCLS_CNT")
    @Expose
    val female_dspsn_wtrcls_cnt: Int,

    @SerializedName("FEMALE_CHILDUSE_WTRCLS_CNT")
    @Expose
    val female_childuse_wtrcls_cnt: Int,

    @SerializedName("MANAGE_INST_NM")
    @Expose
    val manage_inst_nm: String,

    @SerializedName("MANAGE_INST_TELNO")
    @Expose
    val manage_inst_telno: String,

    @SerializedName("OPEN_TM_INFO")
    @Expose
    val open_tm_info: String,

    @SerializedName("INSTL_YY")
    @Expose
    val instl_yy: Any,

    @SerializedName("REFINE_LOTNO_ADDR")
    @Expose
    val refine_lotno_addr: String,

    @SerializedName("REFINE_ROADNM_ADDR")
    @Expose
    val refine_roadnm_addr: String?,

    @SerializedName("REFINE_ZIP_CD")
    @Expose
    val refine_zip_cd: String,

    @SerializedName("REFINE_WGS84_LOGT")
    @Expose
    val refine_wgs84_logt: String?,

    @SerializedName("REFINE_WGS84_LAT")
    @Expose
    val refine_wgs84_lat: String?
)