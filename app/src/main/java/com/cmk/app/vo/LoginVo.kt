package com.cmk.app.vo

/**
 * @Author: romens
 * @Date: 2019-11-14 9:19
 * @Desc:
 */
data class LoginVo(
    var admin: Boolean,
    var chapterTops: List<String>,
    var collectIds: List<Int>,
    var email: String,
    var icon: String,
    var nickname: String,
    var username: String
)