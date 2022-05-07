package com.cmk.app.datasource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cmk.app.net.Http
import com.cmk.app.vo.WxListVo

/**
 * @Author: romens
 * @Date: 2019-12-2 9:59
 * @Desc:
 */

class WxSource(val id: Int) : PagingSource<Int, WxListVo.DataX>() {

    private var onLoadStopListener: (() -> Unit)? = null
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WxListVo.DataX> {
        val page = params.key ?: 0
        return try {
            val data = Http.service.wxList(id, page).data
            val page1 = LoadResult.Page(
                data = data.datas,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (data.over) null else page + 1
            )
            if (page1.nextKey == null) {
                onLoadStopListener?.invoke()
            }
            Log.e("wxSource", "prevKey-->${page1.prevKey}")
            Log.e("wxSource", "nextKey-->${page1.nextKey}")
            Log.e("wxSource", "itemsAfter-->${page1.itemsAfter}")
            Log.e("wxSource", "itemsBefore-->${page1.itemsBefore}")
            return page1
        } catch (e: Exception) {
            Log.e("wxSource", "Exception")
            LoadResult.Error(e)
        }
    }

    fun setOnLoadStopListener(listener:() -> Unit){
        this.onLoadStopListener = listener
    }

    override fun getRefreshKey(state: PagingState<Int, WxListVo.DataX>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
