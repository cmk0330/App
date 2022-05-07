package com.cmk.app.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import com.cmk.app.base.Repository
import com.cmk.app.config.pagingConfig
import com.cmk.app.datasource.WxSource
import com.cmk.app.net.Http
import com.cmk.app.net.Result
import com.cmk.app.vo.WxListVo
import com.cmk.app.vo.WxTabVo
import kotlinx.coroutines.flow.Flow

/**
 * @Author: romens
 * @Date: 2019-12-2 9:50
 * @Desc:
 */
class WxRepository : Repository() {

    suspend fun wxTab(): Result<List<WxTabVo>> {
        return apiCall(call = { requestWxTab() })
    }

    suspend fun wxList(id: Int, page: Int): Result<WxListVo> {
        return apiCall(call = { requestWxList(id, page) })
    }

    fun getWxList(id: Int): Flow<PagingData<WxListVo.DataX>> {
        val source = WxSource(id)
        source.invalid
        return Pager(config = pagingConfig(), pagingSourceFactory = { source }).flow
    }

    private suspend fun requestWxTab(): Result<List<WxTabVo>> =
        executeResponse(Http.service.wxTab())

    private suspend fun requestWxList(id: Int, page: Int): Result<WxListVo> =
        executeResponse(Http.service.wxList(id, page))
}