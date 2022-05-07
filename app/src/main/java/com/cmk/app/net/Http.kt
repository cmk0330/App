package com.cmk.app.net

import android.annotation.SuppressLint
import com.cmk.app.base.App
import com.cmk.app.util.NetWorkUtils
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.lang.Exception
import java.security.SecureRandom
import javax.net.ssl.*
import javax.security.cert.CertificateException
import javax.security.cert.X509Certificate
import okhttp3.ConnectionSpec

import okhttp3.TlsVersion
import java.util.*


/**
 * @Author: 崔明坤
 * @Date: 2019-11-14 13:47
 * @Desc:
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
object Http : HttpClient() {

    val service by lazy { createService(ApiService::class.java, ApiService.BASE_URL) }
    val gankService by lazy { createService(ApiService::class.java, ApiService.GANK_URL) }
    val downService by lazy { createService(ApiService::class.java, ApiService.DOWN_WX) }
    val uploadService by lazy { createService(ApiService::class.java, ApiService.UPLOAD_URL) }
    val upService by lazy { createService(ApiService::class.java, ApiService.UP) }
    val localhost by lazy {createService(ApiService::class.java, ApiService.LOCALHOST_RUL)}


    private val cookieJar by lazy {
        PersistentCookieJar(
            SetCookieCache(),
            SharedPrefsCookiePersistor(App.context)
        )
    }

    override fun okHttpClientConfig(builder: OkHttpClient.Builder) {
        super.okHttpClientConfig(builder)
        val httpCacheDirectory = File(App.context.cacheDir, "responses")
        val cacheSize = 10 * 1024 * 1024L // 10 MiB
        val cache = Cache(httpCacheDirectory, cacheSize)
        builder.cookieJar(cookieJar)
            .cache(cache)
            .addInterceptor { chain ->
                var request = chain.request()
                if (!NetWorkUtils.isNetworkAvailable(App.context)) {
                    request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build()
                }
                val response = chain.proceed(request)
                if (!NetWorkUtils.isNetworkAvailable(App.context)) {
                    val maxAge = 60 * 60
                    response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=$maxAge")
                        .build()
                } else {
                    val maxStale = 60 * 60 * 24 * 28 // tolerate 4-weeks stale
                    response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                        .build()
                }

                response
            }
    }

    /**
     * 有网时候的缓存
     */
    private fun getNetCacheInterceptor() =
        Interceptor {
            val request = it.request()
            val response = it.proceed(request)
            val onlineCacheTime = 30//在线的时候的缓存过期时间，如果想要不缓存，直接时间设置为0
            response.newBuilder()
                .header("Cache-Control", "public, max-age=$onlineCacheTime")
                .removeHeader("Pragma")
                .build()
        }

    /**
     * 没有网时候的缓存
     */
    private fun getOfflineCacheInterceptor() =
        Interceptor {
            val request = it.request()
            if (!NetWorkUtils.isNetworkAvailable(App.context)) {
                val offlineCacheTime = 60 //离线的时候的缓存的过期时间
                request.newBuilder()
//                    .cacheControl(CacheControl
//                                .Builder()
//                                .maxStale(60, TimeUnit.SECONDS)
//                                .onlyIfCached()
//                                .build()) 两种方式结果是一样的，写法不同
                    .header(
                        "Cache-Control",
                        "public, only-if-cached, max-stale=$offlineCacheTime"
                    )
                    .build()

            }
            it.proceed(request)
        }
//
//    override fun retrofitConfig(builder: Retrofit.Builder) {
//        super.retrofitConfig(builder)
//        builder.baseUrl(BASE_URL)
//    }

    @Provides
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}