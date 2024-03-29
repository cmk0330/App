package com.cmk.app.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cmk.app.net.Http
import com.cmk.app.vo.PlazaListVo

/**
 * @Author: romens
 * @Date: 2019-11-27 9:59
 * @Desc:
 */

class PlazaSource : PagingSource<Int, PlazaListVo.DataX>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PlazaListVo.DataX> {
        val page = params.key ?: 0
        return try {
            val data = Http.service.plazaList(page).data
            LoadResult.Page(
                data = data.datas,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (data.over) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PlazaListVo.DataX>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}