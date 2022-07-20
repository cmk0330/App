package com.cmk.app.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cmk.app.net.Http
import com.cmk.app.vo.ArticleVo


class Paging3Source : PagingSource<Int, ArticleVo.DataX>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleVo.DataX> {

        val page = params.key ?: 0
        return try {
            val data = Http.service.articleList(page).data
            LoadResult.Page(
                data = data.datas,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (data.over) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
            //distributionUrl=https\://services.gradle.org/distributions/gradle-6.5-bin.zip
        }

    }

    override fun getRefreshKey(state: PagingState<Int, ArticleVo.DataX>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}