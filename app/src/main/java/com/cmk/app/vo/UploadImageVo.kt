package com.cmk.app.vo

data class UploadImageVo(
    val err_code: Int,
    val err_msg: String,
    val id: String,
    val url: String
)

data class UpLogin(
    val err_code: Int,
    val err_msg: String,
    val token: String,
    val uuid: String
)