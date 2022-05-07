package com.cmk.app.repository

import com.cmk.app.base.Repository
import com.cmk.app.net.ERROR
import com.cmk.app.net.Http
import okhttp3.ResponseBody

/**
 * @Author: romens
 * @Date: 2019-12-19 10:06
 * @Desc:
 */
class DownUpRepository: Repository() {

    suspend fun downLoad():com.cmk.app.net.Result<ResponseBody> {
      return apiCall(call = {requestDownLoad()}, errorMsg = ERROR)
    }

    private suspend fun requestDownLoad() :com.cmk.app.net.Result<ResponseBody> = executeResponse(Http.downService.downLoadWX())
}