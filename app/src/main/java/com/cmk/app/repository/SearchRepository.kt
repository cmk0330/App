package com.cmk.app.repository

import androidx.paging.DataSource
import androidx.paging.Pager
import com.cmk.app.base.App
import com.cmk.app.base.Repository
import com.cmk.app.config.pagingConfig
import com.cmk.app.datasource.SearchSource
import com.cmk.app.net.ERROR
import com.cmk.app.net.Http
import com.cmk.app.net.Result
import com.cmk.app.repository.db.AppDataBase
import com.cmk.app.vo.HotKeyVo
import com.cmk.app.vo.SearchResultVo

/**
 * @Author: romens
 * @Date: 2019-12-17 15:01
 * @Desc:
 */
class SearchRepository : Repository() {

    /**
     * 热词
     */
    suspend fun hotKey(): Result<List<HotKeyVo>> {
        return apiCall(call = { requestHotKey() }, errorMsg = ERROR)
    }

    /**
     * 所搜作者文章
     */
    suspend fun searchAuthor(page: Int, author: String): Result<SearchResultVo.Author> {
        return apiCall(call = { requestSearchAuthor(page, author) }, errorMsg = ERROR)
    }

    fun getSearchAuthor(author: String) =
        Pager(config = pagingConfig(), pagingSourceFactory = { SearchSource(author) }).flow

    private suspend fun requestHotKey(): Result<List<HotKeyVo>> =
        executeResponse(Http.service.hotKey())

    private suspend fun requestSearchAuthor(
        page: Int,
        author: String
    ): Result<SearchResultVo.Author> =
        executeResponse(Http.service.searchAuthor(page, author))

    fun getHistoryAll(): DataSource.Factory<Int, SearchResultVo.History> =
        AppDataBase.getInstance(App.context).SearchHistoryDao().getSearchHistoryAll()

    fun insertHistory(vo: SearchResultVo.History): Long =
        AppDataBase.getInstance(App.context).SearchHistoryDao().insertHistory(vo)

    fun deleteHistory(vo: SearchResultVo.History): Int =
        AppDataBase.getInstance(App.context).SearchHistoryDao().deleteHistory(vo)

    fun delegeHistoryAll(vo: List<SearchResultVo.History>): Int =
        AppDataBase.getInstance(App.context).SearchHistoryDao().deleteHistory(vo)

}