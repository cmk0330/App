package com.cmk.app.vo

import com.cmk.app.base.App
import com.cmk.app.ext.px2dp
import com.cmk.app.ext.screenWidth

data class ImageSize(
    var width: Int = App.context.px2dp(screenWidth),
    var height: Int = App.context.px2dp(screenWidth) * 16 / 9 + App.context.px2dp(screenWidth)
)
