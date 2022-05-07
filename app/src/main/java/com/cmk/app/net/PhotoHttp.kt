package com.cmk.app.net

import com.cmk.app.base.App
import com.cmk.app.util.NetWorkUtils
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import java.io.File

/**
 * @Author: romens
 * @Date: 2019-12-26 10:38
 * @Desc:
 */
object PhotoHttp: HttpClient() {
    val uploadService by lazy { Http.createService(ApiService::class.java, ApiService.UPLOAD_URL) }

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
}