package com.cmk.app.net

import androidx.paging.PagingSource
import com.cmk.app.vo.*
import com.cmk.app.vo.test.AdminUser
import com.cmk.app.vo.test.GoodsDetailVo
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.*
import okhttp3.ResponseBody


import retrofit2.http.POST


/**
 * @Author: romens
 * @Date: 2019-11-1 15:55
 * @Desc:
 */
interface ApiService {

    companion object {
        const val URL: String = "http://47.105.82.18:8777" //一心堂

        //        const val URL:String = "http://omsclient.yxtmart.cn/"
//        const val URL:String = "https://oms-demo-client.yiyao365.cn/"// 测试
//        const val URL:String = "http://39.107.141.204:8777/"//张仲景
        const val BASE_URL: String = "https://www.wanandroid.com/"
        const val GANK_URL: String = "https://gank.io/api/v2/data/category/Girl/type/"
        const val DOWN_WX: String = "https://dldir1.qq.com/weixin/android/weixin7014android1660.apk"
        const val UPLOAD_URL: String = "https://hn216.api.yesapi.cn/"
        const val UP: String = "https://api.yesapi.cn/"
        const val LOCALHOST_RUL: String = "http://192.168.200.251:8080/"
    }

    fun Retrofit.service(url: String) {

    }

    /**
     *  本地环境
     */
    @GET("getuser/{id}")
    suspend fun localHost(@Path("id") id: String): ApiResponse<AdminUser>
    /**
     *  本地环境
     */
    @GET("getuser/{id}")
    suspend fun localHost1(@Path("id") id: String): Call<ResponseBody>

    /**
     * 本地环境登录
     */
    @FormUrlEncoded
    @POST("login")
    suspend fun localLogin(
        @Field("userName") userName: String,
        @Field("password") password: String
    ): Call<ResponseBody>


    /**
     * 启动页图片
     */
    @GET("Girl/page/1/count/10")
    suspend fun showGirl(): ApiResponse<List<GirlVo.Data>>

    /**
     * 我的动态图片
     */
    @GET()
    suspend fun myDynamic(@Url url: String): ApiResponse<List<GirlVo.Data>>

    /**********************************************************************************************/

    /**
     * 下载
     */
    @GET("ReconciliationDay_ZH-CN7914130812?force=download")
    suspend fun downLoad(): ApiResponse<ResponseBody>

    @Streaming
    @GET("android/weixin7014android1660.apk")
    suspend fun downLoadWX(): ApiResponse<ResponseBody>

    @Streaming
    @GET("Windows/WeChatSetup.exe")
    suspend fun downLoadWX1(): ResponseBody

    @Streaming
    @GET("Windows/WeChatSetup.exe")
    suspend fun downLoadWX2(): Call<ResponseBody>

    @FormUrlEncoded
    @POST("?s=App.User.Login")
    suspend fun upLogin(
//        @Query("sign") sing:String,
//        @Field("app_key") appKey: String?,
        @Field("username") userName: String,
        @Field("password") passWord: String
//        @Query("is_allow_many") may:String,
//        @Query("client")client:String

    ): ApiResponse<UpLogin>

    /**
     * 上传图片 base64  http://open.yesapi.cn/?r=App/Mine
     */
    @POST("?s=App.CDN.UploadImgByBase64")
    suspend fun upLoadImageBase64(
        @Part("app_key") appKey: RequestBody,
        @Part("uuid") uuid: RequestBody,
        @Part("token") token: RequestBody,
        @Part part: MultipartBody.Part,
        @Part("file_name") fileName: RequestBody,
        @Part("file_type") fileType: RequestBody? = null
    ): ApiResponse<UploadImageVo>

    /**
     * 上传图片 base64  http://open.yesapi.cn/?r=App/Mine
     */
    @POST("?s=App.CDN.UploadImgByBase64")
    suspend fun upLoadImageBase64(@Body requestBody: RequestBody): ApiResponse<UploadImageVo>

    /**
     * 上传图片 表单方式
     */
    @Multipart
    @POST("App.CDN.UploadImg")
    fun upLoadImage(
        @Part("app_key") appKey: RequestBody,
        @Part("app_secrect") appSecrect: RequestBody,
        @Part("uuid") uuid: RequestBody,
        @Part("token") token: RequestBody,
        @Part part: MultipartBody.Part
    ): ApiResponse<UploadImageVo>

    /**********************************************************************************************/

    /**
     * 注册
     */
    @FormUrlEncoded
    @POST("user/register")
    suspend fun regedit(
        @Field("username") userName: String, @Field("password") passWord: String, @Field(
            "repassword"
        ) rePassWord: String
    ): ApiResponse<LoginVo>

    /**
     * 登录
     */
    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") userName: String,
        @Field("password") passWord: String
    ): ApiResponse<LoginVo>

    /**
     * 退出
     */
    @GET("user/logout/json")
    suspend fun loginOut(): ApiResponse<Any>

    /**
     * 个人信息
     */
    @GET("user/lg/userinfo/json")
    suspend fun getUserInfo():ApiResponse<UserInfoVo>

    /**
     * 首页banner
     */
    @GET("banner/json")
    suspend fun bannerList(): ApiResponse<List<BannerVo>>

    @GET("banner/json")
    suspend fun bannerList2(): Flow<ApiResponse<List<BannerVo>>>

    /**
     * 文章列表
     */
    @GET("article/list/{page}/json")
    suspend fun articleList(@Path("page") page: Int): ApiResponse<ArticleVo>

//    @GET("article/list/{page}/json")
//    suspend fun articleList1(@Path("page") page: Int): ApiResponse<ArticleVo>

    /**
     * 文章列表,paging3 demo
     */
    @GET("article/list/{page}/json")
    suspend fun articlePagingList(@Path("page") page: Int): ApiResponse<PagingSource.LoadResult<Int, ArticleVo>>

    /**
     * 项目分类
     */
    @GET("project/tree/json")
    suspend fun projectType(): ApiResponse<ProjectTypeVo>

    /**
     * 项目列表
     */
    @GET("project/list/{page}/json")
    suspend fun projectList(
        @Path("page") page: Int,
        @Query("cid") cid: Int
    ): ApiResponse<ProjectListVo>

    /**
     * 收藏
     */
    @POST("lg/collect/{id}/json")
    suspend fun collect(@Path("id") articleId: Int): ApiResponse<String>

    /**
     * 取消收藏
     */
    @POST("lg/uncollect_originId/{id}/json")
    suspend fun unCollect(@Path("id") articleId: Int): ApiResponse<String>

    /**
     * 广场列表
     */
    @GET("user_article/list/{page}/json")
    suspend fun plazaList(@Path("page") page: Int): ApiResponse<PlazaListVo>


    /**
     * 公众号tab
     */
    @GET("wxarticle/chapters/json")
    suspend fun wxTab(): ApiResponse<List<WxTabVo>>

    /**
     * 公众号列表
     */
    @GET("wxarticle/list/{id}/{page}/json")
    suspend fun wxList(@Path("id") id: Int, @Path("page") page: Int): ApiResponse<WxListVo>

    /**
     * 搜索热词
     */
    @GET("hotkey/json")
    suspend fun hotKey(): ApiResponse<List<HotKeyVo>>

    /**
     * 搜索作者
     */
    @GET("article/list/{page}/json")
    suspend fun searchAuthor(
        @Path("page") page: Int,
        @Query("author") author: String
    ): ApiResponse<SearchResultVo.Author>

}