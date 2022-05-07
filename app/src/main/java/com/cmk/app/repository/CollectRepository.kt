package com.cmk.app.repository

import com.cmk.app.base.Repository
import com.cmk.app.net.Http
import com.cmk.app.net.Result

/**
 * @Author: romens
 * @Date: 2019-11-13 14:13
 * @Desc:
 */
class CollectRepository : Repository() {

    suspend fun collect(id: Int): Result<String> {
        return apiCall(call = { requestCollect(id) })
    }

    suspend fun unCollect(id: Int): Result<String> {
        return apiCall(call = { requestUnCollect(id) })
    }

    private suspend fun requestCollect(id: Int): Result<String> =
        executeResponse(Http.service.collect(id))

    private suspend fun requestUnCollect(id: Int): Result<String> =
        executeResponse(Http.service.unCollect(id))

}