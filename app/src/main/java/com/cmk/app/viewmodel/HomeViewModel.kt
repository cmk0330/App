package com.cmk.app.viewmodel

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.cmk.app.base.BaseViewModel
import com.cmk.app.config.pagingConfig
import com.cmk.app.datasource.ArticleSource
import com.cmk.app.datasource.Paging3Source
import com.cmk.app.datasource.ProjectSource
import com.cmk.app.net.*
import com.cmk.app.vo.BannerVo
import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * @Author: romens
 * @Date: 2019-11-1 16:46
 * @Desc:
 */
class HomeViewModel  : BaseViewModel() {
    private val _articleScrollState = MutableStateFlow(false)
    val articleScrollState = _articleScrollState.asStateFlow()
    private val _projectScrollState = MutableStateFlow(false)
    val projectScrollState = _projectScrollState.asStateFlow()
    private val _bannerState: MutableStateFlow<List<BannerVo>> = MutableStateFlow(emptyList())
    val bannerState = _bannerState.asStateFlow()
    private val _collectState = MutableStateFlow(false)
    val collectState = _collectState.asStateFlow()
    val pager by lazy {
        Pager(config = PagingConfig(
            pageSize = 10,
            initialLoadSize = 20,
            prefetchDistance = 1,
            enablePlaceholders = false
        ), initialKey = 0, pagingSourceFactory = { Paging3Source() }).flow
//
    }

    fun setArticleScrollState(state: Boolean) {
        _articleScrollState.value = state
    }

    fun setProjectScrollState(state: Boolean) {
        _projectScrollState.value = state
    }

    fun localhostLogin() {
        val url = "http://192.168.200.251:8080/getuser?id=1"
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .get() //默认就是GET请求，可以不写
            .build()
        val call: okhttp3.Call = okHttpClient.newCall(request)
        call.enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("localhost", "onFailure: ")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                Log.e("localhost", "onResponse: " + response.body!!.string())
            }
        })
    }

    /**
     * 轮播
     */
    fun loadBanners() {
        launch {
            flow { emit(Http.service.bannerList()) }
                .onStart { }
                .onCompletion { }
                .onCatch()
                .onCollect {
                    success {
                        _bannerState.value = it
                    }
                    error { errorCode, errorMsg ->
                        Log.e("api1bannerError-->", "$errorCode--$errorMsg")
                    }
                }
        }
    }

    /**
     * 文章列表
     */
    fun getArticle() =
        Pager(config = pagingConfig(), pagingSourceFactory = { ArticleSource() })

    /**
     * 项目列表
     */
    fun getProject() =
        Pager(config = pagingConfig(), pagingSourceFactory = { ProjectSource() })

    /**
     * 收藏
     */
    fun collect(id: Int) {
        launch {
            flow { emit(Http.service.collect(id)) }
                .onCatch()
                .onCollect {
                    success { _collectState.value = true }
                    error { errorCode, errorMsg ->
                        Log.e(
                            "HomeViewModel-->",
                            "collectError:[$errorCode:$errorMsg]"
                        )
                    }
                }
        }
    }

    /**
     * 取消收藏
     */
    fun unCollect(id: Int) {
        launch {
            flow { emit(Http.service.unCollect(id)) }
                .onCatch()
                .onCollect {
                    success { _collectState.value = false }
                    error { errorCode, errorMsg ->
                        Log.e(
                            "HomeViewModel-->",
                            "collectError:[$errorCode:$errorMsg]"
                        )
                    }
                }
        }
    }
}