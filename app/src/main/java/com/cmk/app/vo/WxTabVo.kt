package com.cmk.app.vo

/**
 * @Author: romens
 * @Date: 2019-12-2 9:40
 * @Desc:
 */

data class WxTabVo(

    var children: List<Tag>,
    var courseId: Int,
    var id: Int,
    var name: String,
    var order: Int,
    var parentChapterId: Int,
    var userControlSetTop: Boolean,
    var visible: Int
) {

    data class Tag(
        var name: String,
        var url: String
    )
}
