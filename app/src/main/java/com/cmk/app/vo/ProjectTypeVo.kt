package com.cmk.app.vo

/**
 * @Author: romens
 * @Date: 2019-11-25 16:56
 * @Desc:
 */

data class ProjectTypeVo(
    var children: List<Any>,
    var courseId: Int,
    var id: Int,
    var name: String,
    var order: Int,
    var parentChapterId: Int,
    var userControlSetTop: Boolean,
    var visible: Int
)