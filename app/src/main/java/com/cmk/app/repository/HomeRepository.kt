package com.cmk.app.repository

import androidx.paging.Pager
import com.cmk.app.base.Repository
import com.cmk.app.config.pagingConfig
import com.cmk.app.datasource.ArticleSource
import com.cmk.app.datasource.ProjectSource
import com.cmk.app.net.Http
import com.cmk.app.net.Result
import com.cmk.app.vo.ArticleVo
import com.cmk.app.vo.ProjectListVo
import com.cmk.app.vo.ProjectTypeVo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import javax.inject.Inject

/**
 * @Author: romens
 * @Date: 2019-11-5 10:36
 * @Desc:
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
class HomeRepository @Inject constructor(): Repository() {

    //首页轮播
//    suspend fun getBanners(): Result<List<BannerVo>> {
//        return apiCall(call = { requestBanners() })
//        return apiCall { OldHttp.get().create(ApiService::class.java).bannerList() }
//    }

    // 文章列表
    fun getArticle() =
        Pager(config = pagingConfig(), pagingSourceFactory = { ArticleSource() })

    // 项目列表
    fun getProject() =
        Pager(config = pagingConfig(), pagingSourceFactory = { ProjectSource() })


    // 项目类型
    suspend fun projectType(): Result<ProjectTypeVo> {
        return apiCall(call = { requestProjectType() })
    }

//     suspend fun requestBanners(): ResultBean<List<BannerVo>> =
//        executeResponse(Http.service.bannerList())

    private suspend fun requestArticle(page: Int): Result<ArticleVo> =
        executeResponse(Http.service.articleList(page))

    private suspend fun requestProjectList(page: Int, cid: Int): Result<ProjectListVo> =
        executeResponse(Http.service.projectList(page, cid))

    private suspend fun requestProjectType(): Result<ProjectTypeVo> =
        executeResponse(Http.service.projectType())

}