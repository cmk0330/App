package com.cmk.app.vo.test

data class GoodsDetailVo(
    val goodsId: Long,
    val goodsName: String,
    val goodsIntro: String,
    val goodsCoverImg: String,
    val goodsCarouselList: Array<String>,
    val sellingPrice: Int,
    val originalPrice: Int,
    val goodsDetailContent: String
)