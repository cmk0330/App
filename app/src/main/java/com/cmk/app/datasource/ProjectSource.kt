package com.cmk.app.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cmk.app.net.Http
import com.cmk.app.vo.ProjectListVo

/**
 * @Author: romens
 * @Date: 2019-11-26 9:11
 * @Desc:
 */

class ProjectSource : PagingSource<Int, ProjectListVo.DataX>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProjectListVo.DataX> {
        return try {
            val page = params.key ?: 0
            val cid = 294 // 代表完整项目
            val data = Http.service.projectList(page, cid).data
            LoadResult.Page(
                data = data.datas,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (data.over) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ProjectListVo.DataX>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
