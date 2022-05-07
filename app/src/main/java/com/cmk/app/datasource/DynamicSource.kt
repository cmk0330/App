package com.cmk.app.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cmk.app.net.Http
import com.cmk.app.vo.GirlVo

class DynamicSource : PagingSource<Int, GirlVo.Data>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GirlVo.Data> {
        val page = params.key ?: 0
        val loadSize = params.loadSize
        return try {
            val data = Http.gankService.myDynamic("Girl/page/$page/count/$loadSize").data
            DynamicSource().invalid
            LoadResult.Page(
                data = data,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (data.size < loadSize) null else page + 1
            )

        } catch (e:Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GirlVo.Data>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}