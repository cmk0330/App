package com.cmk.app.repository

import androidx.paging.Pager
import com.cmk.app.base.App
import com.cmk.app.base.Repository
import com.cmk.app.config.pagingConfig
import com.cmk.app.datasource.DynamicSource
import com.cmk.app.net.Http
import com.cmk.app.net.Result
import com.cmk.app.util.Preference
import com.cmk.app.vo.LoginVo
import com.google.gson.Gson

/**
 * @Author: romens
 * @Date: 2019-12-3 16:26
 * @Desc:
 */
class MineRepository : Repository() {

    private var isLogin by Preference(Preference.IS_LOGIN, false)
    private var userGson by Preference(Preference.USER_GSON, "")

    suspend fun login(name: String, pass: String): Result<LoginVo> {
        return apiCall(call = { requestLogin(name, pass) })
    }

    suspend fun regedit(name: String, pass: String, rePass: String): Result<LoginVo> {
        return apiCall(call = { requestRegedit(name, pass, rePass) })
    }

    suspend fun loginOut(): Result<Any> {
        return apiCall(call = { requestLoginOut() }, errorMsg = "退出错误--》")
    }

    fun getDynamic() =
        Pager(config = pagingConfig(), pagingSourceFactory = { DynamicSource() }).flow

    private suspend fun requestLogin(name: String, pass: String): Result<LoginVo> {
        val response = Http.service.login(name, pass)
        return executeResponse(response, {
            val data = response.data
            userGson = Gson().toJson(data)
            isLogin = true
            App.CURRENT_USER = data
        })
    }

    private suspend fun requestRegedit(
        name: String,
        pass: String,
        rePass: String
    ): Result<LoginVo> {
        val response = Http.service.regedit(name, pass, rePass)
        return executeResponse(response, { requestLogin(name, pass) }, {})
    }

    private suspend fun requestLoginOut(): Result<Any> = executeResponse(
        Http.service.loginOut(),
        {
            userGson = ""
            isLogin = false
        }
    )

}