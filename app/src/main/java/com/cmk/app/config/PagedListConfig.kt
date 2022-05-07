package com.cmk.app.config

import androidx.paging.PagingConfig


/**
 * @Author: romens
 * @Date: 2019-11-27 9:57
 * @Desc:
 */

fun pagingConfig(): PagingConfig {
    return PagingConfig(
        pageSize = 24,
        initialLoadSize = 24,
        prefetchDistance = 3,
        enablePlaceholders = false

    )
}

val pagingConfig = PagingConfig(
    // 每页显示的数据的大小
    pageSize = 16,

    // 开启占位符
    enablePlaceholders = false,

    // 预刷新的距离，距离最后一个 item 多远时加载数据
    prefetchDistance = 3,

    /**
     * 初始化加载数量，默认为 pageSize * 3
     *
     * internal const val DEFAULT_INITIAL_PAGE_MULTIPLIER = 3
     * val initialLoadSize: Int = pageSize * DEFAULT_INITIAL_PAGE_MULTIPLIER
     */
    initialLoadSize = 16,

    /**
     * 一次应在内存中保存的最大数据
     * 这个数字将会触发，滑动加载更多的数据
     */
    maxSize = 200
)
