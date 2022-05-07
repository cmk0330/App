package com.cmk.app.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cmk.app.base.App
import com.cmk.app.net.Http
import com.cmk.app.repository.db.AppDataBase
import com.cmk.app.vo.ArticleVo

/**
 * @Author: romens
 * @Date: 2019-11-12 10:02
 * @Desc:
 */

class ArticleSource : PagingSource<Int, ArticleVo.DataX>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleVo.DataX> {
        val page = params.key ?: 0
        return try {
            val data = Http.service.articleList(page).data
//            val dao = AppDataBase.getInstance(App.context).HomeDao()
//            val insertData = dao.insertData(data)
//            val articleData = dao.getArticleData()

            LoadResult.Page(
                data = data.datas,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (data.over) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ArticleVo.DataX>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
