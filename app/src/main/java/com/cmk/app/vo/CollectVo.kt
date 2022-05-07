package com.cmk.app.vo

/**
 * @Author: romens
 * @Date: 2019-11-13 14:32
 * @Desc:
 */

data class CollectVo(
    var curPage: Int,
    var datas: List<DataX>,
    var offset: Int,
    var over: Boolean,
    var pageCount: Int,
    var size: Int,
    var total: Int
) {
    data class DataX(
        var author: String,
        var chapterId: Int,
        var chapterName: String,
        var courseId: Int,
        var desc: String,
        var envelopePic: String,
        var id: Int,
        var link: String,
        var niceDate: String,
        var origin: String,
        var originId: Int,
        var publishTime: Long,
        var title: String,
        var userId: Int,
        var visible: Int,
        var zan: Int
    )
}