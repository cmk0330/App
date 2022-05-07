package com.cmk.app.vo

data class GirlVo(
    var data: List<Data>,
    var page: Int,
    var page_count: Int,
    var status: Int,
    var total_counts: Int
) {
    data class Data(
        var _id: String,
        var author: String,
        var category: String,
        var createdAt: String,
        var desc: String,
        var images: List<String>,
        var likeCounts: Int,
        var publishedAt: String,
        var stars: Int,
        var title: String,
        var type: String,
        var url: String,
        var views: Int
    )
}