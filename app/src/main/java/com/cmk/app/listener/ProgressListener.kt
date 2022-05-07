package com.cmk.app.listener

/**
 * @Author: romens
 * @Date: 2019-12-24 9:21
 * @Desc:
 */
interface ProgressListener {


    fun onProgress(
        progress: Long,
        total: Long,
        done: Boolean
    )
}